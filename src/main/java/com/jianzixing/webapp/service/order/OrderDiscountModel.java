package com.jianzixing.webapp.service.order;

import java.math.BigDecimal;
import java.util.List;

public class OrderDiscountModel {

    private List<OrderGoodsModel> goodsModels; // 商品ID
    /**
     * 计算后的优惠价格
     */
    private BigDecimal payPrice;
    /**
     * 计算前的原始价格 单价*数量
     */
    private BigDecimal originalPrice;

    /**
     * 优惠的钱数
     */
    private BigDecimal discountPrice;

    public List<OrderGoodsModel> getGoodsModels() {
        return goodsModels;
    }

    public void setGoodsModels(List<OrderGoodsModel> goodsModels) {
        this.goodsModels = goodsModels;
    }

    public BigDecimal getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal bg = new BigDecimal(0);
        for (OrderGoodsModel m : goodsModels) {
            bg = bg.add(m.getTotalPrice());
        }
        return bg;
    }

    public int getBuyAmount() {
        int c = 0;
        for (OrderGoodsModel m : goodsModels) {
            c += m.getBuyAmount();
        }
        return c;
    }
}
