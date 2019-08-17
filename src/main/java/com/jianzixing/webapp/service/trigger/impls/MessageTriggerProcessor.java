package com.jianzixing.webapp.service.trigger.impls;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.trigger.ProcessorField;
import com.jianzixing.webapp.service.trigger.ProcessorInterface;
import com.jianzixing.webapp.service.trigger.ProcessorValueType;
import com.jianzixing.webapp.tables.trigger.TableTrigger;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Service;

@Service
public class MessageTriggerProcessor implements ProcessorInterface {
    @Override
    public String getName() {
        return "发送站内信";
    }

    @Override
    public String getDetail() {
        return "发送站内信给用户";
    }

    @Override
    public ProcessorValueType getValueType() {
        return ProcessorValueType.MESSAGE;
    }

    @Override
    public void processor(ModelObject trigger, ModelObject values, ModelObject params) throws Exception {
        String title = trigger.getString(TableTrigger.title);
        String content = trigger.getString(TableTrigger.content);
        if (StringUtils.isNotBlank(title) && StringUtils.isNotBlank(content)) {
            long userId = params.getLongValue("userId");
            title = GlobalService.triggerService.executeContentExpression(title, params);
            content = GlobalService.triggerService.executeContentExpression(content, params);
            GlobalService.messageService.send(0, userId, title, content);
        }
    }

    @Override
    public ProcessorField[] getParams() {
        return new ProcessorField[0];
    }
}
