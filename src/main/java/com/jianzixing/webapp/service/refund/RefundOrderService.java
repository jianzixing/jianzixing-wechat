package com.jianzixing.webapp.service.refund;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.exception.TransactionException;

import java.util.List;

public interface RefundOrderService {

    /**
     * 创建退款单
     *
     * @param object
     * @param isCustomStatus 如果已经定义好状态则不覆盖状态标识
     */
    void addRefundOrder(ModelObject object, boolean isCustomStatus);

    Paging getRefundOrders(ModelObject search, long start, long limit);

    ModelObject getRefundOrder(long rid);

    void setRefundAuditPass(long rid, String remark) throws ModuleException;

    void setRefundAuditReject(long rid, String remark) throws ModuleException;

    void startRefund(long rid) throws ModuleException, TransactionException;

    /**
     * 设置退款单退款成功
     *
     * @param refundOrderNumber
     * @param outTradeNo
     */
    void setRefundSuccess(String refundOrderNumber, String outTradeNo);

    /**
     * 获取退款单
     *
     * @param uid
     * @param orderId
     * @param from
     * @param type
     * @param transId
     * @return
     */
    List<ModelObject> getRefundOrderByInfo(long uid, long orderId, RefundFrom from, RefundType type, long transId);

    void setRefunding(long refundOrderId);
}
