package com.jianzixing.webapp.service.payment.channel;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.payment.*;
import com.jianzixing.webapp.tables.balance.TableBalance;
import com.jianzixing.webapp.tables.payment.TablePaymentTransaction;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@Service
public class BalancePaymentChannel implements PaymentModeInterface {
    @Override
    public String getPaymentName() {
        return "余额支付";
    }

    @Override
    public String getPaymentShortName() {
        return "余额";
    }

    @Override
    public String getPaymentCode() {
        return "balance";
    }

    @Override
    public ParamField[] getNeedParams() {
        return new ParamField[0];
    }

    @Override
    public boolean isWantCertificate() {
        return false;
    }

    @Override
    public Class<? extends PaymentModeInterface> getPackageImpl() {
        return this.getClass();
    }

    @Override
    public PaymentResult apply(OrderPayment order, ModelObject cnfParams, ModelObject pageParams) throws ModuleException {
        long uid = order.getUid();
        String number = order.getOrderNumber();
        PaymentResult pr = this.getCalResult(order, cnfParams, pageParams);
        if (pr != null) {
            BigDecimal amount = pr.getBigDecimalPayAmount();
            if (order.isCreatePayment()) {
                long id = GlobalService.balanceService.orderIn(number, uid, amount);
                pr.setOutPaymentNumber(String.valueOf(id));
                pr.setPaymentNumber(RandomUtils.uuid().toUpperCase());
            }
            return pr;
        }
        return null;
    }

    private PaymentResult getCalResult(OrderPayment order, ModelObject cnfParams, ModelObject pageParams) throws ModuleException {
        long uid = order.getUid();
        ModelObject integralObj = GlobalService.balanceService.getBalanceByUid(uid);
        BigDecimal haveBalance = integralObj.getBigDecimal(TableBalance.balance);
        int isCustom = pageParams.getIntValue("custom");
        BigDecimal consume = haveBalance;
        if (isCustom == 1) {
            consume = new BigDecimal(pageParams.getString("money"));
        }
        if (haveBalance.doubleValue() < consume.doubleValue()) {
            throw new ModuleException("balance_min_consume", "支付的余额大于用户剩余余额");
        }
        PaymentResult pr = new PaymentResult();

        BigDecimal payPrice = order.getOrderPrice();
        // 如果余额能支付整个订单
        if (CalcNumber.as(consume).gte(payPrice)) {
            pr.setPayAmount(payPrice);
            pr.setType(PaymentResult.TYPE.OK);
            pr.setPayPrice(payPrice);
        }
        // 如果余额无法支付整个订单
        else {
            pr.setPayAmount(consume);
            pr.setType(PaymentResult.TYPE.PART);
            pr.setPayPrice(consume);
        }

        return pr;
    }

    @Override
    public PaymentResult trial(OrderPayment order, ModelObject cnfParams, ModelObject pageParams) throws ModuleException {
        PaymentResult obj = this.getCalResult(order, cnfParams, pageParams);
        if (obj != null) {
            return obj;
        }
        return null;
    }

    @Override
    public PaymentFlow getPaymentFlow() {
        return PaymentFlow.TIMELY;
    }

    @Override
    public boolean isCanCallback(String name) {
        return false;
    }

    @Override
    public String paymentCallback(HttpServletRequest request, HttpServletResponse response, String body) {
        throw new IllegalArgumentException("余额支付不支持回调");
    }

    @Override
    public String refundCallback(HttpServletRequest request, HttpServletResponse response, String body) throws Exception {
        throw new IllegalArgumentException("余额支付不支持退款回调");
    }

    @Override
    public void handBack(PaymentRefundParams refundParams) throws ModuleException {
        ModelObject t = refundParams.getTrans();
        BigDecimal oldPayPrice = t.getBigDecimal(TablePaymentTransaction.payPrice);
        long uid = t.getLongValue(TablePaymentTransaction.userId);
        String number = t.getString(TablePaymentTransaction.number);

        if (oldPayPrice.doubleValue() >= refundParams.getPayPrice().doubleValue()) {
            GlobalService.balanceService.handBack(number, uid, refundParams.getPayPrice());
            refundParams.setRefundAmount(refundParams.getPayPrice());
        } else {
            throw new ModuleException("payment_channel_handback_enough", "退款余额大于支付金额");
        }
    }
}
