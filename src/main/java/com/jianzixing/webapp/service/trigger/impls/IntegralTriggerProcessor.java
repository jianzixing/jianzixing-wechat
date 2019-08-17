package com.jianzixing.webapp.service.trigger.impls;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.trigger.ProcessorField;
import com.jianzixing.webapp.service.trigger.ProcessorInterface;
import com.jianzixing.webapp.service.trigger.ProcessorValueType;
import com.jianzixing.webapp.tables.trigger.TableTrigger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Service;

@Service
public class IntegralTriggerProcessor implements ProcessorInterface {
    @Override
    public String getName() {
        return "赠送积分";
    }

    @Override
    public String getDetail() {
        return "使用当前处理器可以赠送积分给用户";
    }

    @Override
    public ProcessorValueType getValueType() {
        return ProcessorValueType.PARAMS;
    }

    @Override
    public void processor(ModelObject trigger, ModelObject values, ModelObject params) throws ModuleException {
        String amountTmpl = values.getString("amount");
        String amountStr = GlobalService.triggerService.executeContentExpression(amountTmpl, params);
        if (NumberUtils.isNumber(amountStr)) {
            int amount = (int) Double.parseDouble(amountStr);

            Long uid = params.getLongValue("userId");
            if (uid != null && uid > 0) {
                String msg = trigger.getString(TableTrigger.name);

                if (StringUtils.isNotBlank(msg)) {
                    msg = msg + ",赠送积分" + amount + "个";
                } else {
                    msg = "赠送积分" + amount + "个";
                }
                GlobalService.integralService.changeUserIntegral(uid, amount, msg);
            }
        }
    }

    @Override
    public ProcessorField[] getParams() {
        return new ProcessorField[]{new ProcessorField("积分数量", String.class, "amount", "赠送的积分数量")};
    }
}
