package com.jianzixing.webapp.service.refund;

public enum RefundStatus {
    CREATE(10, "新建退款单"),
    SUCCESS(20, "退款成功"),
    REFUNDING(30, "正在退款中"),
    FAILURE(50, "退款失败");

    private int code;
    private String msg;

    RefundStatus(int code, String msg) {
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
