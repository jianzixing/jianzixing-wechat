package com.jianzixing.webapp.service.trigger;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.exception.TransactionException;

import java.util.List;

/**
 * 触发器只和用户相关，所以参数params中必须传入userId参数
 */
public interface TriggerService {
    ModelObject trigger(long uid, EventType type, ModelObject params);

    void addTrigger(ModelObject object) throws ModelCheckerException, TransactionException, ModuleException;

    void updateTrigger(ModelObject object) throws ModelCheckerException, TransactionException, ModuleException;

    List<ModelObject> getEvents();

    Paging getTriggers(Query query, long start, long limit);

    void deleteTrigger(long id);

    List<ModelObject> getProcessorImpls();

    void runProcessor(ModelObject trigger, ModelObject params) throws ModuleException;

    String executeContentExpression(String content, ModelObject params) throws ModuleException;
}
