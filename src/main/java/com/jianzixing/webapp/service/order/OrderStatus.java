package com.jianzixing.webapp.service.order;

public enum OrderStatus {
    CREATE(0, "新建"),
    PAY(10, "订单已支付"),
    SURE(20, "订单已确认"),  // 预留状态
    LOGISTICS(30, "订单商品已发货"),
    DELIVERY(40, "订单商品已出库"), // 预留状态
    DELIVERY_REFUSED(41, "订单商品退回或拒收"), // 预留状态
    RECEIVE(50, "用户已确认收货,订单完成"), //订单完成可以退货
    FINISH(60, "订单完成"), //订单完成不可以退货
    CANCEL(90, "订单取消");

    private int code;
    private String msg;

    OrderStatus(int code, String msg) {
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

    public static OrderStatus value(int code) {
        for (OrderStatus os : OrderStatus.values()) {
            if (os.getCode() == code) {
                return os;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.code + "";
    }
}
