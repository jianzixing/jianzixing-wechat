package com.jianzixing.webapp.service.area;

public enum AreaClassify {
    HUADONG(1, "华东"),
    HUABEI(2, "华北"),
    HUAZHONG(3, "华中"),
    HUANAN(4, "华南"),
    DONGBEI(5, "东北"),
    XIBEI(6, "西北"),
    XINAN(7, "西南"),
    GANGAOTAI(8, "港澳台"),
    HAIWAI(9, "海外");
    private int code;
    private String msg;

    AreaClassify(int code, String msg) {
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
