package com.jianzixing.webapp.service.refund;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.payment.PaymentTransactionType;
import com.jianzixing.webapp.tables.aftersales.TableAfterSales;
import com.jianzixing.webapp.tables.order.TableOrder;
import com.jianzixing.webapp.tables.refund.TableRefundOrder;
import com.jianzixing.webapp.tables.user.TableUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.RandomUtils;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class DefaultRefundOrderService implements RefundOrderService {
    private static final Log logger = LogFactory.getLog(DefaultRefundOrderService.class);

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addRefundOrder(ModelObject object, boolean isCustomStatus) {
        object.setObjectClass(TableRefundOrder.class);
        if (!isCustomStatus) {
            object.put(TableRefundOrder.status, RefundStatus.CREATE.getCode());
            object.put(TableRefundOrder.auditStatus, RefundAuditStatus.CREATE.getCode());
        }
        object.put(TableRefundOrder.number, this.getOrderNumber());
        object.put(TableRefundOrder.createTime, new Date());
        sessionTemplate.save(object);
    }

    private String getOrderNumber() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        long num = RandomUtils.randomNumber(100000, 999999);
        return format.format(new Date()) + num;
    }

    @Override
    public Paging getRefundOrders(ModelObject search, long start, long limit) {
        Query query = Criteria.query(TableRefundOrder.class);
        query.subjoin(TableUser.class).eq(TableUser.id, TableRefundOrder.userId).single();
        query.limit(start, limit);
        query.order(TableRefundOrder.id, false);
        if (search != null) {
            if (search.isNotEmpty("userName")) {
                ModelObject user = GlobalService.userService.getUserByUserName(search.getString("userName"));
                if (user != null) {
                    query.eq(TableRefundOrder.userId, user.getLongValue(TableUser.id));
                } else {
                    return null;
                }
            }
            if (search.isNotEmpty("orderNumber")) {
                ModelObject order = GlobalService.orderService.getSimpleOrderByNumber(search.getString("orderNumber"));
                if (order != null) {
                    query.eq(TableRefundOrder.orderId, order.getLongValue(TableOrder.id));
                } else {
                    return null;
                }
            }
            if (search.isNotEmpty("from")) {
                query.eq(TableRefundOrder.from, search.getString("from"));
            }
            if (search.isNotEmpty("auditStatus")) {
                query.eq(TableRefundOrder.auditStatus, search.getIntValue("auditStatus"));
            }
            if (search.isNotEmpty("status")) {
                query.eq(TableRefundOrder.status, search.getIntValue("status"));
            }
            if (search.isNotEmpty("type")) {
                query.eq(TableRefundOrder.type, search.getIntValue("type"));
            }
            if (search.isNotEmpty("createTimeStart")) {
                query.gte(TableRefundOrder.createTime, search.getString("createTimeStart"));
            }
            if (search.isNotEmpty("createTimeEnd")) {
                query.lte(TableRefundOrder.createTime, search.getString("createTimeEnd"));
            }
        }
        return sessionTemplate.paging(query);
    }

    @Override
    public ModelObject getRefundOrder(long refundId) {
        return sessionTemplate.get(TableRefundOrder.class, refundId);
    }

    @Override
    public void setRefundAuditPass(long rid, String remark) throws ModuleException {
        ModelObject refundOrder = sessionTemplate.get(TableRefundOrder.class, rid);
        if (refundOrder != null) {
            int status = refundOrder.getIntValue(TableRefundOrder.status);
            int auditStatus = refundOrder.getIntValue(TableRefundOrder.auditStatus);
            if (status == RefundStatus.CREATE.getCode() && auditStatus == 0) {
                ModelObject update = new ModelObject(TableRefundOrder.class);
                update.put(TableRefundOrder.id, rid);
                update.put(TableRefundOrder.auditStatus, 2);
                update.put(TableRefundOrder.remark, remark);
                sessionTemplate.update(update);
            } else {
                throw new ModuleException(StockCode.STATUS_ERROR, "当前状态不允许修改");
            }
        }
    }

    @Override
    public void setRefundAuditReject(long rid, String remark) throws ModuleException {
        ModelObject refundOrder = sessionTemplate.get(TableRefundOrder.class, rid);
        if (refundOrder != null) {
            int status = refundOrder.getIntValue(TableRefundOrder.status);
            int auditStatus = refundOrder.getIntValue(TableRefundOrder.auditStatus);
            if (status == RefundStatus.CREATE.getCode() && auditStatus == 0) {
                ModelObject update = new ModelObject(TableRefundOrder.class);
                update.put(TableRefundOrder.id, rid);
                update.put(TableRefundOrder.auditStatus, 1);
                update.put(TableRefundOrder.remark, remark);
                sessionTemplate.update(update);
            } else {
                throw new ModuleException(StockCode.STATUS_ERROR, "当前状态不允许修改");
            }
        }
    }

    @Override
    public void startRefund(long rid) throws ModuleException, TransactionException {
        ModelObject refundOrder = sessionTemplate.get(TableRefundOrder.class, rid);
        if (refundOrder != null) {
            int status = refundOrder.getIntValue(TableRefundOrder.status);
            int auditStatus = refundOrder.getIntValue(TableRefundOrder.auditStatus);
            // 新建退款单可以退款 失败的退款单可以重试
            if ((status == RefundStatus.CREATE.getCode() || status == RefundStatus.FAILURE.getCode()) && auditStatus == 2) {
                sessionTemplate.execute(new TransactionCallback<Boolean>() {
                    @Override
                    public Boolean invoke(Transaction transaction) throws Exception {
                        ModelObject update = new ModelObject(TableRefundOrder.class);
                        // 更新状态后开始退款
                        if (refundOrder.getString(TableRefundOrder.from).equals(RefundFrom.RETURN_GOODS.name())) {
                            ModelObject afterSales = GlobalService.afterSalesService.getAfterSalesByRefund(
                                    refundOrder.getLongValue(TableRefundOrder.fromId),
                                    refundOrder.getLongValue(TableRefundOrder.id));

                            if (afterSales != null) {
                                long asid = afterSales.getLongValue(TableAfterSales.id);
                                long orderGoodsId = afterSales.getLongValue(TableAfterSales.orderGoodsId);
                                BigDecimal money = refundOrder.getBigDecimal(TableRefundOrder.money);
                                GlobalService.paymentService.handBackAfterSale(orderGoodsId, asid, rid, money, PaymentTransactionType.ORDER);
                                GlobalService.afterSalesService.setAfterSalesFinish(asid);

                                update.put(TableRefundOrder.userId, afterSales.getLongValue(TableAfterSales.userId));
                                update.put(TableRefundOrder.orderId, afterSales.getLongValue(TableAfterSales.orderId));
                            } else {
                                throw new ModuleException("not_found_after_sales", "退货退款没有找到售后单");
                            }
                        }

                        update.put(TableRefundOrder.id, rid);
                        update.put(TableRefundOrder.status, RefundStatus.SUCCESS.getCode());
                        update.put(TableRefundOrder.refundTime, new Date());
                        sessionTemplate.update(update);
                        return null;
                    }
                });
            } else {
                throw new ModuleException(StockCode.STATUS_ERROR, "当前状态不允许退款");
            }
        }
    }

    @Override
    public void setRefundSuccess(String refundOrderNumber, String outTradeNo) {
        ModelObject refund = sessionTemplate.get(Criteria.query(TableRefundOrder.class).eq(TableRefundOrder.number, refundOrderNumber));
        if (refund != null) {
            ModelObject update = new ModelObject(TableRefundOrder.class);
            update.put(TableRefundOrder.id, refund.getLongValue(TableRefundOrder.id));
            update.put(TableRefundOrder.status, RefundStatus.SUCCESS.getCode());
            update.put(TableRefundOrder.outRefundNumber, outTradeNo);
            sessionTemplate.update(update);

            String from = refund.getString(TableRefundOrder.from);
            if (from.equalsIgnoreCase(RefundFrom.RETURN_GOODS.name())) {
                long afterSalesId = refund.getLongValue(TableRefundOrder.fromId);
                if (afterSalesId > 0) {
                    try {
                        GlobalService.afterSalesService.setAfterSalesFinish(afterSalesId);
                    } catch (ModuleException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            logger.error("设置退款单状态失败,没有找到退款单!");
        }
    }

    @Override
    public List<ModelObject> getRefundOrderByInfo(long uid, long orderId, RefundFrom from, RefundType type, long transId) {
        return sessionTemplate.list(Criteria.query(TableRefundOrder.class)
                .eq(TableRefundOrder.userId, uid)
                .eq(TableRefundOrder.orderId, orderId)
                .eq(TableRefundOrder.from, from.name())
                .eq(TableRefundOrder.type, type.getCode())
                .eq(TableRefundOrder.transId, transId));
    }

    @Override
    public void setRefunding(long refundOrderId) {
        /**
         * 只允许状态是新建状态的退款单设置为退款中的状态
         * 防止接口先于更新前到达
         */
        sessionTemplate.update(Criteria.update(TableRefundOrder.class)
                .eq(TableRefundOrder.id, refundOrderId)
                .eq(TableRefundOrder.status, RefundStatus.CREATE.getCode())
                .value(TableRefundOrder.status, RefundStatus.REFUNDING.getCode()));
    }
}
