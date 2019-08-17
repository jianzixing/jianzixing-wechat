package com.jianzixing.webapp.service.order;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.logistics.DeliveryType;
import com.jianzixing.webapp.service.trigger.EventType;
import com.jianzixing.webapp.tables.goods.TableGoods;
import com.jianzixing.webapp.tables.goods.TableGoodsComment;
import com.jianzixing.webapp.tables.goods.TableGoodsProperty;
import com.jianzixing.webapp.tables.goods.TableGoodsSku;
import com.jianzixing.webapp.tables.logistics.TableLogisticsCompany;
import com.jianzixing.webapp.tables.order.*;
import com.jianzixing.webapp.tables.payment.TablePaymentTransaction;
import com.jianzixing.webapp.tables.user.TableUser;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.mimosaframework.orm.AutoResult;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.criteria.Update;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.orm.transaction.Transaction;
import org.mimosaframework.orm.transaction.TransactionCallback;
import org.mimosaframework.orm.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DefaultOrderService implements OrderService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Autowired
    OrderCheckstandInterface orderCheckstandInterface;

    @Override
    public synchronized ModelObject addOrder(ModelObject order, List<ModelObject> products) throws ModuleException {
        try {
            return sessionTemplate.execute(new TransactionCallback<ModelObject>() {
                @Override
                public ModelObject invoke(Transaction transaction) throws Exception {
                    OrderModel orderModel = getOrderModel(order, products, true);
                    int deliveryType = orderModel.getDeliveryType();
                    if (deliveryType <= 0 || DeliveryType.get(deliveryType) == null) {
                        throw new ModuleException("delivery_empty", "配送方式不能为空");
                    }
                    checkOrderGoods(products);
                    ModelObject order = saveOrderInfo(orderModel);

                    ModelObject params = new ModelObject();
                    params.put("userId", orderModel.getUid());
                    params.put("number", orderModel.getOrderNumber());
                    params.put("payPrice", orderModel.getOrderPrice());
                    params.put("msg", "用户下单");
                    GlobalService.triggerService.trigger(orderModel.getUid(), EventType.PLACE_AN_ORDER, params);
                    return order;
                }
            });
        } catch (TransactionException e) {
            Throwable t = e.getCause();
            if (t instanceof ModuleException) {
                throw (ModuleException) t;
            } else {
                throw new ModuleException(StockCode.FAILURE, "事物执行失败:" + e.getMessage(), e);
            }
        }
    }

    private void checkOrderGoods(List<ModelObject> products) throws ModuleException {
        if (products == null || products.size() == 0) {
            throw new ModuleException(StockCode.ARG_NULL, "创建订单必须有商品信息");
        }
        List<Long> gid = new ArrayList<>();
        for (ModelObject product : products) {
            long pid = product.getLongValue("pid");
            if (gid.contains(pid)) {
                throw new ModuleException(StockCode.EXIST_OBJ, "当前订单中商品信息重复");
            }
            gid.add(pid);
        }
    }

    @Override
    public ModelObject getOrderPrices(ModelObject order, List<ModelObject> products) throws ModuleException {
        ModelObject prices = new ModelObject();
        if (products == null || products.size() == 0) {
            prices.put("goodsPrice", 0);
            prices.put("discountPrice", 0);
            prices.put("couponPrice", 0);
            prices.put("freightPrice", 0);
            prices.put("orderPrice", 0);
            return prices;
        }
        List<ModelObject> objects = GlobalService.logisticsService.getDeliveryTypes(products);
        if (objects != null && objects.size() > 0) {
            int defaultDeliveryType = objects.get(0).getIntValue("id");
            prices.put("deliveryTypes", objects);
            prices.put("deliveryType", defaultDeliveryType);
            order.put("deliveryType", defaultDeliveryType);
        }

        OrderModel orderModel = this.getOrderModel(order, products, false);
        prices.put("goodsPrice", CalcNumber.as(orderModel.getConstProductPrice()).toPrice());
        prices.put("discountPrice", CalcNumber.as(orderModel.getDiscountPrice()).toPrice());
        prices.put("couponPrice", CalcNumber.as(orderModel.getCouponPrice()).toPrice());
        prices.put("freightPrice", CalcNumber.as(orderModel.getFreightPrice()).toPrice());
        prices.put("orderPrice", CalcNumber.as(orderModel.getOrderPrice()).toPrice());
        List<OrderDiscountModel> orderDiscountModels = orderModel.getCoupons();
        if (orderDiscountModels != null) {
            prices.put("useCouponCount", orderDiscountModels.size());
        } else {
            prices.put("useCouponCount", 0);
        }

        List<OrderGoodsModel> models = orderModel.getProducts();
        if (models != null) {
            List<ModelObject> goods = new ArrayList<>();
            for (OrderGoodsModel model : models) {
                ModelObject goodsItem = model.getGoods();
                long gid = model.getGid();
                boolean isExist = false;
                for (ModelObject g : goods) {
                    if (g.getLongValue("buyGoodsId") == gid) {
                        isExist = true;
                        g.put("buyAmount", g.getIntValue("buyAmount") + model.getBuyAmount());
                        break;
                    }
                }
                if (isExist) continue;

                goodsItem.put("buyGoodsId", gid);
                goodsItem.put("buyAmount", model.getBuyAmount());
                goodsItem.put("buySkuId", model.getSkuId());
                goods.add(goodsItem);
            }
            prices.put("buyGoods", goods);
        }

        return prices;
    }

    private OrderModel getOrderModel(ModelObject order, List<ModelObject> products, boolean isCreateOrder) throws ModuleException {
        if (order != null && products != null && products.size() > 0) {
            order.clearEmpty();
            long uid = order.getLongValue("uid");
            long addressId = order.getLongValue("aid");
            long couponId = order.getLongValue("couponId");
            String message = order.getString("message");
            ModelObject invoice = order.getModelObject("invoice");

            /**
             * 验证收货地址和获取支付平台类型
             */
            int deliveryType = order.getIntValue("deliveryType");
            int platform = order.getIntValue("platform");
            if (uid == 0) throw new ModuleException(StockCode.ARG_NULL, "用户ID必须存在");
            if (addressId == 0) throw new ModuleException(StockCode.ARG_NULL, "用户收货地址ID必须存在");
            if (products == null || products.size() == 0) throw new ModuleException(StockCode.ARG_NULL, "订单商品不能为空");

            /**
             * 验证用户信息是否完整
             */
            ModelObject user = GlobalService.userService.getUser(uid);
            if (user == null) throw new ModuleException(StockCode.ARG_NULL, "用户不存在");
            ModelObject address = GlobalService.userAddressService.getUserAddressById(uid, addressId);
            if (address == null) throw new ModuleException(StockCode.ARG_NULL, "用户地址不存在");

            /**
             * 验证商品信息是否完整
             */
            List<Long> goodsKeys = new ArrayList<>();
            List<OrderGoodsModel> orderProductModels = new ArrayList<>();
            for (ModelObject p : products) {
                OrderGoodsModel model = new OrderGoodsModel();
                long productId = p.getLongValue("pid");
                model.setGid(productId);
                goodsKeys.add(productId);
                int buyAmount = p.getIntValue("buyAmount");
                model.setBuyAmount(buyAmount);
                long skuId = p.getLongValue("skuId");
                model.setSkuId(skuId);
                long discountId = p.getLongValue("discountId");
                if (discountId > 0) {
                    model.setDiscountId(discountId);
                }

                ModelObject goods = p.getModelObject("goods");
                if (goods == null) {
                    goods = GlobalService.goodsService.getGoodsById(productId);
                }
                if (goods == null) {
                    throw new ModuleException(StockCode.ARG_NULL, "商品" + productId + "不存在");
                }
                model.setGoods(goods);
                if (GlobalService.goodsService.hasSku(goods)) {
                    ModelObject sku = goods.getModelObject(TableGoodsSku.class.getSimpleName());
                    if (sku == null) {
                        sku = GlobalService.goodsService.getSkuById(skuId);
                    }
                    model.setSku(sku);
                }
                orderProductModels.add(model);
            }


            /**
             * 判断当前商品的配送方式是否允许
             */
            List<ModelObject> deliveryGoodsTypes = GlobalService.logisticsService.getGoodsLogistics(goodsKeys);
            boolean bool = false, free = false;
            for (ModelObject dgt : deliveryGoodsTypes) {
                int type = dgt.getIntValue("type");
                if (type == deliveryType) bool = true;
                if (dgt.getIntValue("free") == DeliveryType.FREE.getCode()) free = true;
            }
            if (!bool && !free) {
                throw new ModuleException("delivery_type_fail", "配送方式不存在");
            }


            OrderModel orderModel = new OrderModel();
            orderModel.setOrderCreate(isCreateOrder);
            orderModel.setUid(uid);
            orderModel.setOrderNumber(getOrderNumber());
            orderModel.setUser(user);
            orderModel.setAid(addressId);
            orderModel.setCouponId(couponId);
            orderModel.setAddress(address);
            orderModel.setDeliveryType(deliveryType);
            orderModel.setPlatformType(PlatformType.get(platform));
            orderModel.setProducts(orderProductModels);
            OrderModel checkout = orderCheckstandInterface.checkin(orderModel);

            orderModel.setProductPrice(checkout.getProductPrice());
            orderModel.setFreightPrice(checkout.getFreightPrice());
            orderModel.setDiscountPrice(checkout.getDiscountPrice());
            orderModel.setDiscounts(checkout.getDiscounts());
            orderModel.setOrderPrice(checkout.getOrderPrice());
            orderModel.setMessage(message);
            orderModel.setInvoice(invoice);
            return orderModel;
        }
        return null;
    }

    @Override
    public void cancelOrder(ModelObject order) throws ModuleException, TransactionException {
        if (order != null) {
            long orderId = order.getLongValue(TableOrder.id);
            int status = order.getIntValue(TableOrder.status);
            int payStatus = order.getIntValue(TableOrder.payStatus);
            if (status != OrderStatus.CREATE.getCode()
                    && status != OrderStatus.PAY.getCode()) {
                throw new ModuleException(StockCode.NOT_ALLOW, "当前订单状态不允许取消");
            }

            List<ModelObject> orderGoods = sessionTemplate.list(
                    Criteria.query(TableOrderGoods.class).eq(TableOrderGoods.orderId, orderId)
            );

            if (orderGoods != null) {
                sessionTemplate.execute(new TransactionCallback<Boolean>() {
                    @Override
                    public Boolean invoke(Transaction transaction) throws Exception {
                        // 退回商品库存
                        for (ModelObject object : orderGoods) {
                            long gid = object.getLongValue(TableOrderGoods.goodsId);
                            long skuId = object.getLongValue(TableOrderGoods.skuId);
                            int buyAmount = object.getIntValue(TableOrderGoods.amount);
                            GlobalService.goodsService.increaseAmount(gid, skuId, buyAmount);
                        }

                        // 更改订单状态
                        ModelObject object = new ModelObject(TableOrder.class);
                        object.put(TableOrder.id, order.getLongValue(TableOrder.id));
                        object.put(TableOrder.status, OrderStatus.CANCEL.getCode());
                        object.put(TableOrder.cancelTime, new Date());

                        // 退回订单相关所有信息，优惠券等等
                        orderCheckstandInterface.checkout(order);

                        // 如果订单已经支付或者部分支付则开始退款
                        if (payStatus == OrderPayStatus.PAYED.getCode()
                                || payStatus == OrderPayStatus.PART_PAY.getCode()) {
                            GlobalService.paymentService.handBack(order);
                            object.put(TableOrder.refundStatus, OrderRefundStatus.FULL.getCode());
                        }

                        sessionTemplate.update(object);

                        // 判断订单是否支付
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public void cancelOrder(long orderId) throws ModuleException, TransactionException {
        ModelObject order = sessionTemplate.get(Criteria.query(TableOrder.class)
                .eq(TableOrder.id, orderId));
        this.cancelOrder(order);
    }

    @Override
    public void cancelOrder(long uid, long orderId) throws ModuleException, TransactionException {
        ModelObject order = sessionTemplate.get(Criteria.query(TableOrder.class)
                .eq(TableOrder.userId, uid)
                .eq(TableOrder.id, orderId)
                .eq(TableOrder.isDel, 0));
        this.cancelOrder(order);
    }

    @Override
    public Paging getOrders(ModelObject search, List<Integer> status, long start, long limit) {
        Query query = Criteria.query(TableOrder.class);
        if (status != null) {
            query.in(TableOrder.status, status);
        } else {
            if (search != null && search.isNotEmpty("status")) {
                query.eq(TableOrder.status, search.getIntValue("status"));
            }
        }
        setQueryOrderSet(query);
        query.limit(start, limit);
        query.order(TableOrder.id, false);

        if (search != null) {
            if (search.isNotEmpty("number")) {
                query.eq(TableOrder.number, search.getString("number"));
            }
            if (search.isNotEmpty("userName")) {
                ModelObject user = GlobalService.userService.getUser(search.getString("userName"), null, null);
                if (user != null) query.eq(TableOrder.userId, user.getLongValue(TableUser.id));
            }
            if (search.isNotEmpty("payStatus")) {
                query.eq(TableOrder.payStatus, search.getString("payStatus"));
            }
            if (search.isNotEmpty("createTimeStart")) {
                query.gte(TableOrder.createTime, search.getString("createTimeStart"));
            }
            if (search.isNotEmpty("createTimeEnd")) {
                query.lte(TableOrder.createTime, search.getString("createTimeEnd"));
            }
        }

        Paging paging = sessionTemplate.paging(query);

        if (paging != null) {
            List<ModelObject> objects = paging.getObjects();
            if (objects != null) {
                for (ModelObject object : objects) {
                    this.clearInvalidPaymentTrans(object);
                }
            }
        }
        return paging;
    }

    @Override
    public Paging getSearchOrder() throws ModuleException {
        ModelObject params = new ModelObject();
        params.put(TableUser.userName, "");
        try {
            AutoResult totalResult = sessionTemplate.getAutonomously("getSearchOrderCount", params);
            AutoResult keyResult = sessionTemplate.getAutonomously("getSearchOrder", params);

            Query query = Criteria.query(TableOrder.class);
            setQueryOrderSet(query);
            query.in(TableOrder.id, keyResult.getNumbers());
            Paging paging = sessionTemplate.paging(query);
            paging.setCount(totalResult.longValue());
            return paging;
        } catch (SQLException e) {
            throw new ModuleException(StockCode.FAILURE, "查询订单出错", e);
        }
    }

    private void setQueryOrderSet(Query query) {
        query.eq(TableOrder.isDel, 0);
        query.subjoin(TableUser.class).eq(TableUser.id, TableOrder.userId).single();
        query.subjoin(TableOrderGoods.class).eq(TableOrderGoods.orderId, TableOrder.id);
        query.subjoin(TableOrderGoodsProperty.class).eq(TableOrderGoodsProperty.orderId, TableOrder.id);
        query.subjoin(TableOrderAddress.class).eq(TableOrderAddress.orderId, TableOrder.id).single();
        query.subjoin(TablePaymentTransaction.class).eq(TablePaymentTransaction.rid, TableOrder.id);
    }

    @Override
    public void deleteOrder(long orderId) throws TransactionException {
        ModelObject order = sessionTemplate.get(TableOrder.class, orderId);
        sessionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean invoke(Transaction transaction) throws Exception {
                sessionTemplate.update(Criteria.update(TableOrder.class)
                        .eq(TableOrder.id, orderId)
                        .value(TableOrder.isDel, 1));
                orderCheckstandInterface.checkout(order);
                return true;
            }
        });
    }

    @Override
    public ModelObject getSimpleOrderById(long orderId) {
        return sessionTemplate.get(
                Criteria.query(TableOrder.class)
                        .eq(TableOrder.id, orderId)
                        .eq(TableOrder.isDel, 0)
        );
    }

    @Override
    public ModelObject getSimpleOrderByNumber(String orderNumber) {
        return sessionTemplate.get(
                Criteria.query(TableOrder.class)
                        .eq(TableOrder.number, orderNumber)
                        .eq(TableOrder.isDel, 0)
        );
    }

    @Override
    public ModelObject getOrderById(long uid, long orderId) {
        return sessionTemplate.get(
                Criteria.query(TableOrder.class)
                        .eq(TableOrder.id, orderId)
                        .eq(TableOrder.userId, uid)
                        .subjoin(TableOrderGoods.class).eq(TableOrderGoods.orderId, TableOrder.id).query()
                        .subjoin(TableOrderInvoice.class).eq(TableOrderInvoice.orderId, TableOrder.id).single().query()
                        .subjoin(TableOrderAddress.class).eq(TableOrderAddress.orderId, TableOrder.id).single().query()
                        .subjoin(TableOrderInvoice.class).eq(TableOrderInvoice.orderId, TableOrder.id).query()
        );
    }

    @Override
    public ModelObject getOrderById(long orderId) {
        Query query = Criteria.query(TableOrder.class);
        this.setQueryOrderSet(query);
        query.eq(TableOrder.id, orderId);
        ModelObject order = sessionTemplate.get(query);
        this.clearInvalidPaymentTrans(order);
        return order;
    }

    private void clearInvalidPaymentTrans(ModelObject order) {
        List<ModelObject> trans = order.getArray(TablePaymentTransaction.class);
        if (trans != null) {
            List<ModelObject> oks = new ArrayList<>();
            for (ModelObject o : trans) {
                if (o.getIntValue(TablePaymentTransaction.payStatus) == OrderPayStatus.PART_PAY.getCode()
                        || o.getIntValue(TablePaymentTransaction.payStatus) == OrderPayStatus.PAYED.getCode()) {
                    oks.add(o);
                }
            }
            order.put(TablePaymentTransaction.class, oks);
        }
    }

    @Override
    public void updateOrderStatus(long orderId, int code) {
        ModelObject order = this.getSimpleOrderById(orderId);
        int status = order.getIntValue(TableOrder.status);
        if (status == OrderStatus.PAY.getCode()) {
            if (code == OrderStatus.DELIVERY.getCode()
                    || code == OrderStatus.LOGISTICS.getCode()
                    || code == OrderStatus.RECEIVE.getCode()
                    || code == OrderStatus.SURE.getCode()
                    || code == OrderStatus.FINISH.getCode()) {
                ModelObject update = new ModelObject(TableOrder.class);
                update.put(TableOrder.id, orderId);
                update.put(TableOrder.status, code);
                update.put(TableOrder.payTime, new Date());
                sessionTemplate.update(update);
            }
        }
        if (code == OrderStatus.FINISH.getCode()) {
            if (status == OrderStatus.RECEIVE.getCode()
                    || status == OrderStatus.LOGISTICS.getCode()
                    || status == OrderStatus.DELIVERY.getCode()) {
                ModelObject update = new ModelObject(TableOrder.class);
                update.put(TableOrder.id, orderId);
                update.put(TableOrder.status, code);
                sessionTemplate.update(update);
            }
        }
    }

    @Override
    public void sendOutOrder(long orderId, String logisticsCompanyCode, String trackingNumber) throws ModuleException {
        ModelObject order = this.getSimpleOrderById(orderId);
        if (order == null) {
            throw new ModuleException(StockCode.NOT_EXIST, "订单不存在");
        }
        int status = order.getIntValue(TableOrder.status);
        if (status == OrderStatus.PAY.getCode() || status == OrderStatus.SURE.getCode()) {
            Update update = Criteria.update(TableOrder.class);
            update.eq(TableOrder.id, orderId);
            update.eq(TableOrder.status, status);

            ModelObject logisticsCompany = GlobalService.logisticsService.getCompanyByCode(logisticsCompanyCode);
            if (StringUtils.isNotBlank(trackingNumber) && logisticsCompany != null) {
                update.value(TableOrder.lgsCompanyCode, logisticsCompany.getString(TableLogisticsCompany.code));
                update.value(TableOrder.lgsCompanyName, logisticsCompany.getString(TableLogisticsCompany.name));
                update.value(TableOrder.trackingNumber, trackingNumber);
            }
            update.value(TableOrder.status, OrderStatus.LOGISTICS.getCode());
            update.value(TableOrder.sendTime, new Date());
            long count = sessionTemplate.update(update);
            if (count == 0) {
                throw new ModuleException(StockCode.FAILURE, "发货失败更新条数为零");
            }
        } else {
            throw new ModuleException(StockCode.STATUS_ERROR, "订单状态不允许改变");
        }
    }

    @Override
    public Paging getStatusOrders(Query query, long uid, OrderStatus status, long start, long limit) {
        if (query == null) query = Criteria.query(TableOrder.class);
        query.subjoin(TableOrderGoods.class).eq(TableOrderGoods.orderId, TableOrder.id);
        query.eq(TableOrder.userId, uid);
        if (status != null) {
            query.eq(TableOrder.status, status.getCode());
        }
        query.order(TableOrder.id, false);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public void updateOrderLogistics(long orderId, String code, String trackingNumber) throws ModuleException {
        ModelObject order = this.getSimpleOrderById(orderId);
        if (order == null) {
            throw new ModuleException(StockCode.NOT_EXIST, "订单不存在");
        }
        int status = order.getIntValue(TableOrder.status);
        if (status == OrderStatus.LOGISTICS.getCode()) {
            Update update = Criteria.update(TableOrder.class);
            update.eq(TableOrder.id, orderId);
            update.eq(TableOrder.status, status);

            ModelObject logisticsCompany = GlobalService.logisticsService.getCompanyByCode(code);
            if (StringUtils.isNotBlank(trackingNumber) && logisticsCompany != null) {
                update.value(TableOrder.lgsCompanyCode, logisticsCompany.getString(TableLogisticsCompany.code));
                update.value(TableOrder.lgsCompanyName, logisticsCompany.getString(TableLogisticsCompany.name));
                update.value(TableOrder.trackingNumber, trackingNumber);
            }
            update.value(TableOrder.sendTime, new Date());
            sessionTemplate.update(update);
        } else {
            throw new ModuleException(StockCode.STATUS_ERROR, "当前订单状态不允许更新配送信息");
        }
    }

    @Override
    public void setOrderDelivery(long id) throws ModuleException {
        ModelObject order = this.getSimpleOrderById(id);
        if (order == null) {
            throw new ModuleException(StockCode.NOT_EXIST, "订单不存在");
        }

        int status = order.getIntValue(TableOrder.status);
        if (status == OrderStatus.LOGISTICS.getCode()) {
            Update update = Criteria.update(TableOrder.class);
            update.eq(TableOrder.id, id);
            update.eq(TableOrder.status, status);
            update.value(TableOrder.status, OrderStatus.DELIVERY.getCode());
            sessionTemplate.update(update);
        }
    }

    @Override
    public void updateOrderPrice(long id, String price) throws ModuleException {
        ModelObject order = this.getSimpleOrderById(id);
        if (order == null) {
            throw new ModuleException(StockCode.NOT_EXIST, "订单不存在");
        }

        int status = order.getIntValue(TableOrder.status);
        int payStatus = order.getIntValue(TableOrder.payStatus);
        if (status == OrderStatus.CREATE.getCode()
                && payStatus == 0) {
            Update update = Criteria.update(TableOrder.class);
            update.eq(TableOrder.id, id);
            update.eq(TableOrder.status, status);
            update.value(TableOrder.payPrice, CalcNumber.as(price).toPrice());
            sessionTemplate.update(update);
        }
    }

    @Override
    public void updateOrderAddress(ModelObject object) throws ModuleException, ModelCheckerException {
        long orderId = object.getLongValue("orderId");
        ModelObject order = this.getSimpleOrderById(orderId);
        if (order == null) {
            throw new ModuleException(StockCode.NOT_EXIST, "订单不存在");
        }

        int status = order.getIntValue(TableOrder.status);
        int payStatus = order.getIntValue(TableOrder.payStatus);
        if (status == OrderStatus.PAY.getCode()
                && payStatus == 1) {
            int source = object.getIntValue("source");
            ModelObject address = null;
            if (source == 1) {
                int cid = object.getIntValue("aid");
                address = GlobalService.userAddressService.getUserAddressById(cid);
            } else {
                address = object;
            }

            ModelObject orderAddress = new ModelObject(TableOrderAddress.class);
            orderAddress.put(TableOrderAddress.orderId, orderId);
            orderAddress.put(TableOrderAddress.realName, address.getString(TableUserAddress.realName));
            orderAddress.put(TableOrderAddress.phoneNumber, address.getString(TableUserAddress.phoneNumber));
            orderAddress.put(TableOrderAddress.telNumber, address.getString(TableUserAddress.telNumber));
            orderAddress.put(TableOrderAddress.country, address.getString(TableUserAddress.country));
            orderAddress.put(TableOrderAddress.province, address.getString(TableUserAddress.province));
            orderAddress.put(TableOrderAddress.city, address.getString(TableUserAddress.city));
            orderAddress.put(TableOrderAddress.county, address.getString(TableUserAddress.county));
            orderAddress.put(TableOrderAddress.address, address.getString(TableUserAddress.address));
            orderAddress.put(TableOrderAddress.email, address.getString(TableUserAddress.email));
            orderAddress.checkUpdateThrowable();
            sessionTemplate.update(orderAddress);
        } else {
            throw new ModuleException(StockCode.FAILURE, "只有刚支付的订单才可以修改地址");
        }
    }

    @Override
    public ModelObject getOrderGoodsById(long orderGoodsId) {
        return sessionTemplate.get(TableOrderGoods.class, orderGoodsId);
    }

    @Override
    public List<ModelObject> getOrderGoods(long orderId) {
        return sessionTemplate.list(Criteria.query(TableOrderGoods.class)
                .eq(TableOrderGoods.orderId, orderId));
    }

    @Override
    public ModelObject getOrderGoodsWithSku(long orderGoodsId) {
        Query query = Criteria.query(TableOrderGoods.class);

        query.eq(TableOrderGoods.id, orderGoodsId);
        query.subjoin(TableOrderGoodsProperty.class)
                .eq(TableOrderGoodsProperty.orderId, TableOrderGoods.orderId)
                .eq(TableOrderGoodsProperty.goodsId, TableOrderGoods.goodsId);

        ModelObject orderGoods = sessionTemplate.get(query);
        if (orderGoods != null && orderGoods.isNotEmpty(TableOrderGoodsProperty.class)) {
            List<ModelObject> property = orderGoods.getArray(TableOrderGoodsProperty.class);
            if (property != null) {
                List<ModelObject> ps = orderGoods.getArray(TableOrderGoodsProperty.class);
                if (ps != null && ps.size() > 0) {
                    List<String> names = new ArrayList<>();
                    for (ModelObject p : ps) {
                        names.add(p.getString(TableOrderGoodsProperty.attrName) + ":" + p.getString(TableOrderGoodsProperty.valueName));
                    }
                    orderGoods.put("goodsSkuNameList", names);
                    orderGoods.put("goodsSkuName", String.join(";", names));
                }
            }
        }
        return orderGoods;
    }

    @Override
    public Date getOrderExpireTime(Date orderTime) {
        String orderExpireTime = GlobalService.systemConfigService.getValue("order_expire_time");
        long time = 24 * 60 * 60 * 1000l;
        if (StringUtils.isNotBlank(orderExpireTime)) {
            time = Long.parseLong(orderExpireTime) * 1000;
        }

        return new Date(orderTime.getTime() + time);
    }

    @Override
    public void setOrderPaySuccess(String orderNumber) {
        ModelObject order = this.getSimpleOrderByNumber(orderNumber);
        if (order != null) {
            int payStatus = order.getIntValue(TableOrder.payStatus);
            int status = order.getIntValue(TableOrder.status);
            if (status == OrderStatus.CREATE.getCode()
                    && payStatus != OrderPayStatus.PAYED.getCode()) {
                sessionTemplate.update(
                        Criteria.update(TableOrder.class)
                                .value(TableOrder.payStatus, OrderPayStatus.PAYED.getCode())
                                .value(TableOrder.status, OrderStatus.PAY.getCode())
                                .value(TableOrder.payTime, new Date())
                                .eq(TableOrder.number, orderNumber)
                );

                ModelObject params = new ModelObject();
                params.put("userId", order.getLongValue(TableOrder.userId));
                params.put("payPrice", order.getBigDecimal(TableOrder.payPrice));
                params.put("msg", "支付成功");
                GlobalService.triggerService.trigger(order.getLongValue(TableOrder.userId), EventType.ORDER_PAYED_SUCCESS, params);
            }
        }
    }

    @Override
    public ModelObject getOrderByNumber(long uid, String number, OrderStatus orderStatus) {
        if (uid > 0 && StringUtils.isNotBlank(number) && orderStatus != null) {
            ModelObject order = sessionTemplate.get(Criteria.query(TableOrder.class)
                    .subjoin(TableOrderGoods.class).eq(TableOrderGoods.orderId, TableOrder.id).query()
                    .subjoin(TableOrderGoodsProperty.class).eq(TableOrderGoodsProperty.orderId, TableOrder.id).query()
                    .eq(TableOrder.number, number)
                    .eq(TableOrder.status, orderStatus.getCode())
                    .eq(TableOrder.userId, uid));

            if (order != null) {
                List<ModelObject> goods = order.getArray(TableOrderGoods.class.getSimpleName());
                List<ModelObject> property = order.getArray(TableOrderGoodsProperty.class.getSimpleName());
                if (goods != null && property != null && property.size() > 0) {
                    for (ModelObject g : goods) {
                        long gid = g.getLongValue(TableOrderGoods.goodsId);
                        long skuId = g.getLongValue(TableOrderGoods.skuId);
                        List<String> names = new ArrayList<>();
                        for (ModelObject p : property) {
                            if (p.getLongValue(TableOrderGoods.goodsId) == gid
                                    && p.getLongValue(TableOrderGoods.skuId) == skuId) {
                                names.add(p.getString(TableOrderGoodsProperty.valueName));
                            }
                        }
                        g.put("goodsSkuNameList", names);
                        g.put("goodsSkuName", String.join(",", names));
                    }
                }
            }

            return order;
        }
        return null;
    }

    @Override
    public void setOrderPayPartSuccess(String orderNumber) {
        ModelObject order = this.getSimpleOrderByNumber(orderNumber);
        if (order != null) {
            int payStatus = order.getIntValue(TableOrder.payStatus);
            int status = order.getIntValue(TableOrder.status);
            if (status == OrderStatus.CREATE.getCode()
                    && payStatus != OrderPayStatus.PAYED.getCode()
                    && payStatus != OrderPayStatus.PART_PAY.getCode()) {
                sessionTemplate.update(
                        Criteria.update(TableOrder.class)
                                .value(TableOrder.payStatus, OrderPayStatus.PART_PAY.getCode())
                                .eq(TableOrder.number, orderNumber)
                );
            }
        }
    }

    @Override
    public List<ModelObject> getUserOrderByPage(long uid, List<String> status, int page) {
        int limit = 10;
        int start = (page - 1) * limit;

        Query query = Criteria.query(TableOrder.class);
        query.eq(TableOrder.userId, uid);
        query.eq(TableOrder.isDel, 0);
        if (status != null && status.size() > 0) {
            query.in(TableOrder.status, status);
        }
        query.subjoin(TableOrderGoods.class).eq(TableOrderGoods.orderId, TableOrder.id);
        query.limit(start, limit);
        query.order(TableOrder.id, false);
        List<ModelObject> orders = sessionTemplate.list(query);
        if (orders != null) {
            for (ModelObject order : orders) {
                Date createTime = order.getDate(TableOrder.createTime);
                order.put(TableOrder.createTime, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(createTime));
            }
        }

        return orders;
    }

    @Override
    public List<ModelObject> searchUserOrderByPage(long uid, String keyword, int page) {
        int limit = 10;
        int start = (page - 1) * limit;
        if (StringUtils.isBlank(keyword)) {
            return null;
        }
        ModelObject params = new ModelObject();
        params.put("uid", uid);
        params.put("keyword", "%" + keyword + "%");
        params.put("start", start);
        params.put("limit", limit);

        Query query = Criteria.query(TableOrder.class);
        query.subjoin(TableOrderGoods.class).eq(TableOrderGoods.orderId, TableOrder.id);
        query.order(TableOrder.id, false);

        List<ModelObject> orders = ModelUtils.getSearchByName("order.searchUserOrder", sessionTemplate, params, query, TableOrder.id);
        if (orders != null) {
            for (ModelObject order : orders) {
                Date createTime = order.getDate(TableOrder.createTime);
                order.put(TableOrder.createTime, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(createTime));
            }
        }
        return orders;
    }

    @Override
    public void deleteUserOrder(long uid, long oid) {
        ModelObject order = sessionTemplate.get(Criteria.query(TableOrder.class)
                .eq(TableOrder.userId, uid)
                .eq(TableOrder.id, oid));
        if (order != null) {
            int status = order.getIntValue(TableOrder.status);
            if (status == OrderStatus.CANCEL.getCode()
                    || status == OrderStatus.RECEIVE.getCode()
                    || status == OrderStatus.FINISH.getCode()) {
                sessionTemplate.update(Criteria.update(TableOrder.class)
                        .eq(TableOrder.id, oid)
                        .eq(TableOrder.userId, uid)
                        .value(TableOrder.isDel, 1));
            }
        }
    }

    @Override
    public ModelObject getUserOrderById(long uid, long oid) {
        Query query = Criteria.query(TableOrder.class);

        query.eq(TableOrder.userId, uid);
        query.eq(TableOrder.id, oid);
        query.eq(TableOrder.isDel, 0);

        query.subjoin(TableOrderGoods.class).eq(TableOrderGoods.orderId, TableOrder.id);
        query.subjoin(TableOrderGoodsProperty.class).eq(TableOrderGoodsProperty.orderId, TableOrder.id);
        query.subjoin(TableOrderAddress.class).eq(TableOrderAddress.orderId, TableOrder.id).single();
        query.subjoin(TableOrderInvoice.class).eq(TableOrderInvoice.orderId, TableOrder.id).single();


        ModelObject order = sessionTemplate.get(query);
        if (order != null && order.containsKey(TableOrderGoods.class)) {
            List<ModelObject> goods = order.getArray(TableOrderGoods.class);
            for (ModelObject g : goods) {
                g.put("isCommented", 0);
            }
        }

        if (order != null && order.isNotEmpty(TableOrderGoodsProperty.class) && order.isNotEmpty(TableOrderGoods.class)) {
            List<ModelObject> goods = order.getArray(TableOrderGoods.class);
            List<ModelObject> property = order.getArray(TableOrderGoodsProperty.class);

            List<Long> orderGoodsIds = new ArrayList<>();
            if (property != null && goods != null) {
                for (ModelObject g : goods) {
                    long orderGoodsId = g.getLongValue(TableOrderGoods.goodsId);
                    long orderSkuId = g.getLongValue(TableOrderGoods.skuId);
                    for (ModelObject p : property) {
                        long goodsId = p.getLongValue(TableOrderGoodsProperty.goodsId);
                        long skuId = p.getLongValue(TableOrderGoodsProperty.skuId);
                        if (orderGoodsId == goodsId && orderSkuId == skuId) {
                            List<ModelObject> ps = g.getArray(TableOrderGoodsProperty.class);
                            if (ps == null) {
                                ps = new ArrayList<>();
                            }
                            ps.add(p);
                            g.put(TableOrderGoodsProperty.class, ps);
                        }
                    }
                    orderGoodsIds.add(g.getLongValue(TableOrderGoods.id));
                }

                for (ModelObject g : goods) {
                    List<ModelObject> ps = g.getArray(TableOrderGoodsProperty.class);
                    if (ps != null && ps.size() > 0) {
                        List<String> names = new ArrayList<>();
                        for (ModelObject p : ps) {
                            names.add(p.getString(TableOrderGoodsProperty.valueName));
                        }
                        g.put("goodsSkuNameList", names);
                        g.put("goodsSkuName", String.join(",", names));
                    }
                }
            }

            List<ModelObject> comments = GlobalService.goodsCommentService.getCommentsByOrderGoods(oid, orderGoodsIds);
            if (comments != null) {
                for (ModelObject comment : comments) {
                    for (ModelObject g : goods) {
                        if (g.getLongValue(TableOrderGoods.id) == comment.getLongValue(TableGoodsComment.orderGoodsId)) {
                            g.put("isCommented", 1);
                        }
                    }
                }
            }
        }

        if (order != null) {
            String number = order.getString(TableOrder.number);
            List<ModelObject> payments = GlobalService.paymentService.getTransactionByOrder(uid, oid, number);
            if (payments != null) {
                order.put(TablePaymentTransaction.class, payments);
                Set<String> names = new LinkedHashSet<>();
                for (ModelObject payment : payments) {
                    names.add(payment.getString(TablePaymentTransaction.payChannelName));
                }
                order.put("paymentNameList", names);
                order.put("paymentName", String.join(",", names));
            }
        }

        return order;
    }

    @Override
    public void confirmOrder(long uid, long oid) {
        ModelObject order = sessionTemplate.get(
                Criteria.query(TableOrder.class)
                        .eq(TableOrder.id, oid)
                        .eq(TableOrder.userId, uid)
                        .eq(TableOrder.isDel, 0)
        );
        if (order != null) {
            int payStatus = order.getIntValue(TableOrder.payStatus);
            int status = order.getIntValue(TableOrder.status);
            if ((status == OrderStatus.LOGISTICS.getCode() || status == OrderStatus.DELIVERY.getCode())
                    && payStatus == OrderPayStatus.PAYED.getCode()) {
                sessionTemplate.update(
                        Criteria.update(TableOrder.class)
                                .value(TableOrder.status, OrderStatus.RECEIVE.getCode())
                                .value(TableOrder.getTime, new Date())
                                .eq(TableOrder.id, oid)
                );

                ModelObject params = new ModelObject();
                params.put("userId", order.getLongValue(TableOrder.userId));
                params.put("number", order.getString(TableOrder.number));
                params.put("payPrice", order.getBigDecimal(TableOrder.payPrice));
                params.put("msg", "订单完成");
                GlobalService.triggerService.trigger(order.getLongValue(TableOrder.userId), EventType.ORDER_USER_CONFIRM, params);

                GlobalService.userService.updateUserLevelAmount(order.getLongValue(TableOrder.userId),
                        order.getBigDecimal(TableOrder.payPrice).intValue(),
                        "用户下单并确认收货");
            }
        }
    }

    @Override
    public void setOrderComment(long uid, long oid) {
        ModelObject order = sessionTemplate.get(
                Criteria.query(TableOrder.class)
                        .eq(TableOrder.id, oid)
                        .eq(TableOrder.userId, uid)
                        .eq(TableOrder.isDel, 0)
        );

        if (order != null) {
            int payStatus = order.getIntValue(TableOrder.payStatus);
            int status = order.getIntValue(TableOrder.status);
            if ((status == OrderStatus.RECEIVE.getCode() || status == OrderStatus.FINISH.getCode())
                    && payStatus == OrderPayStatus.PAYED.getCode()) {
                sessionTemplate.update(
                        Criteria.update(TableOrder.class)
                                .value(TableOrder.discussStatus, 1)
                                .eq(TableOrder.id, oid)
                );
            }
        }
    }

    @Override
    public void setLastAfterSaleType(long orderGoodsId, int afterSaleType) {
        ModelObject update = new ModelObject(TableOrderGoods.class);
        update.put(TableOrderGoods.id, orderGoodsId);
        update.put(TableOrderGoods.afterSaleType, afterSaleType);
        sessionTemplate.update(update);
    }

    @Override
    public ModelObject getOrderAddress(long orderId) {
        return sessionTemplate.get(Criteria.query(TableOrderAddress.class)
                .eq(TableOrderAddress.orderId, orderId));
    }

    @Override
    public long getOrderCount(OrderStatus... statuses) {
        List<Integer> ins = new ArrayList<>();
        for (OrderStatus status : statuses) {
            ins.add(status.getCode());
        }
        return sessionTemplate.count(Criteria.query(TableOrder.class)
                .in(TableOrder.status, ins));
    }

    private ModelObject saveOrderInfo(OrderModel model) throws ModuleException, ModelCheckerException {
        ModelObject order = new ModelObject(TableOrder.class);
        order.put(TableOrder.userId, model.getUid());
        order.put(TableOrder.number, model.getOrderNumber());
        order.put(TableOrder.addressId, model.getAid());
        order.put(TableOrder.deliveryId, model.getDeliveryType()); // 配送方式ID ， 快递 EMS 平邮

        order.put(TableOrder.costPrice, CalcNumber.as(model.getConstProductPrice()).toPrice());
        order.put(TableOrder.totalGoodsPrice, CalcNumber.as(model.getProductPrice()).toPrice());
        order.put(TableOrder.discountPrice, CalcNumber.as(model.getDiscountPrice()).toPrice());
        order.put(TableOrder.payPrice, CalcNumber.as(model.getOrderPrice()).toPrice());
        order.put(TableOrder.freightPrice, CalcNumber.as(model.getFreightPrice()).toPrice());
        order.put(TableOrder.createTime, new Date());
        order.put(TableOrder.status, OrderStatus.CREATE.getCode());
        order.put(TableOrder.refundStatus, OrderRefundStatus.INIT.getCode());
        order.put(TableOrder.message, model.getMessage());

        List<OrderGoodsModel> products = model.getProducts();
        List<ModelObject> goodsList = new ArrayList<>();
        List<ModelObject> skusList = new ArrayList<>();
        for (OrderGoodsModel product : products) {
            ModelObject goods = new ModelObject(TableOrderGoods.class);
            goods.put(TableOrderGoods.goodsId, product.getGid());
            goods.put(TableOrderGoods.goodsName, product.getGoods().getString(TableGoods.name));
            goods.put(TableOrderGoods.type, product.getGoods().getIntValue(TableGoods.type));
            if (product.getSkuId() != 0) {
                goods.put(TableOrderGoods.skuId, product.getSkuId());
            }

            goods.put(TableOrderGoods.amount, product.getBuyAmount());
            goods.put(TableOrderGoods.serialNumber, product.getGoods().getString(TableGoods.serialNumber));
            goods.put(TableOrderGoods.fileName, product.getGoods().getString(TableGoods.fileName));
            goodsList.add(goods);

            if (product.getSku() != null) {
                ModelObject sku = product.getSku();
                ModelObject property = GlobalService.goodsService.getPropertyBySku(product.getGid(), product.getSkuId());
                ModelObject orderSku = new ModelObject(TableOrderGoodsProperty.class);
                orderSku.put(TableOrderGoodsProperty.goodsId, product.getGid());
                orderSku.put(TableOrderGoodsProperty.skuId, product.getSkuId());
                orderSku.put(TableOrderGoodsProperty.attrName, property.getString(TableGoodsProperty.attrName));
                orderSku.put(TableOrderGoodsProperty.valueName, property.getString(TableGoodsProperty.valueName));
                skusList.add(orderSku);
                goods.put(TableOrderGoods.price, sku.getString(TableGoodsSku.price));
                goods.put(TableOrderGoods.vipPrice, sku.getString(TableGoodsSku.vipPrice));
                goods.put(TableOrderGoods.originalPrice, sku.getString(TableGoodsSku.originalPrice));
                goods.put(TableOrderGoods.costPrice, sku.getString(TableGoodsSku.costPrice));
                goods.put(TableOrderGoods.payPrice, CalcNumber.as(product.getPayPrice()).toPrice());
                goods.put(TableOrderGoods.discountPrice, CalcNumber.as(product.getDiscountPrice()).toPrice());
                goods.put(TableOrderGoods.skuId, product.getSkuId());
            } else {
                goods.put(TableOrderGoods.price, product.getGoods().getString(TableGoods.price));
                goods.put(TableOrderGoods.vipPrice, product.getGoods().getString(TableGoods.vipPrice));
                goods.put(TableOrderGoods.originalPrice, product.getGoods().getString(TableGoods.originalPrice));
                goods.put(TableOrderGoods.costPrice, product.getGoods().getString(TableGoods.costPrice));
                goods.put(TableOrderGoods.payPrice, CalcNumber.as(product.getPayPrice()).toPrice());
                goods.put(TableOrderGoods.discountPrice, CalcNumber.as(product.getDiscountPrice()).toPrice());
            }
        }

        ModelObject address = model.getAddress();
        ModelObject orderAddress = new ModelObject(TableOrderAddress.class);
        orderAddress.put(TableOrderAddress.realName, address.getString(TableUserAddress.realName));
        orderAddress.put(TableOrderAddress.phoneNumber, address.getString(TableUserAddress.phoneNumber));
        orderAddress.put(TableOrderAddress.telNumber, address.getString(TableUserAddress.telNumber));
        orderAddress.put(TableOrderAddress.country, address.getString(TableUserAddress.country));
        orderAddress.put(TableOrderAddress.province, address.getString(TableUserAddress.province));
        orderAddress.put(TableOrderAddress.city, address.getString(TableUserAddress.city));
        orderAddress.put(TableOrderAddress.county, address.getString(TableUserAddress.county));
        orderAddress.put(TableOrderAddress.address, address.getString(TableUserAddress.address));
        orderAddress.put(TableOrderAddress.email, address.getString(TableUserAddress.email));

        // 拼装订单发票
        ModelObject invoice = model.getInvoice();
        if (invoice != null) {
            invoice.setObjectClass(TableOrderInvoice.class);
        } else {
            invoice = new ModelObject(TableOrderInvoice.class);
            invoice.put(TableOrderInvoice.type, 1);
            invoice.put(TableOrderInvoice.headType, 0);
            invoice.put(TableOrderInvoice.cntType, 0);
        }
        invoice.put(TableOrderInvoice.orderId, 0);
        invoice.checkAndThrowable();

        sessionTemplate.save(order);
        for (ModelObject p : goodsList) {
            long gid = p.getLongValue(TableOrderGoods.goodsId);
            long skuId = p.getLongValue(TableOrderGoods.skuId);
            int buyAmount = p.getIntValue(TableOrderGoods.amount);
            GlobalService.goodsService.deductAmount(gid, skuId, buyAmount);
            p.put(TableOrderGoods.orderId, order.getLongValue(TableOrder.id));
            sessionTemplate.save(p);
        }
        for (ModelObject s : skusList) {
            s.put(TableOrderGoods.orderId, order.getLongValue(TableOrder.id));
            sessionTemplate.save(s);
        }
        orderAddress.put(TableOrderAddress.orderId, order.getLongValue(TableOrder.id));
        sessionTemplate.save(orderAddress);
        invoice.put(TableOrderInvoice.orderId, order.getLongValue(TableOrder.id));
        sessionTemplate.save(invoice);

        return order;
    }

    private String getOrderNumber() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        long num = RandomUtils.randomNumber(100000, 999999);
        return format.format(new Date()) + num;
    }
}
