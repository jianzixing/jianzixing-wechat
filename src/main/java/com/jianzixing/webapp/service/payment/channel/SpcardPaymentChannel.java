package com.jianzixing.webapp.service.payment.channel;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.payment.*;
import com.jianzixing.webapp.tables.payment.TablePaymentTransaction;
import com.jianzixing.webapp.tables.spcard.TableShoppingCardList;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.mimosaframework.orm.exception.TransactionException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

@Service
public class SpcardPaymentChannel implements PaymentModeInterface {
    @Override
    public String getPaymentName() {
        return "购物卡支付";
    }

    @Override
    public String getPaymentShortName() {
        return "购物卡";
    }

    @Override
    public String getPaymentCode() {
        return "spcard";
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
        List<String> cardNumbers = pageParams.getArray("cardNumbers");
        if (cardNumbers != null && cardNumbers.size() > 0) {
            String number = order.getOrderNumber();
            PaymentResult pr = this.getCalResult(order, cnfParams, pageParams);
            if (pr != null) {
                BigDecimal amount = pr.getBigDecimalPayAmount();
                try {
                    if (order.isCreatePayment()) {
                        List<ModelObject> cards = GlobalService.shoppingCardService.getShoppingCardByNumbers(order.getUid(), cardNumbers);
                        for (ModelObject card : cards) {
                            String cardNumber = card.getString(TableShoppingCardList.cardNumber);
                            BigDecimal balance = card.getBigDecimal(TableShoppingCardList.balance);

                            BigDecimal saveAmount = amount;
                            BigDecimal surplus = amount.subtract(balance);
                            if (surplus.doubleValue() > 0) {
                                saveAmount = balance;
                                amount = surplus;
                            }
                            GlobalService.shoppingCardService.orderIn(
                                    number, cardNumber, uid, saveAmount,
                                    "用户下单(" + number + ")使用购物卡消费" + CalcNumber.as(saveAmount).toPrice() + "元");
                            if (surplus.doubleValue() <= 0) {
                                break;
                            }
                        }
                        pr.setPaymentNumber(RandomUtils.uuid().toUpperCase());
                    }
                } catch (TransactionException e) {
                    e.printStackTrace();
                    throw new ModuleException(StockCode.FAILURE, e.getMessage());
                }
                return pr;
            }
        }
        return null;
    }

    private PaymentResult getCalResult(OrderPayment order, ModelObject cnfParams, ModelObject pageParams) throws ModuleException {
        if (order != null && pageParams != null) {
            List<String> cardNumbers = pageParams.getArray("cardNumbers");
            if (cardNumbers != null && cardNumbers.size() > 0) {
                List<ModelObject> cards = GlobalService.shoppingCardService.getShoppingCardByNumbers(order.getUid(), cardNumbers);
                if (cards != null && cards.size() > 0) {
                    BigDecimal balance = new BigDecimal(0);
                    for (ModelObject card : cards) {
                        balance = balance.add(card.getBigDecimal(TableShoppingCardList.balance));
                    }

                    int isCustom = pageParams.getIntValue("custom");
                    BigDecimal consume = balance;
                    if (isCustom == 1) {
                        consume = new BigDecimal(pageParams.getString("money"));
                    }
                    if (balance.doubleValue() < consume.doubleValue()) {
                        throw new ModuleException("spcard_min_consume", "购物卡余额小于消费金额");
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
            }
        }
        return null;
    }

    @Override
    public PaymentResult trial(OrderPayment order, ModelObject cnfParams, ModelObject pageParams) throws ModuleException {
        return this.getCalResult(order, cnfParams, pageParams);
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
        throw new IllegalArgumentException("购物卡支付不支持回调");
    }

    @Override
    public String refundCallback(HttpServletRequest request, HttpServletResponse response, String body) throws Exception {
        throw new IllegalArgumentException("购物卡支付不支持退款回调");
    }

    @Override
    public void handBack(PaymentRefundParams refundParams) throws ModuleException {
        ModelObject t = refundParams.getTrans();
        BigDecimal oldPayPrice = t.getBigDecimal(TablePaymentTransaction.payPrice);
        long uid = t.getLongValue(TablePaymentTransaction.userId);
        String number = t.getString(TablePaymentTransaction.number);

        if (oldPayPrice.doubleValue() >= refundParams.getPayPrice().doubleValue()) {
            GlobalService.shoppingCardService.handBack(number, uid, refundParams.getPayPrice());
            refundParams.setRefundAmount(refundParams.getPayPrice());
        } else {
            throw new ModuleException("payment_channel_handback_enough", "退款余额大于支付金额");
        }
    }
}
