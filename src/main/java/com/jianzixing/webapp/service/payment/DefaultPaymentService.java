package com.jianzixing.webapp.service.payment;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.order.OrderPayStatus;
import com.jianzixing.webapp.service.order.OrderRefundStatus;
import com.jianzixing.webapp.service.refund.RefundAuditStatus;
import com.jianzixing.webapp.service.refund.RefundFrom;
import com.jianzixing.webapp.service.refund.RefundStatus;
import com.jianzixing.webapp.service.refund.RefundType;
import com.jianzixing.webapp.tables.aftersales.TableAfterSales;
import com.jianzixing.webapp.tables.balance.TableBalanceRecharge;
import com.jianzixing.webapp.tables.order.TableOrder;
import com.jianzixing.webapp.tables.order.TableOrderGoods;
import com.jianzixing.webapp.tables.payment.*;
import com.jianzixing.webapp.tables.refund.TableRefundOrder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.orm.transaction.Transaction;
import org.mimosaframework.orm.transaction.TransactionCallback;
import org.mimosaframework.orm.transaction.TransactionPropagationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class DefaultPaymentService implements PaymentService {
    private static final Log logger = LogFactory.getLog(DefaultPaymentService.class);

    @Autowired
    SessionTemplate sessionTemplate;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void addChannel(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TablePaymentChannel.class);
        object.checkAndThrowable();
        PaymentModeInterface paymentModeInterface = this.getValidPaymentMode(object);
        if (paymentModeInterface.getPaymentFlow() == PaymentFlow.TIMELY) {
            ModelObject paymentChannel = sessionTemplate.get(Criteria.query(TablePaymentChannel.class)
                    .eq(TablePaymentChannel.impl, object.getString(TablePaymentChannel.impl)));
            if (paymentChannel != null) {
                throw new ModuleException(StockCode.EXIST_OBJ,
                        "只允许存在一条[" + paymentModeInterface.getPaymentName() + "]的支付方式");
            }
        }
        try {
            sessionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean invoke(Transaction transaction) throws Exception {
                    sessionTemplate.save(object);

                    ModelArray array = object.getModelArray("arguments");
                    if (array != null && array.size() > 0) {
                        List<ModelObject> args = new ArrayList<>();
                        for (int i = 0; i < array.size(); i++) {
                            ModelObject arg = array.getModelObject(i);
                            if (arg.isNotEmpty(TablePaymentArgument.value)) {
                                ModelObject param = new ModelObject(TablePaymentArgument.class);
                                param.put(TablePaymentArgument.key, arg.getString("key"));
                                param.put(TablePaymentArgument.value, arg.getString("value"));
                                param.put(TablePaymentArgument.channelId, object.getLongValue(TablePaymentChannel.id));
                                param.checkAndThrowable();
                                args.add(param);
                            }
                        }
                        sessionTemplate.save(args);
                    }
                    return true;
                }
            });
        } catch (TransactionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteChannel(long id) {
        ModelObject update = new ModelObject(TablePaymentChannel.class);
        update.put(TablePaymentChannel.id, id);
        update.put(TablePaymentChannel.isDel, 1);
        sessionTemplate.update(update);
    }

    @Override
    public void updateChannel(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TablePaymentChannel.class);
        object.checkUpdateThrowable();
        object.remove(TablePaymentChannel.impl);
        try {
            sessionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean invoke(Transaction transaction) throws Exception {
                    long channelId = object.getLongValue(TablePaymentChannel.id);
                    sessionTemplate.delete(
                            Criteria.delete(TablePaymentArgument.class)
                                    .eq(TablePaymentArgument.channelId, channelId)
                    );
                    sessionTemplate.update(object);

                    ModelArray array = object.getModelArray("arguments");
                    if (array != null) {
                        List<ModelObject> args = new ArrayList<>();
                        for (int i = 0; i < array.size(); i++) {
                            ModelObject arg = array.getModelObject(i);
                            if (arg.isNotEmpty(TablePaymentArgument.value)) {
                                arg.remove(TablePaymentArgument.id);
                                arg.setObjectClass(TablePaymentArgument.class);
                                arg.put(TablePaymentArgument.channelId, channelId);
                                arg.checkAndThrowable();
                                args.add(arg);
                            }
                        }
                        sessionTemplate.save(args);
                    }
                    return true;
                }
            });
        } catch (TransactionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Paging getChannels(String keyword, long start, long limit) {
        Query query = Criteria.query(TablePaymentChannel.class)
                .subjoin(TablePaymentArgument.class).eq(TablePaymentArgument.channelId, TablePaymentChannel.id).query()
                .eq(TablePaymentChannel.isDel, 0)
                .limit(start, limit);
        if (StringUtils.isNotBlank(keyword)) {
            query.like(TablePaymentChannel.name, "%" + keyword + "%");
        }
        return sessionTemplate.paging(query);
    }

    @Override
    public List<ModelObject> getModels() {
        Map<String, PaymentModeInterface> beans = applicationContext.getBeansOfType(PaymentModeInterface.class);
        if (beans != null) {
            List<ModelObject> objects = new ArrayList<>();
            for (Map.Entry<String, PaymentModeInterface> entry : beans.entrySet()) {
                ModelObject object = new ModelObject();
                object.put("name", entry.getValue().getPaymentName());
                object.put("impl", entry.getValue().getPackageImpl().getName());
                object.put("params", entry.getValue().getNeedParams());
                object.put("cert", entry.getValue().isWantCertificate() ? 1 : 0);
                objects.add(object);
            }
            return objects;
        }
        return null;
    }

    @Override
    public List<ModelObject> getValidPayChannelByUid(long uid) {
        List<ModelObject> channels = sessionTemplate.list(
                Criteria.query(TablePaymentChannel.class)
                        .eq(TablePaymentChannel.enable, 1)
                        .eq(TablePaymentChannel.isDel, 0)
                        .order(TablePaymentChannel.pos, false));
        if (channels != null) {
            List<ModelObject> rm = new ArrayList<>();
            for (ModelObject channel : channels) {
                PaymentModeInterface paymentModeInterface = this.getValidPaymentMode(channel);
                if (paymentModeInterface != null) {
                    channel.put("paymentModeInterface", paymentModeInterface);
                    channel.put("code", paymentModeInterface.getPaymentCode());
                } else {
                    rm.add(channel);
                }
            }
            channels.removeAll(rm);
        }
        return channels;
    }

    @Override
    public PaymentModeInterface getValidPaymentMode(long channelId) {
        if (channelId > 0) {
            ModelObject object = sessionTemplate.get(Criteria.query(TablePaymentChannel.class)
                    .eq(TablePaymentChannel.isDel, 0)
                    .eq(TablePaymentChannel.id, channelId));
            return this.getValidPaymentMode(object);
        }
        return null;
    }

    @Override
    public PaymentModeInterface getValidPaymentMode(ModelObject object) {
        if (object != null) {
            String impl = object.getString(TablePaymentChannel.impl);
            if (StringUtils.isNotBlank(impl)) {
                try {
                    Class<? extends PaymentModeInterface> c = (Class<? extends PaymentModeInterface>) Class.forName(impl);
                    return applicationContext.getBean(c);
                    // return c.newInstance();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public PaymentModeInterface getCanCallbackPaymentMode(String name) {
        Map<String, PaymentModeInterface> beans = applicationContext.getBeansOfType(PaymentModeInterface.class);
        if (beans != null) {
            for (Map.Entry<String, PaymentModeInterface> entry : beans.entrySet()) {
                if (entry.getValue().isCanCallback(name)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public ModelObject getChannelById(long paymentChannelId) {
        return sessionTemplate.get(
                Criteria.query(TablePaymentChannel.class).eq(TablePaymentChannel.id, paymentChannelId)
        );
    }

    @Override
    public ModelObject payOrderFront(OrderPayment orderModel) throws Exception {
        String orderNumber = orderModel.getOrderNumber();
        this.getAndCheck(orderModel);
        List<ModelObject> paymentChannels = orderModel.getPaymentChannels();
        long uid = orderModel.getUid();

        ModelObject order = sessionTemplate.get(
                Criteria.query(TableOrder.class)
                        .subjoin(TableOrderGoods.class).eq(TableOrderGoods.orderId, TableOrder.id).query()
                        .eq(TableOrder.userId, uid)
                        .eq(TableOrder.number, orderNumber)
                        .eq(TableOrder.isDel, 0));
        if (order != null) {
            long oid = order.getLongValue(TableOrder.id);
            Date orderTime = order.getDate(TableOrder.createTime);
            Date orderExpireTime = GlobalService.orderService.getOrderExpireTime(orderTime);
            /**
             * 假如用户第一次已经选择组合支付积分余额微信等
             * 但是第一次并没有支付完整，第二次进来则排除已经支付完成的支付方式
             */
            List<ModelObject> orderPayChannels = sessionTemplate.list(
                    Criteria.query(TablePaymentTransaction.class)
                            .eq(TablePaymentTransaction.rid, oid)
                            .eq(TablePaymentTransaction.number, orderNumber)
                            .eq(TablePaymentTransaction.type, PaymentTransactionType.ORDER.getCode())
            );

            BigDecimal orderPrice = order.getBigDecimal(TableOrder.payPrice);
            BigDecimal orderOriginalPrice = order.getBigDecimal(TableOrder.payPrice);
            BigDecimal payedPrice = new BigDecimal(0);
            if (orderPayChannels != null) {
                for (ModelObject orderPayChannel : orderPayChannels) {
                    long payChannelId = orderPayChannel.getLongValue(TablePaymentTransaction.payChannelId);
                    int status = orderPayChannel.getIntValue(TablePaymentTransaction.payStatus);
                    BigDecimal payPrice = orderPayChannel.getBigDecimal(TablePaymentTransaction.payPrice);
                    if (status != OrderPayStatus.NOT_PAY.getCode()) {
                        payedPrice = payedPrice.add(payPrice);
                    }
                }
                orderPrice = orderPrice.subtract(payedPrice);
            }

            /**
             * 验证支付信息是否完整
             */
            orderModel.setUid(order.getLongValue(TableOrder.userId));
            orderModel.setOrderNumber(order.getString(TableOrder.number));
            orderModel.setOrderPrice(orderPrice);
            orderModel.setRid(oid);
            orderModel.setTimeStart(orderTime);
            orderModel.setTimeExpire(orderExpireTime);

            List<ModelObject> orderGoods = order.getArray(TableOrderGoods.class);
            if (orderGoods != null && orderGoods.size() > 0) {
                List<OrderPaymentProduct> orderGoodsModels = new ArrayList<>();
                for (ModelObject goods : orderGoods) {
                    OrderPaymentProduct goodsModel = new OrderPaymentProduct();
                    goodsModel.setGid(goods.getLongValue(TableOrderGoods.goodsId));
                    goodsModel.setAmount(goods.getIntValue(TableOrderGoods.amount));
                    goodsModel.setProductName(goods.getString(TableOrderGoods.goodsName));
                    goodsModel.setProductPrice(goods.getBigDecimal(TableOrderGoods.payPrice));
                    orderGoodsModels.add(goodsModel);
                }
                orderModel.setProducts(orderGoodsModels);
            }

            List<ModelObject> paymentFlowTimely = new ArrayList<>();
            List<ModelObject> paymentFlowDelay = new ArrayList<>();
            List<Long> channelIds = new ArrayList<>();
            if (paymentChannels != null && paymentChannels.size() > 0) {
                Set<Long> pcids = new LinkedHashSet<>();
                for (ModelObject paymentChannel : paymentChannels) {
                    long pcid = paymentChannel.getLongValue(TablePaymentChannel.id);
                    if (pcid <= 0) {
                        throw new ModuleException("payment_not_exist", "支付方式不存在");
                    }
                    pcids.add(pcid);
                }
                List<ModelObject> channels = this.getChannelByIds(new ArrayList<>(pcids));
                if (channels == null || channels.size() <= 0 || channels.size() != pcids.size()) {
                    throw new ModuleException("payment_not_exist", "支付方式不存在或者部分不存在");
                }
                for (ModelObject paymentChannel : paymentChannels) {
                    long pcid = paymentChannel.getLongValue(TablePaymentChannel.id);
                    boolean has = false;
                    for (ModelObject channel : channels) {
                        long fromPcid = channel.getLongValue(TablePaymentChannel.id);
                        if (pcid == fromPcid) {
                            paymentChannel.put(TablePaymentChannel.class.getSimpleName(), channel);
                            has = true;
                        }
                    }
                    if (!has) {
                        throw new ModuleException("payment_not_exist", "支付方式" + pcid + "不存在");
                    }

                    ModelObject channel = paymentChannel.getModelObject(TablePaymentChannel.class.getSimpleName());
                    PaymentModeInterface modeInterface = this.getValidPaymentMode(channel);
                    if (modeInterface == null) {
                        throw new ModuleException("payment_not_exist_impl", "支付方式实现代码不存在");
                    }
                    paymentChannel.put(PaymentModeInterface.class.getSimpleName(), modeInterface);
                    if (modeInterface.getPaymentFlow() == PaymentFlow.TIMELY) {
                        paymentFlowTimely.add(paymentChannel);
                    } else if (modeInterface.getPaymentFlow() == PaymentFlow.DELAY) {
                        paymentFlowDelay.add(paymentChannel);
                    } else {
                        throw new ModuleException("payment_impl_flow", "支付方式实现代码未标注类型");
                    }
                    channelIds.add(pcid);
                }
                orderModel.setPaymentChannelParams(paymentChannels);
            }

            if (paymentFlowDelay != null && paymentFlowDelay.size() > 1) {
                throw new ModuleException("allow_one_delay", "延迟支付方式只允许同时使用一种");
            }

            /**
             * 先执行即时支付实现
             */
            orderPrice = orderModel.getOrderPrice();
            List<ModelObject> paymentArguments = sessionTemplate.list(
                    Criteria.query(TablePaymentArgument.class).in(TablePaymentArgument.channelId, channelIds));

            /**
             * 如果没有延迟支付方式，则需要判断当前支付方式是否能支付当前订单
             */
            if (paymentFlowDelay == null || paymentFlowDelay.size() == 0 || !orderModel.isCreatePayment()) {
                ModelObject price = new ModelObject();
                BigDecimal orderTrialPrice = new BigDecimal(orderPrice.toPlainString());
                for (ModelObject object : paymentFlowTimely) {
                    ModelObject channel = object.getModelObject(TablePaymentChannel.class.getSimpleName());
                    long channelId = channel.getLongValue(TablePaymentChannel.id);
                    PaymentModeInterface modeInterface = (PaymentModeInterface) object.get(PaymentModeInterface.class.getSimpleName());
                    ModelObject cnfParams = getCnfParams(paymentArguments, channelId);

                    PaymentResult result = modeInterface.trial(orderModel, cnfParams, object);
                    if (result != null && (result.getType() == PaymentResult.TYPE.OK
                            || result.getType() == PaymentResult.TYPE.PART)) {
                        BigDecimal payPrice = result.getPayPrice();
                        orderTrialPrice = orderTrialPrice.subtract(payPrice);

                        ModelObject priceItem = new ModelObject();
                        priceItem.put("price", CalcNumber.as(payPrice).toPrice());
                        priceItem.put("money", object.getDoubleValue("money"));
                        price.put(channelId, priceItem);
                    }
                }

                if (orderTrialPrice.doubleValue() <= 0) {
                    // 试算可以完全支付当前订单
                } else {
                    if (orderModel.isCreatePayment()) {
                        throw new ModuleException("payment_not_enough", "当前所选支付方式无法支付订单");
                    }
                }

                price.put("orderPayPrice", orderTrialPrice.doubleValue() > 0 ? CalcNumber.as(orderTrialPrice).toPrice() : "0");
                if (!orderModel.isCreatePayment()) {
                    return price;
                }
            }


            final boolean[] isTimelyPaySuccess = {false};
            if (!orderPrice.equals(0) && paymentFlowTimely != null) {
                sessionTemplate.execute(new TransactionCallback<Boolean>() {
                    @Override
                    public Boolean invoke(Transaction transaction) throws Exception {

                        List<ModelObject> orderPaymentChannels = new ArrayList<>();
                        for (ModelObject object : paymentFlowTimely) {
                            ModelObject channel = object.getModelObject(TablePaymentChannel.class.getSimpleName());
                            long channelId = channel.getLongValue(TablePaymentChannel.id);
                            String payChannelName = channel.getString(TablePaymentChannel.name);
                            PaymentModeInterface modeInterface = (PaymentModeInterface) object.get(PaymentModeInterface.class.getSimpleName());
                            ModelObject cnfParams = getCnfParams(paymentArguments, channelId);

                            PaymentResult result = modeInterface.apply(orderModel, cnfParams, object);
                            if (result != null && (result.getType() == PaymentResult.TYPE.OK
                                    || result.getType() == PaymentResult.TYPE.PART)) {
                                BigDecimal payPrice = result.getPayPrice();
                                int status = OrderPayStatus.NOT_PAY.getCode();
                                if (result.getType() == PaymentResult.TYPE.OK) {
                                    status = OrderPayStatus.PAYED.getCode();
                                }
                                if (result.getType() == PaymentResult.TYPE.PART) {
                                    status = OrderPayStatus.PART_PAY.getCode();
                                }

                                ModelObject orderPayment = createOrderPayment(
                                        oid, uid, orderNumber, PaymentTransactionType.ORDER.getCode(),
                                        channelId, payChannelName,
                                        payPrice, result.getPayAmount(), status,
                                        result.getOutPaymentNumber(),
                                        result.getPaymentNumber());
                                orderPaymentChannels.add(orderPayment);

                                // 每一个支付完成后，重新计算剩余订单金额
                                BigDecimal orderPrice = orderModel.getOrderPrice();
                                BigDecimal surplus = orderPrice.subtract(payPrice);
                                orderModel.setOrderPrice(surplus);
                                if (surplus.doubleValue() <= 0) {
                                    if (status == OrderPayStatus.PAYED.getCode() && orderModel.isCreatePayment()) {
                                        isTimelyPaySuccess[0] = true;
                                        GlobalService.orderService.setOrderPaySuccess(orderNumber);
                                    }
                                    break;
                                }
                            }
                        }
                        if (orderModel.isCreatePayment() && orderPaymentChannels != null
                                && orderPaymentChannels.size() > 0) {
                            sessionTemplate.save(orderPaymentChannels);
                            BigDecimal orderPrice = orderModel.getOrderPrice();
                            if (orderPrice.doubleValue() < orderOriginalPrice.doubleValue()) {
                                GlobalService.orderService.setOrderPayPartSuccess(orderNumber);
                            }
                        }
                        return null;
                    }
                });
            }


            orderPrice = orderModel.getOrderPrice();
            if (!isTimelyPaySuccess[0] && orderModel.isCreatePayment()) { // 如果不是创建支付单则不执行延迟支付代码
                if (!orderPrice.equals(0) && paymentFlowDelay != null) {
                    for (ModelObject object : paymentFlowDelay) {
                        ModelObject channel = object.getModelObject(TablePaymentChannel.class.getSimpleName());
                        long channelId = channel.getLongValue(TablePaymentChannel.id);
                        String payChannelName = channel.getString(TablePaymentChannel.name);
                        PaymentModeInterface modeInterface = (PaymentModeInterface) object.get(PaymentModeInterface.class.getSimpleName());
                        ModelObject cnfParams = this.getCnfParams(paymentArguments, channelId);
                        PaymentResult result = modeInterface.apply(orderModel, cnfParams, object);

                        ModelObject orderPayment = this.createOrderPayment(
                                oid, uid, orderNumber, PaymentTransactionType.ORDER.getCode(),
                                channelId, payChannelName,
                                orderPrice, orderPrice, OrderPayStatus.NOT_PAY.getCode(),
                                result.getOutPaymentNumber(),
                                result.getPaymentNumber());

                        sessionTemplate.save(orderPayment);

                        ModelObject toPageInfo = new ModelObject();
                        toPageInfo.put("url", result.getUrl());
                        toPageInfo.put("type", result.getType());
                        toPageInfo.put("params", result.getParams());
                        return toPageInfo;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public BigDecimal getChannelPayedPrice(long uid, long channelId, String orderNumber) {
        List<ModelObject> orderPayChannels = sessionTemplate.list(
                Criteria.query(TablePaymentTransaction.class)
                        .eq(TablePaymentTransaction.userId, uid)
                        .eq(TablePaymentTransaction.payChannelId, channelId)
                        .eq(TablePaymentTransaction.number, orderNumber)
                        .eq(TablePaymentTransaction.type, PaymentTransactionType.ORDER.getCode())
        );

        BigDecimal payedPrice = new BigDecimal(0);
        if (orderPayChannels != null) {
            for (ModelObject orderPayChannel : orderPayChannels) {
                int status = orderPayChannel.getIntValue(TablePaymentTransaction.payStatus);
                BigDecimal payPrice = orderPayChannel.getBigDecimal(TablePaymentTransaction.payPrice);
                if (status != OrderPayStatus.NOT_PAY.getCode()) {
                    payedPrice = payedPrice.add(payPrice);
                }
            }
        }
        return payedPrice;
    }

    @Override
    public BigDecimal getChannelPayedPrice(long uid, String orderNumber) {
        List<ModelObject> orderPayChannels = sessionTemplate.list(
                Criteria.query(TablePaymentTransaction.class)
                        .eq(TablePaymentTransaction.userId, uid)
                        .eq(TablePaymentTransaction.number, orderNumber)
                        .eq(TablePaymentTransaction.type, PaymentTransactionType.ORDER.getCode())
        );

        BigDecimal payedPrice = new BigDecimal(0);
        if (orderPayChannels != null) {
            for (ModelObject orderPayChannel : orderPayChannels) {
                int status = orderPayChannel.getIntValue(TablePaymentTransaction.payStatus);
                BigDecimal payPrice = orderPayChannel.getBigDecimal(TablePaymentTransaction.payPrice);
                if (status != OrderPayStatus.NOT_PAY.getCode()) {
                    payedPrice = payedPrice.add(payPrice);
                }
            }
        }
        return payedPrice;
    }

    @Override
    public ModelObject payRechargeFront(ModelObject recharge) throws Exception {
        String number = recharge.getString("oid");
        long uid = recharge.getLongValue("uid");
        ModelObject payment = recharge.getModelObject("payment");

        if (recharge.isEmpty("ip")) {
            throw new ModuleException(StockCode.ARG_NULL, "缺少客户端IP信息");
        }
        if (recharge.isEmpty("host")) {
            throw new ModuleException(StockCode.ARG_NULL, "缺少域名地址信息");
        }

        if (payment == null) {
            throw new ModuleException(StockCode.ARG_NULL, "缺少支付方式信息");
        }

        long pid = payment.getLongValue("channelId");
        ModelObject paymentChannel = sessionTemplate.get(Criteria.query(TablePaymentChannel.class)
                .eq(TablePaymentChannel.id, pid)
                .eq(TablePaymentChannel.enable, 1));
        if (paymentChannel == null) {
            throw new ModuleException(StockCode.ARG_NULL, "支付渠道不存在");
        }

        List<ModelObject> paymentArguments = sessionTemplate.list(Criteria.query(TablePaymentArgument.class)
                .eq(TablePaymentArgument.channelId, pid));

        PaymentModeInterface modeInterface = this.getValidPaymentMode(paymentChannel);
        if (modeInterface == null) {
            throw new ModuleException(StockCode.ARG_NULL, "支付方式实现代码不存在");
        }

        ModelObject rechargeOrder = GlobalService.balanceService.getRechargeOrderByNumber(number);
        long id = rechargeOrder.getLongValue(TableBalanceRecharge.id);
        if (rechargeOrder == null) {
            throw new ModuleException(StockCode.ARG_NULL, "充值单不存在");
        }

        OrderPayment orderModel = new OrderPayment();
        orderModel.setUid(rechargeOrder.getLongValue(TableBalanceRecharge.userId));
        orderModel.setRid(rechargeOrder.getLongValue(TableBalanceRecharge.id));
        orderModel.setOrderNumber(rechargeOrder.getString(TableBalanceRecharge.number));
        orderModel.setOrderPrice(rechargeOrder.getBigDecimal(TableBalanceRecharge.money));
        orderModel.setIp(recharge.getString("ip"));
        orderModel.setHost(recharge.getString("host"));
        orderModel.setTimeStart(new Date());
        orderModel.setTimeExpire(new Date(System.currentTimeMillis() + 20 * 60 * 60 * 1000l));

        List<OrderPaymentProduct> paymentProducts = new ArrayList<>();
        OrderPaymentProduct paymentProduct = new OrderPaymentProduct();
        paymentProduct.setGid(rechargeOrder.getLongValue(TableBalanceRecharge.id));
        paymentProduct.setProductPrice(rechargeOrder.getBigDecimal(TableBalanceRecharge.money));
        paymentProduct.setProductName("余额充值");
        paymentProduct.setAmount(1);
        paymentProducts.add(paymentProduct);
        orderModel.setProducts(paymentProducts);

        paymentChannel.put(PaymentModeInterface.class.getSimpleName(), modeInterface);
        if (modeInterface.getPaymentFlow() == PaymentFlow.DELAY) {
            long channelId = paymentChannel.getLongValue(TablePaymentChannel.id);
            String payChannelName = paymentChannel.getString(TablePaymentChannel.name);
            ModelObject cnfParams = this.getCnfParams(paymentArguments, channelId);

            return sessionTemplate.execute(new TransactionCallback<ModelObject>() {
                @Override
                public ModelObject invoke(Transaction transaction) throws Exception {

                    GlobalService.balanceService.setRechargeChannel(orderModel.getOrderNumber(), channelId, payChannelName);
                    PaymentResult result = modeInterface.apply(orderModel, cnfParams, recharge);

                    ModelObject orderPayment = createOrderPayment(
                            id, uid, orderModel.getOrderNumber(), PaymentTransactionType.RECHARGE.getCode(),
                            channelId, payChannelName,
                            orderModel.getOrderPrice(), orderModel.getOrderPrice(), OrderPayStatus.NOT_PAY.getCode(),
                            result.getOutPaymentNumber(),
                            result.getPaymentNumber());
                    sessionTemplate.save(orderPayment);

                    ModelObject toPageInfo = new ModelObject();
                    toPageInfo.put("url", result.getUrl());
                    toPageInfo.put("params", result.getParams());
                    toPageInfo.put("type", result.getType());
                    return toPageInfo;
                }
            });
        } else {
            throw new ModuleException(StockCode.FAILURE, "当前支付方式不允许充值");
        }
    }

    private void getAndCheck(OrderPayment orderPayment) throws ModuleException {
        if (orderPayment.getPaymentChannels() == null || orderPayment.getPaymentChannels().size() == 0) {
            throw new ModuleException(StockCode.ARG_NULL, "缺少支付渠道相关信息");
        }

        if (StringUtils.isBlank(orderPayment.getIp())) {
            throw new ModuleException(StockCode.ARG_NULL, "缺少客户端IP信息");
        }
        if (StringUtils.isBlank(orderPayment.getHost())) {
            throw new ModuleException(StockCode.ARG_NULL, "缺少域名地址信息");
        }
    }

    private ModelObject createOrderPayment(long id,
                                           long uid,
                                           String orderNumber,
                                           int type,
                                           long channelId,
                                           String payChannelName,
                                           Object payPrice,
                                           Object payAmount,
                                           int status,
                                           String outPaymentNumber,
                                           String paymentNumber) {
        ModelObject orderPayment = new ModelObject(TablePaymentTransaction.class);
        orderPayment.put(TablePaymentTransaction.rid, id);
        orderPayment.put(TablePaymentTransaction.userId, uid);
        orderPayment.put(TablePaymentTransaction.number, orderNumber);
        orderPayment.put(TablePaymentTransaction.type, type);
        orderPayment.put(TablePaymentTransaction.payChannelId, channelId);
        orderPayment.put(TablePaymentTransaction.payChannelName, payChannelName);
        orderPayment.put(TablePaymentTransaction.payPrice, payPrice);
        orderPayment.put(TablePaymentTransaction.payAmount, payAmount);
        orderPayment.put(TablePaymentTransaction.payStatus, status);
        orderPayment.put(TablePaymentTransaction.outPaymentNumber, outPaymentNumber);
        orderPayment.put(TablePaymentTransaction.paymentNumber, paymentNumber);
        orderPayment.put(TablePaymentTransaction.createTime, new Date());
        return orderPayment;
    }

    private ModelObject getCnfParams(List<ModelObject> paymentArguments, long channelId) {
        ModelObject cnfParams = new ModelObject();
        if (paymentArguments != null) {
            for (ModelObject args : paymentArguments) {
                String key = args.getString(TablePaymentArgument.key);
                String value = args.getString(TablePaymentArgument.value);
                long channelAId = args.getLongValue(TablePaymentArgument.channelId);
                if (channelId == channelAId) {
                    cnfParams.put(key, value);
                }
            }
        }
        return cnfParams;
    }

    @Override
    public List<ModelObject> getChannelByIds(ArrayList<Long> longs) {
        return sessionTemplate.list(
                Criteria.query(TablePaymentChannel.class)
                        .in(TablePaymentChannel.id, longs)
                        .in(TablePaymentChannel.enable, 1)
        );
    }

    @Override
    public void setOrderPaymentSuccess(String orderNumber, String paymentNumber, String outPaymentNumber) {
        ModelObject paymentTrans = sessionTemplate.get(Criteria.query(TablePaymentTransaction.class)
                .eq(TablePaymentTransaction.number, orderNumber)
                .eq(TablePaymentTransaction.paymentNumber, paymentNumber));
        int type = paymentTrans.getIntValue(TablePaymentTransaction.type);
        sessionTemplate.update(
                Criteria.update(TablePaymentTransaction.class)
                        .value(TablePaymentTransaction.payStatus, OrderPayStatus.PAYED.getCode())
                        .value(TablePaymentTransaction.outPaymentNumber, outPaymentNumber)
                        .eq(TablePaymentTransaction.number, orderNumber)
                        .eq(TablePaymentTransaction.paymentNumber, paymentNumber)
        );
        if (type == PaymentTransactionType.ORDER.getCode()) {
            GlobalService.orderService.setOrderPaySuccess(orderNumber);
        } else if (type == PaymentTransactionType.RECHARGE.getCode()) {
            GlobalService.balanceService.setRechargeSuccess(orderNumber);
        }
    }

    @Override
    public void addPaymentTrans(ModelObject object) {
        object.setObjectClass(TablePaymentResponse.class);
        object.put(TablePaymentResponse.createTime, new Date());
        sessionTemplate.save(object);
    }

    @Override
    public ModelObject getOrderPaymentChannel(String orderNumber, String paymentNumber) {
        ModelObject paymentTrans = sessionTemplate.get(Criteria.query(TablePaymentTransaction.class)
                .eq(TablePaymentTransaction.number, orderNumber)
                .eq(TablePaymentTransaction.paymentNumber, paymentNumber));
        if (paymentTrans != null) {
            long channelId = paymentTrans.getLongValue(TablePaymentTransaction.payChannelId);
            List<ModelObject> paymentArguments = sessionTemplate.list(Criteria.query(TablePaymentArgument.class)
                    .eq(TablePaymentArgument.channelId, channelId));
            return this.getCnfParams(paymentArguments, channelId);
        }
        return null;
    }

    /**
     * 退款接口不使用数据库事务,如果有任何错误都需要手动查原因
     * 如果使用事务后接口退款成功但是数据库事务回滚造成重复退款问题
     *
     * @param order
     * @param trans
     * @param refundType
     * @param afterSalesId
     * @param refundOrderId
     * @throws ModuleException
     */
    private void handBack(ModelObject order,
                          ModelObject trans, // 包含TablePaymentChannel的TablePaymentTransaction
                          PaymentRefundType refundType,
                          long afterSalesId,
                          long refundOrderId) throws ModuleException {
        long uid = order.getLongValue(TableOrder.userId);
        String orderNumber = order.getString(TableOrder.number);
        long orderId = order.getLongValue(TableOrder.id);
        long transId = trans.getLongValue(TablePaymentTransaction.id);

        ModelObject channel = trans.getModelObject(TablePaymentChannel.class);
        // 已经计算好的退款金额
        BigDecimal refundPrice = trans.getBigDecimal("needRefundPrice");
        ModelObject cnfParams = this.getCnfParams(channel.getArray(TablePaymentArgument.class), channel.getLongValue(TablePaymentChannel.id));
        PaymentModeInterface paymentModeInterface = this.getValidPaymentMode(channel);
        if (paymentModeInterface != null && trans != null) {
            long tid = trans.getLongValue(TablePaymentTransaction.id);
            BigDecimal tranPayPrice = trans.getBigDecimal(TablePaymentTransaction.payPrice);
            BigDecimal tranRefundPrice = trans.getBigDecimal(TablePaymentTransaction.refundPrice);
            if (tranRefundPrice == null) tranRefundPrice = new BigDecimal(0);
            boolean isFullReback = tranPayPrice.subtract(tranRefundPrice).subtract(refundPrice).doubleValue() <= 0;
            ModelObject refundOrder = null;

            /**
             * 支付方式必须是在线支付才需要创建退款单
             * 如果退款单为0，则必须创建一个退款单，
             * 如果不为0，说明已经创建过退款单
             */
            if (refundOrderId == 0
                    && paymentModeInterface.getPaymentFlow() == PaymentFlow.DELAY
                    && refundType.equals(PaymentRefundType.ORDER_CANCEL)) {

                /**
                 * 取消订单时只会创建一个退款单，如果重复退款也只需要判断一个即可
                 */
                List<ModelObject> refundOrders = GlobalService.refundOrderService.getRefundOrderByInfo(uid, orderId, RefundFrom.CANCEL_ORDER, RefundType.SOURCE, transId);
                if (refundOrders == null || refundOrders.size() == 0) {
                    refundOrder = new ModelObject();
                    refundOrder.put(TableRefundOrder.userId, uid);
                    refundOrder.put(TableRefundOrder.orderId, orderId);
                    refundOrder.put(TableRefundOrder.from, RefundFrom.CANCEL_ORDER.name());
                    refundOrder.put(TableRefundOrder.status, RefundStatus.CREATE.getCode());
                    refundOrder.put(TableRefundOrder.auditStatus, RefundAuditStatus.SUCCESS.getCode());
                    refundOrder.put(TableRefundOrder.type, RefundType.SOURCE.getCode());
                    refundOrder.put(TableRefundOrder.sourceMoney, refundPrice);
                    refundOrder.put(TableRefundOrder.transId, transId);
                    refundOrder.put(TableRefundOrder.money, refundPrice);
                    refundOrder.put(TableRefundOrder.remark, "用户取消订单");
                    refundOrder.put(TableRefundOrder.detail, "用户取消订单");

                    ModelObject finalRefundOrder = refundOrder;
                    try {
                        sessionTemplate.execute(new TransactionCallback<Boolean>() {
                            @Override
                            public Boolean invoke(Transaction transaction) throws Exception {
                                GlobalService.refundOrderService.addRefundOrder(finalRefundOrder, true);
                                return true;
                            }
                        }, TransactionPropagationType.PROPAGATION_NOT_SUPPORTED);
                    } catch (TransactionException e) {
                        throw new ModuleException("create_refund_order_fail", "订单取消创建退款单失败", e);
                    }
                    refundOrderId = refundOrder.getLongValue(TableRefundOrder.id);
                } else {

                    for (ModelObject refundOrderItem : refundOrders) {
                        int refundOrderType = refundOrderItem.getIntValue(TableRefundOrder.status);
                        if (refundOrderType == RefundStatus.SUCCESS.getCode()) {
                            logger.error("查询到当前订单已经存在退款单且退款成功,不做处理,refundOrderId: " + refundOrderItem.getLongValue(TableRefundOrder.id));
                            return;
                        }
                        if (refundOrderType == RefundStatus.FAILURE.getCode()) {
                            throw new ModuleException("refund_order_failure", "查询到当前订单已经存在退款单且退款失败,请联系客服手动退款");
                        }
                        refundOrderId = refundOrderItem.getLongValue(TableRefundOrder.id);
                        refundOrder = refundOrderItem;
                    }
                }
            }

            OrderRefundStatus refundStatus = isFullReback ? OrderRefundStatus.FULL : OrderRefundStatus.PART;

            if (refundOrder == null && refundOrderId > 0) {
                refundOrder = GlobalService.refundOrderService.getRefundOrder(refundOrderId);
            }

            if (refundOrder != null) {
                int refundOrderType = refundOrder.getIntValue(TableRefundOrder.status);
                if (refundOrderType == RefundStatus.SUCCESS.getCode()) {
                    throw new ModuleException("refund_order_was_success", "退款单已经退款成功,不允许重复退款");
                }
                if (refundOrderType == RefundStatus.FAILURE.getCode()) {
                    throw new ModuleException("refund_order_was_failure", "退款单已经退款失败,请联系客服手动退款");
                }
            }


            /**
             * 开始使用退款接口退款
             * 如果退款成功则重新更新支付交易单状态
             */
            PaymentRefundParams refundParams = new PaymentRefundParams(channel, cnfParams, trans, refundPrice);
            refundParams.setRefundOrderId(refundOrderId);
            refundParams.setRefundOrder(refundOrder);

            logger.info("Execute Hand Back Interface Before:" +
                    "  interfaceName[" + paymentModeInterface.getPaymentName() + "]" +
                    "  orderId[" + orderId + "]" +
                    "  orderNumber[" + orderNumber + "]" +
                    "  afterSalesId[" + afterSalesId + "]" +
                    "  refundType[" + refundType.getMsg() + "]" +
                    "  refundOrderId[" + refundOrderId + "]");


            paymentModeInterface.handBack(refundParams);
            logger.info("Execute Hand Back Interface Success.");

            /**
             * 如果退款接口调用成功后设置退款单为正在退款状态
             */
            GlobalService.refundOrderService.setRefunding(refundOrderId);

            BigDecimal oldRefundPrice = trans.getBigDecimal(TablePaymentTransaction.refundPrice);
            if (oldRefundPrice == null) oldRefundPrice = new BigDecimal(0);
            BigDecimal oldRefundAmount = refundParams.getRefundAmount();
            if (oldRefundAmount == null) trans.getBigDecimal(TablePaymentTransaction.refundAmount);
            if (oldRefundAmount == null) oldRefundAmount = new BigDecimal(0);
            ModelObject update = new ModelObject(TablePaymentTransaction.class);
            update.put(TablePaymentTransaction.id, tid);
            update.put(TablePaymentTransaction.refundStatus, refundStatus.getCode());
            update.put(TablePaymentTransaction.refundPrice, CalcNumber.as(oldRefundPrice.add(refundPrice)).toPrice());
            update.put(TablePaymentTransaction.refundAmount,
                    CalcNumber.as(oldRefundAmount.add(refundParams.getRefundAmount() == null ? new BigDecimal(0) : refundParams.getRefundAmount())).toPrice());
            sessionTemplate.update(update);
            if (afterSalesId > 0) {
                GlobalService.afterSalesService.addProgress(afterSalesId, 0, "退款成功");
            }

            /**
             * 记录退款记录，以便以后查询使用
             */
            BigDecimal refundAmount = refundParams.getRefundAmount();
            if (refundAmount == null) refundAmount = refundPrice;
            ModelObject record = new ModelObject(TablePaymentRefund.class);
            record.put(TablePaymentRefund.type, refundType.getCode());
            record.put(TablePaymentRefund.refundOrderId, refundOrderId);
            record.put(TablePaymentRefund.afterSalesId, afterSalesId);
            record.put(TablePaymentRefund.payChannelId, trans.getLongValue(TablePaymentTransaction.payChannelId));
            record.put(TablePaymentRefund.payChannelName, paymentModeInterface.getPaymentShortName());
            record.put(TablePaymentRefund.refundPrice, refundPrice);
            record.put(TablePaymentRefund.refundAmount, refundAmount);
            record.put(TablePaymentRefund.createTime, new Date());
            sessionTemplate.save(record);
        }
    }

    @Override
    public void handBack(ModelObject order) throws ModuleException, TransactionException {
        long uid = order.getLongValue(TableOrder.userId);
        String orderNumber = order.getString(TableOrder.number);
        long orderId = order.getLongValue(TableOrder.id);
        List<ModelObject> transList = sessionTemplate.list(Criteria.query(TablePaymentTransaction.class)
                .subjoin(TablePaymentChannel.class).eq(TablePaymentChannel.id, TablePaymentTransaction.payChannelId)
                .childJoin(TablePaymentArgument.class).eq(TablePaymentArgument.channelId, TablePaymentChannel.id).parent()
                .single().query()
                .eq(TablePaymentTransaction.userId, uid)
                .eq(TablePaymentTransaction.type, PaymentTransactionType.ORDER.getCode())
                .eq(TablePaymentTransaction.number, orderNumber)
                .in(TablePaymentTransaction.payStatus,
                        Arrays.asList(OrderPayStatus.PAYED.getCode(), OrderPayStatus.PART_PAY.getCode())));

        if (transList != null) {
            BigDecimal orderPayPrice = order.getBigDecimal(TableOrder.payPrice);
            BigDecimal totalPayPrice = new BigDecimal("0");
            for (ModelObject trans : transList) {
                BigDecimal payPrice = trans.getBigDecimal(TablePaymentTransaction.payPrice);
                totalPayPrice = totalPayPrice.add(payPrice);
                trans.put("needRefundPrice", payPrice);
            }
            if (totalPayPrice.doubleValue() == orderPayPrice.doubleValue()) {

                if (transList != null && transList.size() > 0) {
                    for (ModelObject transItem : transList) {
                        BigDecimal tranPayPrice = transItem.getBigDecimal(TablePaymentTransaction.payPrice);
                        BigDecimal tranRefundPrice = transItem.getBigDecimal(TablePaymentTransaction.refundPrice);
                        if (tranRefundPrice == null || tranPayPrice.subtract(tranRefundPrice).doubleValue() > 0) {
                            handBack(order, transItem, PaymentRefundType.ORDER_CANCEL, 0, 0);
                        } else {
                            throw new ModuleException("pay_trans_out_of", "支付交易单退款金额超出支付金额,不允许重新退款");
                        }
                    }
                }
            } else {
                throw new ModuleException("order_&_pay_money_diff", "支付金额和订单金额不一致请手动检查");
            }
        }
    }

    @Override
    public void handBackAfterSale(long afterSalesId,
                                  long refundOrderId,
                                  long paymentTransactionId,
                                  BigDecimal refundMoney,
                                  PaymentTransactionType type) throws ModuleException {
        ModelObject afterSales = GlobalService.afterSalesService.getSimpleAfterSalesById(afterSalesId);
        long orderGoodsId = afterSales.getLongValue(TableAfterSales.orderGoodsId);
        ModelObject orderGoods = GlobalService.orderService.getOrderGoodsById(orderGoodsId);
        long orderId = orderGoods.getLongValue(TableOrderGoods.orderId);
        ModelObject order = GlobalService.orderService.getSimpleOrderById(orderId);

        ModelObject trans = sessionTemplate.get(Criteria.query(TablePaymentTransaction.class)
                .eq(TablePaymentTransaction.rid, orderId)
                .eq(TablePaymentTransaction.id, paymentTransactionId)
                .in(TablePaymentTransaction.payStatus, OrderPayStatus.PART_PAY.getCode(), OrderPayStatus.PAYED.getCode())
                .in(TablePaymentTransaction.refundStatus, OrderRefundStatus.INIT.getCode(), OrderPayStatus.PART_PAY.getCode())
                .eq(TablePaymentTransaction.type, type.getCode()));

        if (trans != null) {
            BigDecimal price = trans.getBigDecimal(TablePaymentTransaction.payPrice);
            BigDecimal refundPrice = trans.getBigDecimal(TablePaymentTransaction.refundPrice);
            if (refundPrice == null) refundPrice = new BigDecimal(0);
            long payChannelId = trans.getLongValue(TablePaymentTransaction.payChannelId);
            ModelObject payment = sessionTemplate.get(Criteria.query(TablePaymentChannel.class)
                    .subjoin(TablePaymentArgument.class).eq(TablePaymentArgument.channelId, TablePaymentChannel.id).query()
                    .eq(TablePaymentChannel.id, payChannelId));
            if (price.doubleValue() > refundPrice.doubleValue()) {
                trans.put(TablePaymentChannel.class, payment);
                trans.put("needRefundPrice", refundMoney);
                this.handBack(order, trans, PaymentRefundType.AFTER_SALES, afterSalesId, refundOrderId);
            } else {
                throw new ModuleException("refund_trans_not_enough", "交易单中退款金额超出支付金额");
            }
        } else {
            throw new ModuleException("not_found_payment_trans", "没有找到支付交易单");
        }
    }

    @Override
    public List<ModelObject> getTransactionByOrder(long uid, long oid, String number) {
        return sessionTemplate.list(Criteria.query(TablePaymentTransaction.class)
                .eq(TablePaymentTransaction.userId, uid)
                .eq(TablePaymentTransaction.rid, oid)
                .eq(TablePaymentTransaction.number, number)
                .ne(TablePaymentTransaction.payStatus, OrderPayStatus.NOT_PAY.getCode()));
    }

    @Override
    public List<ModelObject> getPaymentRefund(PaymentRefundType afterSales, long prid) {
        return sessionTemplate.list(Criteria.query(TablePaymentRefund.class)
                .eq(TablePaymentRefund.type, afterSales.getCode())
                .eq(TablePaymentRefund.afterSalesId, prid));
    }

    @Override
    public ModelObject getPaymentChannelByArguments(Map<String, String> valueMaps) {
        Iterator<Map.Entry<String, String>> iterator = valueMaps.entrySet().iterator();
        List<ModelObject> args = null;
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            keys.add(key);
            values.add(value);
        }

        List<ModelObject> objects = sessionTemplate.list(Criteria.query(TablePaymentArgument.class)
                .in(TablePaymentArgument.key, keys)
                .in(TablePaymentArgument.value, values));
        if (objects != null && objects.size() > 0) {
            long channelId = objects.get(0).getLongValue(TablePaymentArgument.channelId);
            List<ModelObject> channelArgs = sessionTemplate.list(Criteria.query(TablePaymentArgument.class)
                    .eq(TablePaymentArgument.channelId, channelId));

            return this.getCnfParams(channelArgs, channelId);
        }
        return null;
    }

    @Override
    public void setHandBackSuccess(String refundOrderNumber, String outTradeNo) {
        GlobalService.refundOrderService.setRefundSuccess(refundOrderNumber, outTradeNo);
    }
}
