package com.jianzixing.webapp.service.wechatsm;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

public interface WeChatMiniProgramService {
    void addMiniProgram(ModelObject object) throws ModelCheckerException;

    void delMiniProgram(int id);

    void updateMiniProgram(ModelObject object) throws ModelCheckerException;

    Paging getMiniPrograms(Query query, int start, int limit);

    ModelObject getMiniProgramByCode(String code);

    ModelObject getMiniProgramById(int accountId);

    void updateMiniProgramInfo(ModelObject update) throws ModelCheckerException;

    ModelObject getDefaultAccount();

    void setDefaultAccount(int openType, int accountId);

    void setEmptyAccountToken(int accountId);

    void setEmptyAccountToken(String code);
}
