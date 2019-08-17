package com.jianzixing.webapp.service.payment.channel;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.payment.*;
import com.jianzixing.webapp.tables.integral.TableIntegral;
import com.jianzixing.webapp.tables.payment.TablePaymentTransaction;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class IntegralPaymentChannel implements PaymentModeInterface {
    @Override
    public String getPaymentName() {
        return "积分支付";
    }

    @Override
    public String getPaymentShortName() {
        return "积分";
    }

    @Override
    public String getPaymentCode() {
        return "integral";
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
        ModelObject obj = this.getCalResult(order, cnfParams, pageParams);
        if (obj != null) {
            BigDecimal amount = obj.getBigDecimal("amount");
            BigDecimal residue = obj.getBigDecimal("residue");

            PaymentResult pr = (PaymentResult) obj.get("paymentResult");
            if (order.isCreatePayment()) {
                long id = GlobalService.integralService.orderIn(number, uid, amount, residue);
                pr.setOutPaymentNumber(String.valueOf(id));
                pr.setPaymentNumber(RandomUtils.uuid().toUpperCase());
            }
            return pr;
        }
        return null;
    }

    private ModelObject getCalResult(OrderPayment order, ModelObject cnfParams, ModelObject pageParams) throws ModuleException {
        long uid = order.getUid();
        // 获取积分和钱数兑换比例
        Map<String, String> rate = GlobalService.systemConfigService.getValues("integral_rate_money", "integral_min_money");
        BigDecimal rateMoney = new BigDecimal(rate.get("integral_rate_money"));
        BigDecimal minMoney = new BigDecimal(rate.get("integral_min_money"));
        ModelObject integralObj = GlobalService.integralService.getIntegralByUid(uid);
        BigDecimal haveAmount = integralObj.getBigDecimal(TableIntegral.amount);

        int isCustom = pageParams.getIntValue("custom");
        BigDecimal consume = haveAmount;
        if (isCustom == 1) {
            consume = new BigDecimal(pageParams.getString("money"));
        }
        if (haveAmount.doubleValue() < consume.doubleValue()) {
            throw new ModuleException("integral_min_consume", "支付的积分大于用户剩余积分");
        }


        if (minMoney.max(consume) == consume) {
            PaymentResult pr = new PaymentResult();
            ModelObject object = new ModelObject();

            BigDecimal payPrice = order.getOrderPrice();
            BigDecimal payPriceIntegralAmount = payPrice.multiply(rateMoney);
            // 如果积分能支付整个订单
            if (CalcNumber.as(consume).gte(payPriceIntegralAmount)) {
                object.put("amount", payPriceIntegralAmount);
                object.put("residue", consume.subtract(payPriceIntegralAmount));
                pr.setPayAmount(payPriceIntegralAmount.longValue());
                pr.setType(PaymentResult.TYPE.OK);
                pr.setPayPrice(payPrice);

            }
            // 如果积分无法支付整个订单
            else {
                BigDecimal integralMoney = consume.divide(rateMoney);
                integralMoney = new BigDecimal(CalcNumber.as(integralMoney).toPrice());
                BigDecimal payIntegral = integralMoney.multiply(rateMoney);
                BigDecimal residueMoney = consume.subtract(payIntegral);
                object.put("amount", payIntegral);
                object.put("residue", residueMoney);
                pr.setPayAmount(payIntegral.longValue());
                pr.setType(PaymentResult.TYPE.PART);
                pr.setPayPrice(integralMoney);
            }

            object.put("paymentResult", pr);
            return object;
        }
        return null;
    }

    @Override
    public PaymentResult trial(OrderPayment order, ModelObject cnfParams, ModelObject pageParams) throws ModuleException {
        ModelObject obj = this.getCalResult(order, cnfParams, pageParams);
        if (obj != null) {
            return (PaymentResult) obj.get("paymentResult");
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
        throw new IllegalArgumentException("积分支付不支持回调");
    }

    @Override
    public String refundCallback(HttpServletRequest request, HttpServletResponse response, String body) throws Exception {
        throw new IllegalArgumentException("积分支付不支持退款回调");
    }

    @Override
    public void handBack(PaymentRefundParams refundParams) throws ModuleException {
        ModelObject t = refundParams.getTrans();
        BigDecimal oldPayPrice = t.getBigDecimal(TablePaymentTransaction.payPrice);
        BigDecimal amount = t.getBigDecimal(TablePaymentTransaction.payAmount);
        long uid = t.getLongValue(TablePaymentTransaction.userId);
        String number = t.getString(TablePaymentTransaction.number);

        Map<String, String> rate = GlobalService.systemConfigService.getValues("integral_rate_money");
        BigDecimal rateMoney = new BigDecimal(rate.get("integral_rate_money"));

        if (oldPayPrice.doubleValue() >= refundParams.getPayPrice().doubleValue()) {
            BigDecimal backAmount = refundParams.getPayPrice().multiply(rateMoney);
            if (backAmount.doubleValue() <= amount.doubleValue()) {
                GlobalService.integralService.handBack(number, uid, backAmount);
                refundParams.setRefundAmount(backAmount);
            }
        } else {
            throw new ModuleException("payment_channel_handback_enough", "退款余额大于支付金额");
        }
    }
}
