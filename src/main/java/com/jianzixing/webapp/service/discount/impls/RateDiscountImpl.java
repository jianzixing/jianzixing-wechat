package com.jianzixing.webapp.service.discount.impls;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.discount.DiscountCalculateInterface;
import com.jianzixing.webapp.service.order.OrderDiscountModelWrapper;
import com.jianzixing.webapp.tables.discount.TableDiscount;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RateDiscountImpl implements DiscountCalculateInterface {

    public String getView() {
        return "App.discount.impl.RateDiscountView";
    }

    @Override
    public boolean calculate(OrderDiscountModelWrapper wrapper) {
        if (wrapper != null) {
            ModelObject discount = wrapper.getDiscount();
            BigDecimal totalPrice = wrapper.getTotalPrice();
            String paramsStr = discount.getString(TableDiscount.params);
            if (StringUtils.isNotBlank(paramsStr)) {
                ModelObject params = ModelObject.parseObject(paramsStr);
                if (params != null) {
                    List<String> firstMoney = params.getArray("firstMoney");
                    List<String> subMoney = params.getArray("subMoney");
                    int count = firstMoney.size();

                    for (int i = 0; i < count; i++) {
                        BigDecimal firstPrice = new BigDecimal(firstMoney.get(i));
                        BigDecimal subPrice = new BigDecimal(subMoney.get(i));
                        boolean isSucc = this.applyDiscount(wrapper, totalPrice, firstPrice, subPrice);
                        if (isSucc) return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean applyDiscount(OrderDiscountModelWrapper wrapper,
                                  BigDecimal totalPrice,
                                  BigDecimal firstPrice,
                                  BigDecimal subPrice) {
        boolean isSucc = false;
        if (firstPrice != null && (firstPrice.max(totalPrice) == totalPrice || firstPrice.doubleValue() == totalPrice.doubleValue())) {
            BigDecimal payPrice = totalPrice.multiply(subPrice.divide(new BigDecimal("100")));
            wrapper.setGoodsDiscountPrice(payPrice);
            isSucc = true;
        }
        return isSucc;
    }

    @Override
    public String getName() {
        return "满折";
    }

    @Override
    public void checkSaveParams(ModelObject object) throws ModuleException {
        List<String> firstMoney;
        List<String> subMoney;
        try {
            firstMoney = object.getArray("firstMoney");
            subMoney = object.getArray("subMoney");
        } catch (Exception e) {
            throw new ModuleException(StockCode.FAILURE, "参数firstMoney和参数subMoney需要是一个数组");
        }

        if (firstMoney == null || subMoney == null || firstMoney.size() == 0 || subMoney.size() == 0) {
            throw new ModuleException(StockCode.FAILURE, "满折金额必须填写");
        }
        if (firstMoney.size() != subMoney.size()) {
            throw new ModuleException(StockCode.FAILURE, "满折金额无法一一对应");
        }

        int count = firstMoney.size();
        for (int i = 0; i < count; i++) {
            BigDecimal firstPrice = new BigDecimal(firstMoney.get(i));
            BigDecimal subPrice = new BigDecimal(subMoney.get(i));
            if (subPrice.doubleValue() >= 100) {
                throw new ModuleException(StockCode.TOO_LARGE, "不允许折扣数大于等于100%");
            }
            if (firstPrice.doubleValue() <= 0) {
                throw new ModuleException(StockCode.TOO_SMALL, "不允许消费金额小于等于0");
            }
        }
    }
}
