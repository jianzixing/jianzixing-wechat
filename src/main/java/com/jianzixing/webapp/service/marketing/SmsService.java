package com.jianzixing.webapp.service.marketing;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

import java.util.List;

public interface SmsService {
    void addSms(ModelObject object) throws ModelCheckerException;

    void delSms(int id);

    void updateSms(ModelObject object) throws ModelCheckerException;

    List<ModelObject> getSms();

    List<ModelObject> getSmsImpls();

    void sendSms(SmsParams smsParams) throws ModuleException;

    ModelObject getSmsById(long id);

    List<ModelObject> getEnableSms(String keyword);

    void enableSms(int id);

    void disableSms(int id);

    ModelObject getLastSms(String phone, SmsAction action);

    void addSmsAuthCode(String type, String phone, String code);

    ModelObject getSmsAuthCode(String type, String phone);
}
