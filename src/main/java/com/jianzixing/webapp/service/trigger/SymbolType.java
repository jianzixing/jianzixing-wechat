package com.jianzixing.webapp.service.trigger;

public enum SymbolType {
    EQ("eq", "等于"),
    GT("gt", "大于"),
    GTE("gte", "大于等于"),
    LT("lt", "小于"),
    LTE("lte", "小于等于");
    private String name;
    private String msg;

    SymbolType(String name, String msg) {
        this.name = name;
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }
}
