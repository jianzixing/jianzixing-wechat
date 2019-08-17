package com.jianzixing.webapp.service.refund;

public enum RefundAuditStatus {
    CREATE(0, "未审核"),
    FAILURE(1, "审核拒绝"),
    SUCCESS(2, "审核通过");

    private int code;
    private String msg;

    RefundAuditStatus(int code, String msg) {
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
