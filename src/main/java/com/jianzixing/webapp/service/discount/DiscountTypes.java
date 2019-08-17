package com.jianzixing.webapp.service.discount;

public enum DiscountTypes {
    GOODS_CLASSIFY(0, "商品分类"),
    GOODS(1, "商品"),
    BRAND(2, "品牌");

    private int code;
    private String msg;

    DiscountTypes(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
