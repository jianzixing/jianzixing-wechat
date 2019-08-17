package com.jianzixing.webapp.service.goods;

public enum GoodsType {
    ENTITY(10, "实体商品"),
    VIRTUAL(11, "虚拟商品");

    private int code;
    private String msg;

    GoodsType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    @Override
    public String toString() {
        return this.code + "";
    }
}
