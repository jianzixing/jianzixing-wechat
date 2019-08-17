package com.jianzixing.webapp.service.marketing;

import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;

import java.util.ArrayList;
import java.util.List;

public class EmailParams {
    private long sid;
    private ModelObject email;
    private String address;
    private List<String> addresses;
    private String subject;
    private String content;

    public static EmailParams create() {
        return new EmailParams();
    }

    public EmailParams() {
    }

    public long getSid() {
        return sid;
    }

    public EmailParams setSid(long sid) {
        this.sid = sid;
        return this;
    }

    public ModelObject getEmail() {
        return email;
    }

    public EmailParams setEmail(ModelObject email) {
        this.email = email;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public EmailParams setAddress(String address) {
        this.address = address;
        return this;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public EmailParams setAddresses(List<String> addresses) {
        this.addresses = addresses;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public EmailParams setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getContent() {
        return content;
    }

    public EmailParams setContent(String content) {
        this.content = content;
        return this;
    }

    public List<String> getAllAddresses() {
        List<String> addresses = new ArrayList<>();
        if (StringUtils.isNotBlank(this.getAddress())) {
            addresses.add(this.getAddress());
        }
        if (this.getAddresses() != null) {
            addresses.addAll(this.getAddresses());
        }
        return addresses;
    }
}
