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
public class MoneyOffDiscountImpl implements DiscountCalculateInterface {

    public String getView() {
        return "App.discount.impl.MoneyOffDiscountView";
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
                    int hasLimit = params.getIntValue("hasLimit");
                    List<String> firstMoney = params.getArray("firstMoney");
                    List<String> subMoney = params.getArray("subMoney");
                    int count = firstMoney.size();

                    /**
                     * 先检查是否上不封顶，如果上不封顶计算出整除值为0
                     * 再继续计算是否再区间内
                     */

                    if (hasLimit == 1) {
                        BigDecimal firstPrice = new BigDecimal(firstMoney.get(count - 1));
                        BigDecimal subPrice = new BigDecimal(subMoney.get(count - 1));
                        int sum = totalPrice.divide(firstPrice).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                        if (sum > 0) {
                            BigDecimal discountPrice = subPrice.multiply(new BigDecimal(sum));
                            wrapper.setGoodsDiscountPrice(discountPrice);
                            return true;
                        }
                    }

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
        if (firstPrice != null && (firstPrice.max(totalPrice) == totalPrice) || firstPrice.doubleValue() == totalPrice.doubleValue()) {
            BigDecimal payPrice = totalPrice.subtract(subPrice);
            wrapper.setGoodsDiscountPrice(totalPrice.subtract(payPrice));
            isSucc = true;
        }
        return isSucc;
    }

    @Override
    public String getName() {
        return "满减";
    }

    @Override
    public void checkSaveParams(ModelObject object) throws ModuleException {
        int hasLimit = object.getIntValue("hasLimit");
        if (hasLimit != 0 && hasLimit != 1) {
            object.put("hasLimit", 0);
        }
        List<String> firstMoney;
        List<String> subMoney;
        try {
            firstMoney = object.getArray("firstMoney");
            subMoney = object.getArray("subMoney");
        } catch (Exception e) {
            throw new ModuleException(StockCode.FAILURE, "参数firstMoney和参数subMoney需要是一个数组");
        }

        if (firstMoney == null || subMoney == null || firstMoney.size() == 0 || subMoney.size() == 0) {
            throw new ModuleException(StockCode.FAILURE, "满减金额必须填写");
        }
        if (firstMoney.size() != subMoney.size()) {
            throw new ModuleException(StockCode.FAILURE, "满减金额无法一一对应");
        }

        int count = firstMoney.size();
        for (int i = 0; i < count; i++) {
            BigDecimal firstPrice = new BigDecimal(firstMoney.get(i));
            BigDecimal subPrice = new BigDecimal(subMoney.get(i));
            if (firstPrice.max(subPrice) == subPrice) {
                throw new ModuleException(StockCode.TOO_LARGE, "不允许优惠金额比消费金额大");
            }

            if (firstPrice.doubleValue() <= 0) {
                throw new ModuleException(StockCode.TOO_SMALL, "不允许消费金额小于等于0");
            }
            if (subPrice.doubleValue() <= 0) {
                throw new ModuleException(StockCode.TOO_SMALL, "不允许优惠金额小于等于0");
            }
        }
    }
}
