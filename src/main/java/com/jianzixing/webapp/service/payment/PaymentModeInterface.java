package com.jianzixing.webapp.service.payment;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.exception.TransactionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

public interface PaymentModeInterface {
    String getPaymentName();

    String getPaymentShortName();

    String getPaymentCode();

    ParamField[] getNeedParams();

    boolean isWantCertificate();

    Class<? extends PaymentModeInterface> getPackageImpl();

    /**
     * 执行支付配置
     *
     * @param order
     * @param cnfParams  数据库配置的支付参数
     * @param pageParams 页面传入的支付参数
     * @return
     * @throws ModuleException
     */
    PaymentResult apply(OrderPayment order, ModelObject cnfParams, ModelObject pageParams) throws Exception;

    /**
     * 执行支付试算
     *
     * @param order
     * @param cnfParams  数据库配置的支付参数
     * @param pageParams 页面传入的支付参数
     * @return
     * @throws ModuleException
     */
    PaymentResult trial(OrderPayment order, ModelObject cnfParams, ModelObject pageParams) throws Exception;

    PaymentFlow getPaymentFlow();

    /**
     * 支付成功回调是否支持
     * 支付回调是第三方系统的支付通知
     *
     * @param name 名称
     * @return
     */
    boolean isCanCallback(String name);

    /**
     * 支付回调处理
     * 处理第三方系统的支付成功通知
     *
     * @param request
     * @return
     */
    String paymentCallback(HttpServletRequest request, HttpServletResponse response, String body) throws Exception;

    /**
     * 退款回调处理
     * 处理第三方系统的退款成功通知
     *
     * @param request
     * @return
     */
    String refundCallback(HttpServletRequest request, HttpServletResponse response, String body) throws Exception;

    /**
     * 退款处理
     */
    void handBack(PaymentRefundParams refundParams) throws ModuleException;
}
