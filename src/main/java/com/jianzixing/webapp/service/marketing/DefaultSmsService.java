package com.jianzixing.webapp.service.marketing;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.marketing.TableMarketSms;
import com.jianzixing.webapp.tables.marketing.TableMarketSmsAuthCode;
import com.jianzixing.webapp.tables.marketing.TableMarketSmsRecord;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class DefaultSmsService implements SmsService {
    @Autowired
    private SessionTemplate sessionTemplate;

    @Autowired
    ApplicationContext context;

    @Override
    public void addSms(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableMarketSms.class);
        String impl = object.getString(TableMarketSms.impl);
        try {
            Class c = Class.forName(impl);
            SmsMarketing smsMarketing = (SmsMarketing) context.getBean(c);
            object.put(TableMarketSms.implName, smsMarketing.getName());
            object.put(TableMarketSms.type, smsMarketing.getSmsType().getCode());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String params = object.getString(TableMarketSms.params);
        object.put(TableMarketSms.params, params);
        object.checkAndThrowable();
        sessionTemplate.save(object);
    }

    @Override
    public void delSms(int id) {
        sessionTemplate.delete(TableMarketSms.class, id);
    }

    @Override
    public void updateSms(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableMarketSms.class);
        String impl = object.getString(TableMarketSms.impl);
        try {
            Class c = Class.forName(impl);
            SmsMarketing smsMarketing = (SmsMarketing) context.getBean(c);
            object.put(TableMarketSms.implName, smsMarketing.getName());
            object.put(TableMarketSms.type, smsMarketing.getSmsType().getCode());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String params = object.getString(TableMarketSms.params);
        object.put(TableMarketSms.params, params);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public List<ModelObject> getSms() {
        Query query = Criteria.query(TableMarketSms.class);
        query.order(TableMarketSms.id, false);
        return sessionTemplate.list(query);
    }

    @Override
    public List<ModelObject> getSmsImpls() {
        Map<String, SmsMarketing> processorMap = context.getBeansOfType(SmsMarketing.class);
        List<ModelObject> objects = new ArrayList<>();
        if (processorMap != null) {
            Iterator<Map.Entry<String, SmsMarketing>> iterator = processorMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, SmsMarketing> entry = iterator.next();
                SmsMarketing smsMarketing = entry.getValue();
                ModelArray field = smsMarketing.getParams();
                ModelObject object = new ModelObject();
                object.put("name", smsMarketing.getName());
                object.put("value", smsMarketing.getClass().getName());
                object.put("type", smsMarketing.getSmsType().getCode());
                object.put("params", field);
                object.put("detail", smsMarketing.getDetail());
                objects.add(object);
            }
        }
        return objects;
    }

    @Override
    public void sendSms(SmsParams smsParams) {
        try {
            ModelObject sms = smsParams.getSms();
            if (sms == null) {
                sms = this.getSmsById(smsParams.getSid());
            }
            if (sms == null) {
                throw new ModuleException(StockCode.ARG_NULL, "不存在或未启用的短信服务");
            }
            smsParams.setSms(sms);
            String impl = sms.getString(TableMarketSms.impl);
            String paramStr = sms.getString(TableMarketSms.params);
            ModelArray paramArray = null;
            ModelObject infParams = new ModelObject();
            if (StringUtils.isNotBlank(paramStr)) {
                paramArray = ModelArray.parseArray(paramStr);
                for (int i = 0; i < paramArray.size(); i++) {
                    ModelObject item = paramArray.getModelObject(i);
                    infParams.put(item.getString("code"), item.getString("value"));
                }
            }
            Class c = Class.forName(impl);
            SmsMarketing processorInterface = (SmsMarketing) context.getBean(c);

            String msg = "";
            if (sms.getIntValue(TableMarketSms.type) == 1) {
                msg = processorInterface.send(infParams, smsParams);
            } else {
                msg = processorInterface.send(infParams, smsParams);
            }
            List<String> phones = smsParams.getAllPhones();
            List<ModelObject> records = new ArrayList<>();
            for (String phone : phones) {
                ModelObject record = new ModelObject(TableMarketSmsRecord.class);
                record.put(TableMarketSmsRecord.phone, phone);
                record.put(TableMarketSmsRecord.smsId, smsParams.getSid());
                if (sms.getIntValue(TableMarketSms.type) == 1) {
                    record.put(TableMarketSmsRecord.content, smsParams.getTemplateCode());
                } else {
                    record.put(TableMarketSmsRecord.content, smsParams.getContent());
                }
                if (msg != null) {
                    record.put(TableMarketSmsRecord.result, msg.length() > 200 ? msg.substring(0, 200) : msg);
                }
                record.put(TableMarketSmsRecord.action, smsParams.getAction());
                record.put(TableMarketSmsRecord.createTime, new Date());
                records.add(record);
            }
            sessionTemplate.save(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ModelObject getSmsById(long id) {
        ModelObject sms = sessionTemplate.get(
                Criteria.query(TableMarketSms.class)
                        .eq(TableMarketSms.id, id)
                        .eq(TableMarketSms.enable, 1));
        return sms;
    }

    @Override
    public List<ModelObject> getEnableSms(String keyword) {
        Query query = Criteria.query(TableMarketSms.class);
        query.order(TableMarketSms.id, true);
        query.eq(TableMarketSms.enable, 1);
        if (StringUtils.isNotBlank(keyword)) {
            query.like(TableMarketSms.name, "%" + keyword + "%");
        }
        return sessionTemplate.list(query);
    }

    @Override
    public void enableSms(int id) {
        ModelObject update = new ModelObject(TableMarketSms.class);
        update.put(TableMarketSms.id, id);
        update.put(TableMarketSms.enable, 1);
        sessionTemplate.update(update);
    }

    @Override
    public void disableSms(int id) {
        ModelObject update = new ModelObject(TableMarketSms.class);
        update.put(TableMarketSms.id, id);
        update.put(TableMarketSms.enable, 0);
        sessionTemplate.update(update);
    }

    @Override
    public ModelObject getLastSms(String phone, SmsAction action) {
        return sessionTemplate.get(
                Criteria.query(TableMarketSmsRecord.class)
                        .eq(TableMarketSmsRecord.phone, phone)
                        .eq(TableMarketSmsRecord.action, action.getCode())
                        .order(TableMarketSmsRecord.id, false)
                        .limit(0, 1)
        );
    }

    @Override
    public void addSmsAuthCode(String type, String phone, String code) {
        ModelObject object = new ModelObject(TableMarketSmsAuthCode.class);
        object.put(TableMarketSmsAuthCode.type, type);
        object.put(TableMarketSmsAuthCode.phone, phone);
        object.put(TableMarketSmsAuthCode.code, code);
        object.put(TableMarketSmsAuthCode.createTime, new Date());
        sessionTemplate.save(object);
    }

    @Override
    public ModelObject getSmsAuthCode(String type, String phone) {
        return sessionTemplate.get(
                Criteria.query(TableMarketSmsAuthCode.class)
                        .eq(TableMarketSmsAuthCode.type, type)
                        .eq(TableMarketSmsAuthCode.phone, phone)
                        .order(TableMarketSmsRecord.id, false)
                        .limit(0, 1)
        );
    }
}
