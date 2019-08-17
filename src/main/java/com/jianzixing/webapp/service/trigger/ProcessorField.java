package com.jianzixing.webapp.service.trigger;

import com.alibaba.druid.sql.visitor.functions.Char;

public class ProcessorField {
    private String name;
    private Class type;
    // 唯一标识码
    private String code;
    private Object value;
    private String detail;
    /**
     * 需要用户填写的字段
     */
    private boolean userSetValue = false;

    public ProcessorField() {
    }

    public ProcessorField(String name, Class type, String code, String detail) {
        this.name = name;
        this.type = type;
        this.code = code;
        this.detail = detail;
    }

    public ProcessorField(String code, Object value) {
        this.code = code;
        this.value = value;
    }

    public ProcessorField(Object value) {
        this.value = value;
        if (value == null) {
            throw new IllegalArgumentException("字段值必须存在");
        }
        type = value.getClass();
        this.setType(type);
    }

    public ProcessorField(Class type, String code, String detail) {
        this.type = type;
        this.code = code;
        this.detail = detail;
        this.setType(type);
    }

    public ProcessorField(Class type, String code, String detail, boolean userSetValue) {
        this.type = type;
        this.code = code;
        this.detail = detail;
        this.userSetValue = userSetValue;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
        if (type != String.class
                && type != Integer.class
                && type != int.class
                && type != Long.class
                && type != long.class
                && type != Short.class
                && type != short.class
                && type != Byte.class
                && type != byte.class
                && type != Boolean.class
                && type != boolean.class
                && type != Double.class
                && type != double.class
                && type != Float.class
                && type != float.class
                && type != Char.class
                && type != char.class) {
            throw new IllegalArgumentException("只接受基本数据类型");
        }
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isUserSetValue() {
        return userSetValue;
    }

    public void setUserSetValue(boolean userSetValue) {
        this.userSetValue = userSetValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessorField clone() {
        ProcessorField field = new ProcessorField();
        field.name = name;
        field.type = type;
        field.value = value;
        field.code = code;
        field.userSetValue = userSetValue;
        field.detail = detail;

        return field;
    }
}
