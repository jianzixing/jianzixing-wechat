package com.jianzixing.webapp.service.order;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.discount.TableDiscount;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.calculator.CalcNumber;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class OrderDiscountModelWrapper {
    private long did;
    private ModelObject discount;
    private OrderDiscountModel goodsModel;

    public OrderDiscountModelWrapper(long did, OrderDiscountModel goodsModel) {
        this.did = did;
        this.goodsModel = goodsModel;
    }

    public OrderDiscountModelWrapper(ModelObject discount, OrderDiscountModel goodsModel) {
        this.discount = discount;
        this.goodsModel = goodsModel;
        this.did = discount.getLongValue(TableDiscount.id);
    }

    public long getDid() {
        return did;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal p = null;
        if (goodsModel != null) {
            BigDecimal price = goodsModel.getTotalPrice();
            p = price;
        }
        return p;
    }

    public long getTotalAmount() {
        long c = 0;
        if (goodsModel != null) {
            c = goodsModel.getBuyAmount();
        }
        return c;
    }

    public void setGoodsDiscountPrice(BigDecimal calDiscountPrice) {
        if (goodsModel != null) {
            goodsModel.setDiscountPrice(CalcNumber.as(calDiscountPrice).toBigDecimalPrice());
            List<OrderGoodsModel> goodsModels = goodsModel.getGoodsModels();
            BigDecimal totalPrice = goodsModel.getTotalPrice();
            /**
             * 按商品金额比例计算平均商品金额
             *
             */
            Map<OrderGoodsModel, BigDecimal> rate = new LinkedHashMap<>();
            for (OrderGoodsModel model : goodsModels) {
                rate.put(model, model.getTotalPrice().divide(totalPrice, 8, RoundingMode.FLOOR));
            }
            Map<OrderGoodsModel, BigDecimal> ratePrice = new LinkedHashMap<>();
            for (OrderGoodsModel model : goodsModels) {
                BigDecimal rateItem = rate.get(model);
                ratePrice.put(model, rateItem.multiply(calDiscountPrice));
            }
            Iterator<Map.Entry<OrderGoodsModel, BigDecimal>> iterator = ratePrice.entrySet().iterator();

            BigDecimal balance = new BigDecimal(calDiscountPrice.toPlainString());
            while (iterator.hasNext()) {
                Map.Entry<OrderGoodsModel, BigDecimal> models = iterator.next();
                OrderGoodsModel model = models.getKey();
                BigDecimal discountPrice = models.getValue();

                if (iterator.hasNext()) {
                    BigDecimal realPrice = CalcNumber.as(discountPrice).toBigDecimalPrice();
                    balance = balance.subtract(realPrice);
                    model.setDiscountPrice(CalcNumber.as(realPrice).toBigDecimalPrice());
                    model.setPayPrice(model.getTotalPrice().subtract(realPrice));
                } else {
                    model.setDiscountPrice(CalcNumber.as(balance).toBigDecimalPrice());
                    model.setPayPrice(CalcNumber.as(model.getTotalPrice().subtract(balance)).toBigDecimalPrice());
                }
            }
        }
    }

    public ModelObject getDiscount() {
        if (discount == null) {
            discount = GlobalService.discountService.getDiscountById(did);
        }
        return discount;
    }

    public void setDiscount(ModelObject discount) {
        this.discount = discount;
    }
}
