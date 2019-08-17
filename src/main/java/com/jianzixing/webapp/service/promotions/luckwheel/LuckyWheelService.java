package com.jianzixing.webapp.service.promotions.luckwheel;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.util.List;

public interface LuckyWheelService {
    void addRule(ModelObject object) throws ModelCheckerException;

    void deleteRule(long id);

    void updateRule(ModelObject object) throws ModelCheckerException;

    Paging getRules(Query query, long start, long limit);

    void addAwards(List<ModelObject> awards) throws ModelCheckerException, ModuleException;

    void updateAwards(List<ModelObject> awards) throws ModelCheckerException, ModuleException;

    void randomAward(long uid, long rid) throws ModuleException;
}
