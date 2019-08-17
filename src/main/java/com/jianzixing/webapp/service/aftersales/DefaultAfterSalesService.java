package com.jianzixing.webapp.service.aftersales;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.order.OrderPayStatus;
import com.jianzixing.webapp.service.order.OrderStatus;
import com.jianzixing.webapp.service.payment.PaymentFlow;
import com.jianzixing.webapp.service.payment.PaymentModeInterface;
import com.jianzixing.webapp.service.payment.PaymentRefundType;
import com.jianzixing.webapp.service.payment.PaymentTransactionType;
import com.jianzixing.webapp.service.refund.RefundFrom;
import com.jianzixing.webapp.service.refund.RefundType;
import com.jianzixing.webapp.service.support.SupportServiceType;
import com.jianzixing.webapp.tables.aftersales.*;
import com.jianzixing.webapp.tables.logistics.TableLogisticsCompany;
import com.jianzixing.webapp.tables.order.TableOrder;
import com.jianzixing.webapp.tables.order.TableOrderGoods;
import com.jianzixing.webapp.tables.order.TableOrderGoodsProperty;
import com.jianzixing.webapp.tables.payment.TablePaymentRefund;
import com.jianzixing.webapp.tables.payment.TablePaymentTransaction;
import com.jianzixing.webapp.tables.refund.TableRefundOrder;
import com.jianzixing.webapp.tables.system.TableAdmin;
import com.jianzixing.webapp.tables.user.TableUser;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelBuilder;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.mimosaframework.orm.AutoResult;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.orm.transaction.Transaction;
import org.mimosaframework.orm.transaction.TransactionCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class DefaultAfterSalesService implements AfterSalesService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addAfterSales(ModelObject object) throws ModuleException, ModelCheckerException, TransactionException {
        object.setObjectClass(TableAfterSales.class);
        List<ModelObject> images = object.getArray("images");
        object.remove("images");


        // 以下参数为必须参数
        int type = object.getIntValue(TableAfterSales.type);
        long orderGoodsId = object.getLongValue(TableAfterSales.orderGoodsId);
        long userId = object.getLongValue(TableAfterSales.userId);
        int amount = object.getIntValue(TableAfterSales.amount);
        if (amount <= 0) {
            throw new ModuleException("after_sale_amount", "售后商品数量必须大于零");
        }

        if (type != AfterSalesType.REFUND.getCode()
                && type != AfterSalesType.EXCHANGE.getCode()
                && type != AfterSalesType.REPAIR.getCode()) {
            throw new ModuleException("not_support_type", "不支持的售后类型");
        }

        ModelObject orderGoods = sessionTemplate.get(Criteria.query(TableOrderGoods.class)
                .eq(TableOrderGoods.id, orderGoodsId));
        if (orderGoods != null) {
            long orderId = orderGoods.getLongValue(TableOrderGoods.orderId);
            object.put(TableAfterSales.orderId, orderId);

            List<ModelObject> olds = sessionTemplate.list(Criteria.query(TableAfterSales.class)
                    .eq(TableAfterSales.orderId, orderId)
                    .eq(TableAfterSales.orderGoodsId, orderGoodsId));
            if (olds != null) {
                for (ModelObject old : olds) {
                    int oldType = old.getIntValue(TableAfterSales.type);
                    int status = old.getIntValue(TableAfterSales.status);
                    if (status != AfterSalesStatus.INIT.getCode()
                            && status != AfterSalesStatus.FINISH.getCode()
                            && status != AfterSalesStatus.CANCEL.getCode()) {
                        throw new ModuleException("status_error_nf", "您当前还有未完成的售后单");
                    }
                    if (oldType == AfterSalesType.REFUND.getCode()
                            && status == AfterSalesStatus.FINISH.getCode()) {
                        throw new ModuleException("status_error_f", "当前订单商品已经退货");
                    }
                }
            }

            object.put(TableAfterSales.number, getOrderNumber());
            ModelObject order = GlobalService.orderService.getSimpleOrderById(orderId);
            if (order == null) {
                throw new ModuleException("order_not_exist", "售后订单不存在");
            }
            if (order.getLongValue(TableOrder.userId) != userId) {
                throw new ModuleException("user_not_you", "售后订单必须本人提交");
            }

            ModelObject supports = GlobalService.supportService.getAfterSalesByOrder(order, orderGoods.getLongValue(TableOrderGoods.goodsId));
            if (type == AfterSalesType.REFUND.getCode()
                    && supports.getIntValue(SupportServiceType.SALES_RETURN.name()) != 1) {
                throw new ModuleException("support_expire", "商品售后已过期");
            }
            if (type == AfterSalesType.EXCHANGE.getCode()
                    && supports.getIntValue(SupportServiceType.EXCHANGE_GOODS.name()) != 1) {
                throw new ModuleException("support_expire", "商品售后已过期");
            }
            if (type == AfterSalesType.REPAIR.getCode()
                    && supports.getIntValue(SupportServiceType.MAINTAIN.name()) != 1) {
                throw new ModuleException("support_expire", "商品售后已过期");
            }

            object.put(TableAfterSales.status, AfterSalesStatus.START.getCode());
            ModelObject goods = GlobalService.orderService.getOrderGoodsById(orderGoodsId);
            if (goods == null) {
                throw new ModuleException("order_goods_empty", "售后商品未找到");
            }
            if (orderId != goods.getLongValue(TableOrderGoods.orderId)) {
                throw new ModuleException("order_goods_not_rel", "售后商品不在当前订单中");
            }

            object.put(TableAfterSales.createTime, new Date());
            object.checkAndThrowable();
            ModelObject orderGoodsUpdate = new ModelObject(TableOrderGoods.class);
            orderGoodsUpdate.put(TableOrderGoods.id, orderGoodsId);
            orderGoodsUpdate.put(TableOrderGoods.afterSaleType, type);

            sessionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean invoke(Transaction transaction) throws Exception {

                    sessionTemplate.save(object);
                    sessionTemplate.update(orderGoodsUpdate);

                    if (images != null) {
                        for (ModelObject img : images) {
                            if (img != null && StringUtils.isNotBlank(img.getString(TableAfterSalesImages.fileName))) {
                                img.setObjectClass(TableAfterSalesImages.class);
                                img.put(TableAfterSalesImages.asid, object.getLongValue(TableAfterSales.id));
                                sessionTemplate.save(img);
                            }
                        }
                    }
                    addProgress(object.getLongValue(TableAfterSales.id), 0,
                            "您的售后服务单" + object.getString(TableAfterSales.number) + "创建成功");
                    return true;
                }
            });
        } else {
            throw new ModuleException("not_found_order_goods", "不存在的订单商品");
        }
    }

    @Override
    public Paging getAfterSales(ModelObject search, int type, long start, long limit) {
        Query query = Criteria.query(TableAfterSales.class);
        if (type > 0) {
            query.in(TableAfterSales.type, type);
        } else {
            if (search != null && search.isNotEmpty("type")) {
                query.eq(TableAfterSales.type, search.getIntValue("type"));
            }
        }
        query.limit(start, limit);
        query.order(TableAfterSales.id, false);
        query.subjoin(TableUser.class).eq(TableUser.id, TableAfterSales.userId).single();
        query.subjoin(TableAfterSalesImages.class).eq(TableAfterSalesImages.asid, TableAfterSales.id);
        query.subjoin(TableOrder.class).eq(TableOrder.id, TableAfterSales.orderId).single();
        query.subjoin(TableOrderGoods.class).eq(TableOrderGoods.id, TableAfterSales.orderGoodsId).single();
        query.subjoin(TableOrderGoodsProperty.class).eq(TableOrderGoodsProperty.orderId, TableAfterSales.orderId);
        query.subjoin(TableAfterSalesAddress.class).eq(TableAfterSalesAddress.asid, TableAfterSales.id);

        if (search != null) {
            if (search.isNotEmpty("orderNumber")) {
                ModelObject order = GlobalService.orderService.getSimpleOrderByNumber(search.getString("orderNumber"));
                if (order != null) {
                    query.eq(TableAfterSales.orderId, order.getLongValue(TableOrder.id));
                } else {
                    return null;
                }
            }
            if (search.isNotEmpty("number")) {
                query.eq(TableAfterSales.number, search.getString("number"));
            }
            if (search.isNotEmpty("userName")) {
                ModelObject user = GlobalService.userService.getUser(search.getString("userName"), null, null);
                if (user != null) {
                    query.eq(TableAfterSales.userId, user.getLongValue(TableUser.id));
                } else {
                    return null;
                }
            }
            if (search.isNotEmpty("status")) {
                query.eq(TableAfterSales.status, search.getIntValue("status"));
            }
            if (search.isNotEmpty("createTimeStart")) {
                query.gte(TableAfterSales.createTime, search.getString("createTimeStart"));
            }
            if (search.isNotEmpty("createTimeEnd")) {
                query.lte(TableAfterSales.createTime, search.getString("createTimeEnd"));
            }
        }

        return sessionTemplate.paging(query);
    }

    @Override
    public Paging getAfterSaleProgress(long asid, long start, long limit) {
        return sessionTemplate.paging(
                Criteria.query(TableAfterSalesProgress.class)
                        .eq(TableAfterSalesProgress.asid, asid)
                        .subjoin(TableAdmin.class).eq(TableAdmin.id, TableAfterSalesProgress.adminId).single().query()
                        .limit(start, limit)
        );
    }

    @Override
    public void addAfterSalesByOrder(ModelObject object) throws ModuleException, ModelCheckerException, TransactionException {
        long orderId = object.getLongValue(TableAfterSales.orderId);
        ModelObject order = GlobalService.orderService.getSimpleOrderById(orderId);
        object.put(TableAfterSales.userId, order.getLongValue(TableOrder.userId));

        this.addAfterSales(object);
    }

    @Override
    public void addProgress(long asid, long adminId, String detail) {
        ModelObject object = sessionTemplate.get(TableAfterSales.class, asid);
        if (object != null) {
            ModelObject save = new ModelObject(TableAfterSalesProgress.class);
            save.put(TableAfterSalesProgress.asid, asid);
            save.put(TableAfterSalesProgress.number, object.getString(TableAfterSales.number));
            save.put(TableAfterSalesProgress.status, object.getIntValue(TableAfterSales.status));
            save.put(TableAfterSalesProgress.adminId, adminId);
            save.put(TableAfterSalesProgress.detail, detail);
            save.put(TableAfterSalesProgress.createTime, new Date());
            sessionTemplate.save(save);
        }
    }

    @Override
    public void cancelAfterSales(long id) throws ModuleException {
        ModelObject as = sessionTemplate.get(TableAfterSales.class, id);
        this.cancelAfterSales(as);
    }

    private void cancelAfterSales(ModelObject as) throws ModuleException {
        if (as != null) {
            long id = as.getLongValue(TableAfterSales.id);
            int status = as.getIntValue(TableAfterSales.status);
            if (status >= AfterSalesStatus.SEND.getCode()) {
                throw new ModuleException(StockCode.STATUS_ERROR, "用户已发货的售后单不允许取消");
            }
            ModelObject object = new ModelObject(TableAfterSales.class);
            object.put(TableAfterSales.id, id);
            object.put(TableAfterSales.status, AfterSalesStatus.CANCEL.getCode());
            sessionTemplate.update(object);

            this.addProgress(id, 0, "售后单已经被取消");
        }
    }

    @Override
    public void auditPass(long id, String realName, String phone, String address) throws ModuleException {
        ModelObject as = sessionTemplate.get(TableAfterSales.class, id);
        if (as != null) {
            int status = as.getIntValue(TableAfterSales.status);
            if (status != AfterSalesStatus.START.getCode()) {
                throw new ModuleException(StockCode.STATUS_ERROR, "状态必须是新建才能审核");
            }
            ModelObject object = new ModelObject(TableAfterSales.class);
            object.put(TableAfterSales.id, id);
            object.put(TableAfterSales.status, AfterSalesStatus.START_SUCCESS.getCode());

            ModelObject addressObj = new ModelObject(TableAfterSalesAddress.class);
            addressObj.put(TableAfterSalesAddress.asid, id);
            addressObj.put(TableAfterSalesAddress.type, 1);
            addressObj.put(TableAfterSalesAddress.realName, realName);
            addressObj.put(TableAfterSalesAddress.phoneNumber, phone);
            addressObj.put(TableAfterSalesAddress.address, address);
            addressObj.put(TableAfterSalesAddress.createTime, new Date());

            sessionTemplate.saveAndUpdate(addressObj);
            sessionTemplate.update(object);

            this.addProgress(id, 0, "售后单审核已通过");
        }
    }

    @Override
    public void auditRefused(long id) throws ModuleException {
        ModelObject as = sessionTemplate.get(TableAfterSales.class, id);
        if (as != null) {
            int status = as.getIntValue(TableAfterSales.status);
            if (status != AfterSalesStatus.START.getCode()) {
                throw new ModuleException(StockCode.STATUS_ERROR, "状态必须是新建才能审核");
            }
            ModelObject object = new ModelObject(TableAfterSales.class);
            object.put(TableAfterSales.id, id);
            object.put(TableAfterSales.status, AfterSalesStatus.START_REFUSED.getCode());
            sessionTemplate.update(object);

            this.addProgress(id, 0, "售后单审核被拒绝");
        }
    }

    @Override
    public ModelObject getAfterSalesById(long id) {
        Query query = Criteria.query(TableAfterSales.class).eq(TableAfterSales.id, id);
        query.subjoin(TableUser.class).eq(TableUser.id, TableAfterSales.userId).single();
        query.subjoin(TableAfterSalesImages.class).eq(TableAfterSalesImages.asid, TableAfterSales.id);
        query.subjoin(TableAfterSalesAddress.class).eq(TableAfterSalesAddress.asid, TableAfterSales.id);
        query.subjoin(TableOrder.class).eq(TableOrder.id, TableAfterSales.orderId).single();
        query.subjoin(TableOrderGoods.class).eq(TableOrderGoods.id, TableAfterSales.orderGoodsId).single();
        query.subjoin(TableOrderGoodsProperty.class).eq(TableOrderGoodsProperty.orderId, TableAfterSales.orderId);
        ModelObject as = sessionTemplate.get(query);
        return as;
    }

    @Override
    public ModelObject getSimpleAfterSalesById(long id) {
        Query query = Criteria.query(TableAfterSales.class).eq(TableAfterSales.id, id);
        ModelObject as = sessionTemplate.get(query);
        return as;
    }

    @Override
    public void sureCheckGoodsFailure(long id) throws ModuleException {
        ModelObject as = sessionTemplate.get(TableAfterSales.class, id);
        if (as != null) {
            int status = as.getIntValue(TableAfterSales.status);
            if (status != AfterSalesStatus.GET.getCode()) {
                throw new ModuleException(StockCode.STATUS_ERROR, "卖家收货后才能验货");
            }
            ModelObject object = new ModelObject(TableAfterSales.class);
            object.put(TableAfterSales.id, id);
            object.put(TableAfterSales.status, AfterSalesStatus.GET_REFUSED.getCode());
            sessionTemplate.update(object);

            this.addProgress(id, 0, "卖家验货失败");
        }
    }

    @Override
    public void setGetGoods(long id) throws ModuleException {
        ModelObject as = sessionTemplate.get(TableAfterSales.class, id);
        if (as != null) {
            int status = as.getIntValue(TableAfterSales.status);
            if (status != AfterSalesStatus.SEND.getCode()) {
                throw new ModuleException(StockCode.STATUS_ERROR, "等待买家发货后才能收货");
            }
            ModelObject object = new ModelObject(TableAfterSales.class);
            object.put(TableAfterSales.id, id);
            object.put(TableAfterSales.status, AfterSalesStatus.GET.getCode());
            sessionTemplate.update(object);

            this.addProgress(id, 0, "客服已收到商品");
        }
    }

    @Override
    public void sureCheckGoodsSuccess(long id) throws ModuleException {
        ModelObject as = sessionTemplate.get(TableAfterSales.class, id);
        if (as != null) {
            int status = as.getIntValue(TableAfterSales.status);
            if (status != AfterSalesStatus.GET.getCode()) {
                throw new ModuleException(StockCode.STATUS_ERROR, "必须先收货后再验货");
            }
            ModelObject object = new ModelObject(TableAfterSales.class);
            object.put(TableAfterSales.id, id);
            object.put(TableAfterSales.status, AfterSalesStatus.GET_SUCCESS.getCode());
            sessionTemplate.update(object);

            this.addProgress(id, 0, "客服已验收商品");
        }
    }

    @Override
    public void sureStartRepair(long id) throws ModuleException {
        ModelObject as = sessionTemplate.get(TableAfterSales.class, id);
        if (as != null) {
            int status = as.getIntValue(TableAfterSales.status);
            if (status != AfterSalesStatus.GET_SUCCESS.getCode()) {
                throw new ModuleException(StockCode.STATUS_ERROR, "先验货后再维修");
            }
            ModelObject object = new ModelObject(TableAfterSales.class);
            object.put(TableAfterSales.id, id);
            object.put(TableAfterSales.status, AfterSalesStatus.START_REPAIR.getCode());
            sessionTemplate.update(object);

            this.addProgress(id, 0, "工作人员已经开始维修");
        }
    }

    @Override
    public void repairSuccess(long id) throws ModuleException {
        ModelObject as = sessionTemplate.get(TableAfterSales.class, id);
        if (as != null) {
            int status = as.getIntValue(TableAfterSales.status);
            if (status != AfterSalesStatus.START_REPAIR.getCode()) {
                throw new ModuleException(StockCode.STATUS_ERROR, "确定开始维修后再设置结果");
            }
            ModelObject object = new ModelObject(TableAfterSales.class);
            object.put(TableAfterSales.id, id);
            object.put(TableAfterSales.status, AfterSalesStatus.REPAIR_SUCCESS.getCode());
            sessionTemplate.update(object);

            this.addProgress(id, 0, "工作人员维修成功");
        }
    }

    @Override
    public void repairFailure(long id) throws ModuleException {
        ModelObject as = sessionTemplate.get(TableAfterSales.class, id);
        if (as != null) {
            int status = as.getIntValue(TableAfterSales.status);
            if (status != AfterSalesStatus.START_REPAIR.getCode()) {
                throw new ModuleException(StockCode.STATUS_ERROR, "确定开始维修后再设置结果");
            }
            ModelObject object = new ModelObject(TableAfterSales.class);
            object.put(TableAfterSales.id, id);
            object.put(TableAfterSales.status, AfterSalesStatus.REPAIR_FAILURE.getCode());
            sessionTemplate.update(object);

            this.addProgress(id, 0, "工作人员维修失败");
        }
    }

    @Override
    public void createRefundOrder(ModelObject object) throws ModuleException, TransactionException {
        long afterSalesId = object.getLongValue("asid");
        int isDefault = object.getIntValue("default");
        ModelObject as = sessionTemplate.get(TableAfterSales.class, afterSalesId);
        if (as != null) {
            long orderGoodsId = as.getLongValue(TableAfterSales.orderGoodsId);
            int status = as.getIntValue(TableAfterSales.status);
            int amount = as.getIntValue(TableAfterSales.amount);
            if (status != AfterSalesStatus.GET_SUCCESS.getCode()) {
                throw new ModuleException(StockCode.STATUS_ERROR, "确认寄回商品没有问题后才能退款");
            }

            ModelObject orderGoods = GlobalService.orderService.getOrderGoodsById(orderGoodsId);
            BigDecimal orderGoodsPrice = orderGoods.getBigDecimal(TableOrderGoods.payPrice);
            int orderGoodsAmount = orderGoods.getIntValue(TableOrderGoods.amount);
            List<ModelObject> refundOrders = sessionTemplate.list(
                    Criteria.query(TableAfterSalesRefundOrder.class)
                            .eq(TableAfterSalesRefundOrder.afterSalesId, afterSalesId));
            long orderId = as.getLongValue(TableAfterSales.orderId);
            if (refundOrders != null && refundOrders.size() > 0) {
                throw new ModuleException(StockCode.FAILURE, "您已经创建过退款单不允许重复创建");
            }


            /**
             * 首先判断当前支付方式延时支付和即时支付
             * 如果全部是即时支付，则不需要创建退款单
             * 如果有延时支付，则需要创建退款单走退款流程(暂时只支持原路返还)
             * 即时支付方式退款金额按照支付占比退款
             */

            ModelObject prices = this.getRefundMoney(afterSalesId); // 退款金额
            BigDecimal refundPrice = prices.getBigDecimal("refundPrice");

            ModelObject update = new ModelObject(TableAfterSales.class);
            update.put(TableAfterSales.id, afterSalesId);
            update.put(TableAfterSales.status, AfterSalesStatus.REFUND.getCode());


            // 如果后台没有自定义退款金额，则按照支付金额来
            if (isDefault == 1) {
                object.put(TableRefundOrder.money, refundPrice);
                object.put(TableRefundOrder.chargeMoney, 0);
            } else {
                refundPrice = object.getBigDecimal("money");
                BigDecimal oldFundPrice = prices.getBigDecimal(TableOrderGoods.payPrice);
                BigDecimal oldPayPrice = prices.getBigDecimal("refundPrice");
                if (oldPayPrice.doubleValue() > oldFundPrice.doubleValue()) {
                    throw new ModuleException(StockCode.FAILURE, "退款金额不能大于订单商品支付金额(商品支付总金额/购买数量)");
                }
                object.put(TableRefundOrder.money, CalcNumber.as(refundPrice).toPrice());
                object.put(TableRefundOrder.chargeMoney, CalcNumber.as(oldPayPrice).subtract(refundPrice).toPrice());
            }

            if (refundPrice.doubleValue() > CalcNumber.as(orderGoodsPrice).divide(orderGoodsAmount).multiply(amount).toDouble()) {
                throw new ModuleException(StockCode.FAILURE, "退款金额大于售后应退金额(商品支付总金额/购买数量)*售后数量");
            }


            List<ModelObject> trans = sessionTemplate.list(Criteria.query(TablePaymentTransaction.class)
                    .eq(TablePaymentTransaction.type, PaymentTransactionType.ORDER.getCode())
                    .eq(TablePaymentTransaction.rid, orderId)
                    .ne(TablePaymentTransaction.payStatus, OrderPayStatus.NOT_PAY.getCode()));

            if (trans != null && trans.size() > 0) {
                BigDecimal totalChannelPrice = new BigDecimal(0);
                for (ModelObject transItem : trans) {
                    BigDecimal transPayPrice = transItem.getBigDecimal(TablePaymentTransaction.payPrice);
                    if (transPayPrice == null) transPayPrice = new BigDecimal(0);
                    BigDecimal transRefundPrice = transItem.getBigDecimal(TablePaymentTransaction.refundPrice);
                    if (transRefundPrice == null) transRefundPrice = new BigDecimal(0);
                    totalChannelPrice = totalChannelPrice.add(transPayPrice.subtract(transRefundPrice));
                }

                // 计算出退款金额占支付总金额比例
                // 然后拿这个比例平均每种支付方式应该退的金额
                // 废弃!!! 比如36元退款12元这时1/3无法整除只能设置精度，导致结果不是理想结果
                //BigDecimal rate = CalcNumber.as(refundPrice)
                //        .divide(totalChannelPrice, 10, RoundingMode.FLOOR).toBigDecimal();

                boolean hasDelay = false;
                for (ModelObject transItem : trans) {
                    long transId = transItem.getLongValue(TablePaymentTransaction.id);
                    long payChannelId = transItem.getLongValue(TablePaymentTransaction.payChannelId);
                    ModelObject channel = GlobalService.paymentService.getChannelById(payChannelId);
                    PaymentModeInterface paymentModeInterface = GlobalService.paymentService.getValidPaymentMode(channel);

                    if (paymentModeInterface == null) {
                        throw new ModuleException("not_found_payment_interface", "没有找到退款接口无法退款");
                    }

                    BigDecimal transPayPrice = transItem.getBigDecimal(TablePaymentTransaction.payPrice);
                    if (transPayPrice == null) transPayPrice = new BigDecimal(0);
                    BigDecimal transRefundPrice = transItem.getBigDecimal(TablePaymentTransaction.refundPrice);
                    if (transRefundPrice == null) transRefundPrice = new BigDecimal(0);
                    BigDecimal surplusMoney = transPayPrice.subtract(transRefundPrice);
                    // 当前支付方式应该退款的金额
                    BigDecimal currRefundPrice = CalcNumber.as(surplusMoney).multiply(refundPrice)
                            .divide(totalChannelPrice, 2, RoundingMode.FLOOR).toBigDecimal();

                    if (paymentModeInterface.getPaymentFlow() == PaymentFlow.DELAY) {
                        hasDelay = true;
                        // 组建退款单,暂时只支持原路返还
                        object.put(TableRefundOrder.userId, as.getLongValue(TableAfterSales.userId));
                        object.put(TableRefundOrder.orderId, as.getLongValue(TableAfterSales.orderId));
                        object.put(TableRefundOrder.type, RefundType.SOURCE.getCode());
                        object.put(TableRefundOrder.from, RefundFrom.RETURN_GOODS.name());
                        object.put(TableRefundOrder.fromId, afterSalesId);
                        object.put(TableRefundOrder.transId, transId);
                        object.put(TableRefundOrder.sourceMoney, CalcNumber.as(currRefundPrice).toPrice());
                        StringBuilder sb = new StringBuilder();
                        sb.append("用户");
                        int type = as.getIntValue(TableAfterSales.type);
                        if (type == 10) sb.append("退货");
                        sb.append("通过并退款");
                        sb.append(",售后单号:" + as.getString(TableAfterSales.number));
                        sb.append(",商品名称:" + prices.getString(TableOrderGoods.goodsName) + "[" + prices.getString(TableOrderGoods.serialNumber) + "]");
                        object.put(TableRefundOrder.detail, sb.toString());


                        GlobalService.refundOrderService.addRefundOrder(object, false);
                        ModelObject afterSalesRefundOrder = new ModelObject(TableAfterSalesRefundOrder.class);
                        afterSalesRefundOrder.put(TableAfterSalesRefundOrder.afterSalesId, afterSalesId);
                        afterSalesRefundOrder.put(TableAfterSalesRefundOrder.refundOrderId, object.getLongValue(TableRefundOrder.id));
                        sessionTemplate.saveAndUpdate(afterSalesRefundOrder);

                    } else {
                        GlobalService.paymentService.handBackAfterSale(afterSalesId, 0, transId, currRefundPrice, PaymentTransactionType.ORDER);
                    }
                }
                if (hasDelay) {
                    addProgress(afterSalesId, 0, "正在退款中");
                } else {
                    addProgress(afterSalesId, 0, "退款成功");
                    update.put(TableAfterSales.status, AfterSalesStatus.FINISH.getCode());
                }
                sessionTemplate.update(update);
            } else {
                throw new ModuleException(StockCode.FAILURE, "支付交易单不存在");
            }

        } else {
            throw new ModuleException(StockCode.FAILURE, "售后单不存在");
        }
    }

    @Override
    public ModelObject getRefundMoney(long id) throws ModuleException {
        ModelObject as = sessionTemplate.get(TableAfterSales.class, id);
        if (as != null) {
            long ogi = as.getLongValue(TableAfterSales.orderGoodsId);
            int amount = as.getIntValue(TableAfterSales.amount);
            ModelObject goods = GlobalService.orderService.getOrderGoodsById(ogi);
            BigDecimal payPrice = goods.getBigDecimal(TableOrderGoods.payPrice);
            ModelObject object = new ModelObject();
            object.put(TableOrderGoods.payPrice, payPrice);
            object.put(TableOrderGoods.goodsName, goods.getString(TableOrderGoods.goodsName));
            object.put(TableOrderGoods.serialNumber, goods.getString(TableOrderGoods.serialNumber));
            object.put("refundPrice", CalcNumber.as(payPrice).divide(goods.getIntValue(TableOrderGoods.amount))
                    .multiply(amount).toPrice());
            return object;
        } else {
            throw new ModuleException(StockCode.NOT_EXIST, "售后单不存在");
        }
    }

    @Override
    public void rebackGoods(ModelObject object) throws ModuleException, TransactionException {
        long id = object.getLongValue("asid");
        ModelObject as = sessionTemplate.get(TableAfterSales.class, id);
        if (as != null) {
            int status = as.getIntValue(TableAfterSales.status);
            if (status != AfterSalesStatus.REPAIR_SUCCESS.getCode()) {
                throw new ModuleException(StockCode.STATUS_ERROR, "维修成功后才可以寄回商品");
            }
            ModelObject update = new ModelObject(TableAfterSales.class);
            update.put(TableAfterSales.id, id);
            update.put(TableAfterSales.status, AfterSalesStatus.SEND_BACK.getCode());
            sessionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean invoke(Transaction transaction) throws Exception {
                    sessionTemplate.update(update);
                    object.setObjectClass(TableAfterSalesAddress.class);
                    object.checkAndThrowable();
                    return true;
                }
            });

            this.addProgress(id, 0, "商品正在快递寄回");
        }
    }

    @Override
    public void resendGoods(ModelObject object) throws ModuleException, ModelCheckerException, TransactionException {
        long id = object.getLongValue("asid");
        ModelObject as = sessionTemplate.get(TableAfterSales.class, id);
        if (as != null) {
            int status = as.getIntValue(TableAfterSales.status);
            if (status != AfterSalesStatus.GET_SUCCESS.getCode()) {
                throw new ModuleException(StockCode.STATUS_ERROR, "验货没问题后才可以重新发货");
            }

            object.setObjectClass(TableAfterSalesAddress.class);
            object.checkAndThrowable();

            ModelObject update = new ModelObject(TableAfterSales.class);
            update.put(TableAfterSales.id, id);
            update.put(TableAfterSales.status, AfterSalesStatus.SEND_BACK.getCode());
            sessionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean invoke(Transaction transaction) throws Exception {
                    sessionTemplate.saveAndUpdate(object);
                    sessionTemplate.update(update);
                    return true;
                }
            });
        } else {
            throw new ModuleException(StockCode.NOT_EXIST, "售后单信息不存在");
        }
    }

    @Override
    public List<ModelObject> getOrderByAfterSales(long uid, String keyword, int page) {
        int limit = 20;
        int start = (page - 1) * limit;

        List<ModelObject> orders = null;
        if (StringUtils.isNotBlank(keyword)) {
            AutoResult ar = null;
            try {
                ar = sessionTemplate.getAutonomously("aftersales.getOrderSearch",
                        ModelBuilder.create()
                                .put("keyword", "%" + keyword + "%")
                                .put("userId", uid)
                                .put("status", OrderStatus.RECEIVE.getCode())
                                .put("start", start)
                                .put("limit", limit).toModelObject());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            List<String> ids = ar.getStrings();
            if (ids != null && ids.size() > 0) {
                Query query = Criteria.query(TableOrderGoods.class)
                        .subjoin(TableOrder.class).eq(TableOrder.id, TableOrderGoods.orderId).single().query()
                        .order(TableOrderGoods.id, false);
                query.in(TableAfterSales.id, ids);
                List<ModelObject> orderGoods = sessionTemplate.list(query);

                if (orderGoods != null && orderGoods.size() > 0) {
                    orders = new ArrayList<>();
                    for (ModelObject og : orderGoods) {
                        ModelObject order = og.getModelObject(TableOrder.class);
                        order.put(TableOrderGoods.class, Arrays.asList(og));
                        og.remove(TableOrder.class);
                        orders.add(order);
                    }
                }
            }
        } else {
            Query query = Criteria.query(TableOrder.class)
                    .subjoin(TableOrderGoods.class).eq(TableOrderGoods.orderId, TableOrder.id).query()
                    .order(TableOrder.id, false);
            query.eq(TableOrder.userId, uid);
            query.eq(TableOrder.status, OrderStatus.RECEIVE.getCode());
            query.limit(start, limit);
            orders = sessionTemplate.list(query);
        }
        return orders;
    }

    @Override
    public List<ModelObject> getProcessAfterSaleList(long uid, int page) {
        int limit = 20;
        int start = (page - 1) * limit;
        List<ModelObject> orders = sessionTemplate.list(Criteria.query(TableAfterSales.class)
                .subjoin(TableOrderGoods.class).eq(TableOrderGoods.id, TableAfterSales.orderGoodsId).single().query()
                .eq(TableAfterSales.userId, uid)
                .ne(TableAfterSales.status, AfterSalesStatus.CANCEL.getCode())
                .ne(TableAfterSales.status, AfterSalesStatus.FINISH.getCode())
                .order(TableAfterSales.id, false)
                .limit(start, limit));
        return orders;
    }

    @Override
    public List<ModelObject> getAfterSaleList(long uid, String keyword, int page) {
        int limit = 20;
        int start = (page - 1) * limit;

        Query query = Criteria.query(TableAfterSales.class)
                .subjoin(TableOrderGoods.class).eq(TableOrderGoods.id, TableAfterSales.orderGoodsId).single().query()
                .subjoin(TableAfterSalesProgress.class).eq(TableAfterSalesProgress.asid, TableAfterSales.id).query()
                .order(TableAfterSales.id, false)
                .limit(start, limit);


        if (StringUtils.isNotBlank(keyword)) {
            try {
                AutoResult ar = sessionTemplate.getAutonomously("aftersales.getASSearch",
                        ModelBuilder.create()
                                .put("keyword", "%" + keyword + "%")
                                .put("userId", uid)
                                .put("start", start)
                                .put("limit", limit).toModelObject());
                List<String> ids = ar.getStrings();
                if (ids != null && ids.size() > 0) {
                    query.in(TableAfterSales.id, ids);
                } else {
                    query = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            query.eq(TableAfterSales.userId, uid);
            query.limit(start, limit);
        }

        if (query != null) {
            List<ModelObject> orders = sessionTemplate.list(query);
            if (orders != null) {
                for (ModelObject order : orders) {
                    List<ModelObject> progresses = order.getArray(TableAfterSalesProgress.class);
                    long maxid = 0;
                    ModelObject maxp = null;
                    if (progresses != null) {
                        for (ModelObject p : progresses) {
                            long id = p.getLongValue(TableAfterSalesProgress.id);
                            if (maxid < id) {
                                maxid = id;
                                maxp = p;
                            }
                        }
                    }
                    if (maxp != null) {
                        order.put(TableAfterSalesProgress.class, maxp);
                    }
                }
            }

            return orders;
        }
        return null;
    }

    @Override
    public ModelObject getAfterSaleOrderGoods(long uid, long og) {
        ModelObject orderGoods = sessionTemplate.get(Criteria.query(TableOrderGoods.class)
                .eq(TableOrderGoods.id, og));
        if (orderGoods != null) {
            long oid = orderGoods.getLongValue(TableOrderGoods.orderId);
            ModelObject order = sessionTemplate.get(Criteria.query(TableOrder.class)
                    .eq(TableOrder.id, oid)
                    .eq(TableOrder.userId, uid));

            if (order == null) {
                return null;
            } else {
                BigDecimal payPrice = orderGoods.getBigDecimal(TableOrderGoods.payPrice);
                int amount = orderGoods.getIntValue(TableOrderGoods.amount);
                orderGoods.put("unitPrice", CalcNumber.as(payPrice.divide(new BigDecimal(amount))).toPrice());
                orderGoods.put(TableOrder.class, order);
                return orderGoods;
            }
        }
        return null;
    }

    @Override
    public ModelObject getUserAfterSaleDetail(long uid, String number) {
        ModelObject object = sessionTemplate.get(Criteria.query(TableAfterSales.class)
                .subjoin(TableAfterSalesImages.class).eq(TableAfterSalesImages.asid, TableAfterSales.id).query()
                .subjoin(TableAfterSalesAddress.class).eq(TableAfterSalesAddress.asid, TableAfterSales.id).query()
                .subjoin(TableOrderGoods.class).eq(TableOrderGoods.id, TableAfterSales.orderGoodsId).single().query()
                .eq(TableAfterSales.userId, uid)
                .eq(TableAfterSales.number, number));
        if (object != null) {
            ModelObject orderGoods = object.getModelObject(TableOrderGoods.class);
            BigDecimal payPrice = orderGoods.getBigDecimal(TableOrderGoods.payPrice);
            int amount = orderGoods.getIntValue(TableOrderGoods.amount);
            orderGoods.put("unitPrice", CalcNumber.as(payPrice.divide(new BigDecimal(amount))).toPrice());

            ModelObject lastProgress = sessionTemplate.get(Criteria.query(TableAfterSalesProgress.class)
                    .eq(TableAfterSalesProgress.asid, object.getLongValue(TableAfterSales.id))
                    .order(TableAfterSalesProgress.id, false));
            object.put(TableAfterSalesProgress.class, lastProgress);
        }
        return object;
    }

    @Override
    public List<ModelObject> getAfterSaleProcess(long uid, String number) {
        ModelObject object = sessionTemplate.get(Criteria.query(TableAfterSales.class)
                .eq(TableAfterSales.userId, uid)
                .eq(TableAfterSales.number, number));
        if (object != null) {
            return sessionTemplate.list(Criteria.query(TableAfterSalesProgress.class)
                    .eq(TableAfterSalesProgress.asid, object.getLongValue(TableAfterSales.id))
                    .order(TableAfterSalesProgress.id, false));
        }
        return null;
    }

    @Override
    public void cancelUserAfterSalesByNumber(long uid, String number) throws ModuleException {
        ModelObject object = sessionTemplate.get(Criteria.query(TableAfterSales.class)
                .eq(TableAfterSales.userId, uid)
                .eq(TableAfterSales.number, number));
        this.cancelAfterSales(object);
    }

    @Override
    public void deliveryGoodsByUser(long uid, ModelObject as) throws ModuleException, TransactionException {
        String number = as.getString(TableAfterSales.number);
        if (StringUtils.isNotBlank(number)) {
            ModelObject afterSale = sessionTemplate.get(Criteria.query(TableAfterSales.class)
                    .eq(TableAfterSales.number, number));


            if (afterSale != null) {
                long id = afterSale.getLongValue(TableAfterSales.id);
                int status = afterSale.getIntValue(TableAfterSales.status);
                if (status != AfterSalesStatus.START_SUCCESS.getCode()) {
                    throw new ModuleException(StockCode.STATUS_ERROR, "审核通过后才能寄回商品");
                }
                ModelObject update = new ModelObject(TableAfterSales.class);
                update.put(TableAfterSales.id, id);
                update.put(TableAfterSales.status, AfterSalesStatus.SEND.getCode());

                ModelObject address = sessionTemplate.get(Criteria.query(TableAfterSalesAddress.class)
                        .eq(TableAfterSalesAddress.asid, id)
                        .eq(TableAfterSalesAddress.type, 1));

                if (address == null) {
                    throw new ModuleException("not_found_sale_addr", "没有找到卖家收货地址");
                }
                String code = as.getString(TableAfterSalesAddress.lgsCompanyCode);
                if (StringUtils.isNotBlank(code)) {
                    ModelObject company = GlobalService.logisticsService.getCompanyByCode(code);
                    if (company != null) {
                        as.put(TableAfterSalesAddress.lgsCompanyName, company.getString(TableLogisticsCompany.name));
                    }
                }
                ModelObject updateAddress = new ModelObject(TableAfterSalesAddress.class);
                updateAddress.put(TableAfterSalesAddress.asid, address.getLongValue(TableAfterSalesAddress.asid));
                updateAddress.put(TableAfterSalesAddress.type, address.getIntValue(TableAfterSalesAddress.type));
                updateAddress.put(TableAfterSalesAddress.lgsCompanyCode, as.getString(TableAfterSalesAddress.lgsCompanyCode));
                updateAddress.put(TableAfterSalesAddress.lgsCompanyName, as.getString(TableAfterSalesAddress.lgsCompanyName));
                updateAddress.put(TableAfterSalesAddress.trackingNumber, as.getString(TableAfterSalesAddress.trackingNumber));

                sessionTemplate.execute(new TransactionCallback<Boolean>() {
                    @Override
                    public Boolean invoke(Transaction transaction) throws Exception {
                        sessionTemplate.update(update);
                        sessionTemplate.update(updateAddress);
                        addProgress(id, 0, "买家已寄回商品，等待卖家收货");
                        return true;
                    }
                });

            }
        }
    }

    @Override
    public ModelObject getAfterSalesByRefund(long fromId, long refundId) {
        ModelObject object = sessionTemplate.get(Criteria.query(TableAfterSalesRefundOrder.class)
                .eq(TableAfterSalesRefundOrder.afterSalesId, fromId)
                .eq(TableAfterSalesRefundOrder.refundOrderId, refundId));
        if (object != null) {
            return sessionTemplate.get(Criteria.query(TableAfterSales.class)
                    .eq(TableAfterSales.id, fromId)
                    .eq(TableAfterSales.status, AfterSalesType.REFUND.getCode()));
        }
        return null;
    }

    @Override
    public void setAfterSalesFinish(long asid) throws ModuleException {
        ModelObject afterSales = sessionTemplate.get(Criteria.query(TableAfterSales.class)
                .eq(TableAfterSales.id, asid));
        if (afterSales != null) {
            long orderGoodsId = afterSales.getLongValue(TableAfterSales.orderGoodsId);
            int type = afterSales.getIntValue(TableAfterSales.type);
            int status = afterSales.getIntValue(TableAfterSales.status);
            if (status == AfterSalesStatus.REPAIR_SUCCESS.getCode()
                    || status == AfterSalesStatus.REFUND.getCode()
                    || status == AfterSalesStatus.SEND_BACK.getCode()) {
                ModelObject update = new ModelObject(TableAfterSales.class);
                update.put(TableAfterSales.id, asid);
                update.put(TableAfterSales.status, AfterSalesStatus.FINISH.getCode());
                sessionTemplate.update(update);
                GlobalService.orderService.setLastAfterSaleType(orderGoodsId, type);

                addProgress(asid, 0, "您的售后服务已完成，感谢您的支持");
            } else {
                throw new ModuleException("status_not_allow", "当前售后单状态不允许完成");
            }
        } else {
            throw new ModuleException("not_found_after_sales", "没有找到售后单");
        }
    }

    @Override
    public ModelObject getAfterSalesRefund(long asid) {
        List<ModelObject> prices = GlobalService.paymentService.getPaymentRefund(PaymentRefundType.AFTER_SALES, asid);
        if (prices != null && prices.size() > 0) {
            BigDecimal totalPrice = new BigDecimal(0);
            if (prices != null) {
                for (ModelObject p : prices) {
                    BigDecimal refundPrice = p.getBigDecimal(TablePaymentRefund.refundPrice);
                    BigDecimal refundAmount = p.getBigDecimal(TablePaymentRefund.refundAmount);
                    totalPrice = totalPrice.add(refundPrice);
                    p.put("isDiffAmount", 0);
                    if (refundPrice.doubleValue() != refundAmount.doubleValue()) {
                        p.put("isDiffAmount", 1);
                    }
                }
            }
            ModelObject object = new ModelObject();
            object.put("totalPrice", totalPrice);
            object.put("ways", prices);
            return object;
        }
        return null;
    }

    @Override
    public long getAfterSaleCount(int type) {
        if (type == 1) {
            return sessionTemplate.count(Criteria.query(TableAfterSales.class)
                    .ne(TableAfterSales.status, AfterSalesStatus.FINISH.getCode())
                    .ne(TableAfterSales.status, AfterSalesStatus.CANCEL.getCode()));
        }
        return 0;
    }

    private String getOrderNumber() {
        long num = RandomUtils.randomNumber(100000, 999999);
        return "" + (new Date().getTime() / 1000) + num;
    }
}
