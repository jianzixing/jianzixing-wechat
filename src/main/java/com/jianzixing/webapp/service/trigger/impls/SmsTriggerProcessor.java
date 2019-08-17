package com.jianzixing.webapp.service.trigger.impls;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.marketing.SmsParams;
import com.jianzixing.webapp.service.marketing.SmsType;
import com.jianzixing.webapp.service.trigger.EventType;
import com.jianzixing.webapp.service.trigger.ProcessorField;
import com.jianzixing.webapp.service.trigger.ProcessorInterface;
import com.jianzixing.webapp.service.trigger.ProcessorValueType;
import com.jianzixing.webapp.tables.marketing.TableMarketSms;
import com.jianzixing.webapp.tables.trigger.TableTrigger;
import com.jianzixing.webapp.tables.user.TableUser;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class SmsTriggerProcessor implements ProcessorInterface {
    @Override
    public String getName() {
        return "发送短信";
    }

    @Override
    public String getDetail() {
        return "默认使用 (短信服务管理) 中的已启用的短信服务";
    }

    @Override
    public ProcessorValueType getValueType() {
        return ProcessorValueType.SMS;
    }

    /**
     * 可接收参数phones和userId，如果有phones有限使用phones参数
     *
     * @param trigger 当前执行的触发器信息
     * @param values  处理器需要的配置值，比如赠送积分数量
     * @param params  每个触发事件的传入值，{@link EventType}中配置的输入值
     * @throws ModuleException
     */
    @Override
    public void processor(ModelObject trigger, ModelObject values, ModelObject params) throws ModuleException {
        List<String> phones = new ArrayList<>();
        if (trigger != null) {
            if (params.containsKey("phones")) {
                Object phone = params.get("phones");
                if (phone instanceof String) {
                    phones.add((String) phone);
                }
                if (phone instanceof List) {
                    phones.addAll((Collection<? extends String>) phone);
                }
            } else {
                long userId = params.getLongValue("userId");
                if (userId > 0) {
                    ModelObject user = GlobalService.userService.getUser(userId);
                    if (user != null) {
                        String phone = user.getString(TableUser.phone);
                        phones.add(phone);
                    }
                }
            }

            if (phones.size() > 0) {
                long sid = trigger.getLongValue(TableTrigger.sid);
                ModelObject sms = GlobalService.smsService.getSmsById(sid);
                if (sms == null) {
                    throw new ModuleException(StockCode.ARG_NULL, "短信服[" + sid + "]务未找到或未启用");
                }
                int type = sms.getIntValue(TableMarketSms.type);
                String content = trigger.getString(TableTrigger.content);
                SmsParams smsParams = SmsParams.create();
                if (type == SmsType.TEMPLATE.getCode()) {
                    smsParams.setTemplateCode(content.trim());
                }

                params.remove("phones");
                int action = params.getIntValue("action");
                params.remove("action");
                smsParams.setAction(action);
                smsParams.setSid(sid);
                smsParams.setSms(sms);
                smsParams.setPhones(phones);
                if (type == SmsType.TEMPLATE.getCode()) {
                    smsParams.setTemplateParam(params);
                } else {
                    smsParams.setContent(GlobalService.triggerService.executeContentExpression(content, params));
                }
                GlobalService.smsService.sendSms(smsParams);
            }
        }
    }

    @Override
    public ProcessorField[] getParams() {
        return new ProcessorField[0];
    }
}
