package com.jianzixing.webapp.service.wechatsm;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.io.IOException;

/**
 * 定时群发功能
 */
public interface WeChatMassService {

    void addMass(ModelObject object) throws ModelCheckerException, ModuleException;

    void delMass(int id);

    void updateMass(ModelObject object) throws ModelCheckerException;

    Paging<ModelObject> getMasses(Query query, int openType, int accountId, int start, int limit) throws ModuleException;

    void enableMass(int id);

    void disableMass(int id);

    void triggerTimerMass() throws IOException, ModuleException;
}
