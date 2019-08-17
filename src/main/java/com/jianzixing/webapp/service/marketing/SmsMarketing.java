package com.jianzixing.webapp.service.marketing;

import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;

import java.util.List;

public interface SmsMarketing {

    String getName();

    String getDetail();

    /**
     * 结构 [{code:'appid',name:'APPID',detail:''}]
     *
     * @return
     */
    ModelArray getParams();

    SmsType getSmsType();

    String send(ModelObject params, SmsParams smsParams);
}
