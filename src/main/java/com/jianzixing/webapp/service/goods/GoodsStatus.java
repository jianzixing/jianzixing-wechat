package com.jianzixing.webapp.service.goods;

public enum GoodsStatus {
    CREATE(0, "创建未审核"),
    AUDIT(10, "审核通过"),
    DOWN(20, "商品已下架"),
    UP(30, "商品已上架"),
    INVALID(40, "商品无效"),
    DUE(50, "商品已过期");

    private int code;
    private String msg;

    GoodsStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return this.code + "";
    }
}
