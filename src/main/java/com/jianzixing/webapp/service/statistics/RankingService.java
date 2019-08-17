package com.jianzixing.webapp.service.statistics;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

import java.util.List;

public interface RankingService {
    void addRule(ModelObject object) throws ModelCheckerException, ModuleException;

    void delRule(int id);

    void updateRule(ModelObject object) throws ModelCheckerException, ModuleException;

    Paging getRules(int start, int limit);

    Paging getContents(int ruleId, int start, int limit);

    /**
     * 执行所有的排行算法
     */
    void executor();

    List<ModelObject> getArithmetics(int type);
}
