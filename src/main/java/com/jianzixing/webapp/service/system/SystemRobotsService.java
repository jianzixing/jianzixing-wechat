package com.jianzixing.webapp.service.system;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;

import java.util.List;

public interface SystemRobotsService {
    void addRobots(ModelObject object) throws ModelCheckerException;

    void delRobots(int id);

    void updateRobots(ModelObject object) throws ModelCheckerException;

    List<ModelObject> getRobotsObject();

    void reloadRobots();

    String getRobots();

    void updateRobotsPos(int id, int pos);
}
