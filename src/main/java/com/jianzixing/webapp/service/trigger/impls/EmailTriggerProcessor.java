package com.jianzixing.webapp.service.trigger.impls;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.marketing.EmailParams;
import com.jianzixing.webapp.service.trigger.EventType;
import com.jianzixing.webapp.service.trigger.ProcessorField;
import com.jianzixing.webapp.service.trigger.ProcessorInterface;
import com.jianzixing.webapp.service.trigger.ProcessorValueType;
import com.jianzixing.webapp.tables.trigger.TableTrigger;
import com.jianzixing.webapp.tables.user.TableUser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class EmailTriggerProcessor implements ProcessorInterface {
    private static final Log logger = LogFactory.getLog(EmailTriggerProcessor.class);

    @Override
    public String getName() {
        return "发送邮件";
    }

    @Override
    public String getDetail() {
        return "默认使用 (邮件服务管理) 中的已启用的邮件服务";
    }

    @Override
    public ProcessorValueType getValueType() {
        return ProcessorValueType.EMAIL;
    }

    /**
     * 可接收参数userId或者emails，优先使用emails参数
     *
     * @param trigger 当前执行的触发器信息
     * @param values  处理器需要的配置值，比如赠送积分数量
     * @param params  每个触发事件的传入值，{@link EventType}中配置的输入值
     * @throws Exception
     */
    @Override
    public void processor(ModelObject trigger, ModelObject values, ModelObject params) throws Exception {
        List<String> addresses = new ArrayList<>();

        if (params.containsKey("emails")) {
            Object emails = params.get("emails");
            if (emails instanceof String) {
                addresses.add((String) emails);
            }
            if (emails instanceof List) {
                addresses.addAll((Collection<? extends String>) emails);
            }
        } else {
            long userId = params.getLongValue("userId");
            if (userId > 0) {
                ModelObject user = GlobalService.userService.getUser(userId);
                if (user != null) {
                    String email = user.getString(TableUser.email);
                    addresses.add(email);
                }
            }
        }

        if (addresses.size() > 0) {
            String subject = trigger.getString("title");
            if (StringUtils.isBlank(subject)) {
                subject = trigger.getString(TableTrigger.title);
            }
            if (StringUtils.isBlank(subject)) {
                subject = trigger.getString(TableTrigger.name);
            }

            long sid = trigger.getLongValue(TableTrigger.sid);
            ModelObject email = GlobalService.emailService.getEmailById(sid);
            EmailParams emailParams = EmailParams.create().setEmail(email);
            emailParams.setSid(sid);
            emailParams.setAddresses(addresses);

            if (StringUtils.isNotBlank(subject)) {
                subject = GlobalService.triggerService.executeContentExpression(subject, params);
            }
            String content = trigger.getString(TableTrigger.content);
            if (StringUtils.isNotBlank(content)) {
                content = GlobalService.triggerService.executeContentExpression(content, params);
                emailParams.setSubject(subject);
                emailParams.setContent(content);
                GlobalService.emailService.sendEmail(emailParams);
                logger.info("触发器发送邮件:" + subject + " , " + String.join(",", addresses));
            }
        }
    }

    @Override
    public ProcessorField[] getParams() {
        return new ProcessorField[]{};
    }
}
