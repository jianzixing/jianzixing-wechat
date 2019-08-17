package com.jianzixing.webapp.service.marketing;

import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;

import java.util.ArrayList;
import java.util.List;

public class SmsParams {
    private long sid;
    private ModelObject sms;
    private String phone;
    private List<String> phones;
    /**
     * 非短信模板的短信内容
     */
    private String content;

    /**
     * 如果是短信模板，这个是短信模板code
     */
    private String templateCode;

    /**
     * 如果是短信模板，则这个是短信模板的参数
     */
    private ModelObject templateParam;
    /**
     * 验证码 营销短信 或者其他
     * {@link SmsAction}
     */
    private int action;

    public static SmsParams create(long sid, String phone, List<String> phones, String templateCode) {
        return new SmsParams(sid, phone, phones, templateCode);
    }

    public static SmsParams create(String phone, ModelObject templateParam) {
        return new SmsParams(phone, templateParam);
    }

    public static SmsParams create() {
        return new SmsParams();
    }

    public SmsParams(long sid, String phone, List<String> phones, String templateCode) {
        this.sid = sid;
        this.phone = phone;
        this.phones = phones;
        this.templateCode = templateCode;
    }

    public SmsParams(String phone, ModelObject templateParam) {
        this.phone = phone;
        this.templateParam = templateParam;
    }

    public SmsParams() {
    }

    public long getSid() {
        return sid;
    }

    public SmsParams setSid(long sid) {
        this.sid = sid;
        return this;
    }

    public ModelObject getSms() {
        return sms;
    }

    public SmsParams setSms(ModelObject sms) {
        this.sms = sms;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public SmsParams setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public List<String> getPhones() {
        return phones;
    }

    public SmsParams setPhones(List<String> phones) {
        this.phones = phones;
        return this;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public SmsParams setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SmsParams setContent(String content) {
        this.content = content;
        return this;
    }

    public ModelObject getTemplateParam() {
        return templateParam;
    }

    public SmsParams setTemplateParam(ModelObject templateParam) {
        this.templateParam = templateParam;
        return this;
    }

    public int getAction() {
        return action;
    }

    public SmsParams setAction(int action) {
        this.action = action;
        return this;
    }

    public List<String> getAllPhones() {
        List<String> phones = new ArrayList<>();
        if (StringUtils.isNotBlank(this.getPhone())) {
            phones.add(this.getPhone());
        }
        if (this.getPhones() != null) {
            phones.addAll(this.getPhones());
        }
        return phones;
    }
}
