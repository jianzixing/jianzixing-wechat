package com.jianzixing.webapp.service.support;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;

import java.util.List;

public interface SupportService {
    void addSupport(ModelObject object) throws ModelCheckerException;

    void delSupport(int id);

    void updateSupport(ModelObject object) throws ModelCheckerException;

    List<ModelObject> getSupports();

    List<ModelObject> getSupportByArray(List<String> ids);

    List<ModelObject> getSupportsByGroup(long groupId);

    List<ModelObject> getSupportsByGoods(long goodsId);

    ModelObject getAfterSalesByOrder(ModelObject order, long goodsId);
}
