package com.jianzixing.webapp.service.user;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;

import java.util.List;

public interface UserLevelService {
    List<ModelObject> getLevels();

    void addLevel(ModelObject object) throws ModelCheckerException;

    void updateLevel(ModelObject object) throws ModelCheckerException;

    void deleteLevel(int id);

    ModelObject getLevelByAmount(long levelAmount);

    void setLevelAmountChange(long uid, int amount, long before, long after, String msg);

    List<ModelObject> getLevels(List<Integer> levelIds);

    ModelObject getUserLevel(ModelObject user);
}
