package com.jianzixing.webapp.service.trigger.impls;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.trigger.ProcessorField;
import com.jianzixing.webapp.service.trigger.ProcessorInterface;
import com.jianzixing.webapp.service.trigger.ProcessorValueType;
import com.jianzixing.webapp.tables.trigger.TableTrigger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mimosaframework.core.json.ModelObject;

public class UserLevelProcessor implements ProcessorInterface {
    @Override
    public String getName() {
        return "赠送等级分数";
    }

    @Override
    public String getDetail() {
        return "赠送等级分数，该分数达到指定等级数量时，用户可以升级。";
    }

    @Override
    public ProcessorValueType getValueType() {
        return ProcessorValueType.PARAMS;
    }

    @Override
    public void processor(ModelObject trigger, ModelObject values, ModelObject params) throws Exception {
        String amountTmpl = values.getString("amount");
        String amountStr = GlobalService.triggerService.executeContentExpression(amountTmpl, params);
        if (NumberUtils.isNumber(amountStr)) {
            int amount = (int) Double.parseDouble(amountStr);

            Long uid = params.getLongValue("userId");
            if (uid != null && uid > 0) {
                String msg = trigger.getString(TableTrigger.name);

                if (StringUtils.isNotBlank(msg)) {
                    msg = msg + ",等级分数" + amount + "分";
                } else {
                    msg = "等级分数" + amount + "分";
                }
                GlobalService.userService.updateUserLevelAmount(uid, amount, msg);
            }
        }
    }

    @Override
    public ProcessorField[] getParams() {
        return new ProcessorField[]{new ProcessorField("等级分数", String.class, "amount", "赠送的等级分数")};
    }
}
