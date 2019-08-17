package com.jianzixing.webapp.service.payment;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.exception.TransactionException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    /**
     * 添加一个支付渠道
     *
     * @param object
     * @throws ModelCheckerException
     * @throws ModuleException
     */
    void addChannel(ModelObject object) throws ModelCheckerException, ModuleException;

    /**
     * 删除一个支付渠道(标记删除)
     *
     * @param id
     */
    void deleteChannel(long id);

    void updateChannel(ModelObject object) throws ModelCheckerException;

    Paging getChannels(String keyword, long start, long limit);

    List<ModelObject> getModels();

    List<ModelObject> getValidPayChannelByUid(long uid);

    PaymentModeInterface getValidPaymentMode(long channelId);

    /**
     * 通过支付渠道获取支付接口
     *
     * @param object
     * @return
     */
    PaymentModeInterface getValidPaymentMode(ModelObject object);

    PaymentModeInterface getCanCallbackPaymentMode(String name);

    ModelObject getChannelById(long paymentChannelId);

    /**
     * 支付页面选择支付方式后开始计算组合支付金额
     * paymentChannels 参数根据每一个实现不同传参不同
     * balance 传参 id,money
     * integral 传参 id,money
     * spcard 传参 id,money,cardNumbers
     * 以上如果不传money则代表全部拥有数量来扣减订单金额
     *
     * @param orderPayment
     */
    ModelObject payOrderFront(OrderPayment orderPayment) throws Exception;

    /**
     * 获取某个支付方式已经支付多少钱
     *
     * @param uid
     * @param channelId
     * @param orderNumber
     * @return
     */
    BigDecimal getChannelPayedPrice(long uid, long channelId, String orderNumber);

    /**
     * 同上
     *
     * @param uid
     * @param orderNumber
     * @return
     */
    BigDecimal getChannelPayedPrice(long uid, String orderNumber);

    /**
     * 前端余额充值
     *
     * @param recharge
     * @return
     * @throws Exception
     */
    ModelObject payRechargeFront(ModelObject recharge) throws Exception;

    List<ModelObject> getChannelByIds(ArrayList<Long> longs);

    /**
     * 外部支付方式支付成功后回调回来通知支付成功
     *
     * @param orderNumber
     * @param paymentNumber
     * @param outPaymentNumber
     */
    void setOrderPaymentSuccess(String orderNumber, String paymentNumber, String outPaymentNumber);

    void addPaymentTrans(ModelObject object);

    /**
     * 通过订单号和支付单号获取渠道参数(TablePaymentArgument)信息
     *
     * @param orderNumber
     * @param paymentNumber
     * @return
     */
    ModelObject getOrderPaymentChannel(String orderNumber, String paymentNumber);

    /**
     * 用户取消订单返还支付金额
     *
     * @param order
     * @throws ModuleException
     */
    void handBack(ModelObject order) throws ModuleException, TransactionException;

    /**
     * 用户退还部分支付金额
     *
     * @param afterSalesId         售后单id
     * @param refundOrderId        退款单，如果没有为0,表TableRefundOrder
     * @param paymentTransactionId 交易单必须存在,表TablePaymentTransaction
     * @param refundMoney          退款金额
     * @param type
     * @throws ModuleException
     */
    void handBackAfterSale(long afterSalesId,
                           long refundOrderId,
                           long paymentTransactionId,
                           BigDecimal refundMoney,
                           PaymentTransactionType type) throws ModuleException;

    /**
     * 获取一个订单的支付方式
     * 支付方式可能有多个
     *
     * @param uid
     * @param oid
     * @param number
     * @return
     */
    List<ModelObject> getTransactionByOrder(long uid, long oid, String number);

    /**
     * 获取某个退款记录
     *
     * @param afterSales
     * @param prid
     * @return
     */
    List<ModelObject> getPaymentRefund(PaymentRefundType afterSales, long prid);

    /**
     * 通过支付参数获取一个支付渠道参数(TablePaymentArgument)信息
     * 如果有相同参数的多个渠道只取第一个
     *
     * @param values
     * @return
     */
    ModelObject getPaymentChannelByArguments(Map<String, String> values);

    /**
     * 设置退款成功
     *
     * @param refundOrderNumber 当前系统退款单号
     * @param outTradeNo        外部第三方系统退款单号
     */
    void setHandBackSuccess(String refundOrderNumber, String outTradeNo);
}
