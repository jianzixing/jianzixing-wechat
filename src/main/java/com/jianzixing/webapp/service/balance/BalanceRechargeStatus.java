package com.jianzixing.webapp.service.balance;

public enum BalanceRechargeStatus {
    NOT_RECHARGED(0, "未充值"),
    RECHARGED(1, "充值成功");

    private int code;
    private String msg;

    BalanceRechargeStatus(int code, String msg) {
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
}
