package com.jianzixing.webapp.service.trigger;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.json.ModelObject;

public interface ProcessorInterface {
    String getName();

    String getDetail();

    /**
     * 处理器值类型，比如富文本，普通文本
     *
     * @return
     */
    ProcessorValueType getValueType();

    /**
     * 所有参数包括用户填写或者上下文传入
     *
     * @param trigger 当前执行的触发器信息
     * @param values  处理器需要的配置值，比如赠送积分数量
     * @param params  每个触发事件的传入值，{@link EventType}中配置的输入值
     */
    void processor(ModelObject trigger, ModelObject values, ModelObject params) throws Exception;

    /**
     * 需要用户填写的参数
     *
     * @return
     */
    ProcessorField[] getParams();
}
