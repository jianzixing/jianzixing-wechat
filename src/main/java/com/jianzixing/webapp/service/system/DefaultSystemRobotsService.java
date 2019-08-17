package com.jianzixing.webapp.service.system;

import com.jianzixing.webapp.tables.system.TableRobots;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultSystemRobotsService implements SystemRobotsService, InitializingBean {
    private static String ROBOTS = null;

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addRobots(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableRobots.class);
        object.checkAndThrowable();

        sessionTemplate.save(object);
        this.reloadRobots();
    }

    @Override
    public void delRobots(int id) {
        sessionTemplate.delete(TableRobots.class, id);
        this.reloadRobots();
    }

    @Override
    public void updateRobots(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableRobots.class);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
        this.reloadRobots();
    }

    @Override
    public List<ModelObject> getRobotsObject() {
        return sessionTemplate.list(Criteria.query(TableRobots.class).order(TableRobots.pos, true));
    }

    @Override
    public void reloadRobots() {
        List<ModelObject> objects = sessionTemplate.list(Criteria.query(TableRobots.class).order(TableRobots.pos, true));
        if (objects != null) {
            StringBuilder sb = new StringBuilder();
            for (ModelObject object : objects) {
                String key = object.getString(TableRobots.cmd);
                String value = object.getString(TableRobots.value);
                if (StringUtils.isNotBlank(key)) {
                    sb.append(key);
                }
                if (StringUtils.isNotBlank(value)) {
                    sb.append(": ");
                    sb.append(value);
                    sb.append("\r\n");
                }
            }
            ROBOTS = sb.toString();
        }
    }

    @Override
    public String getRobots() {
        return ROBOTS;
    }

    @Override
    public void updateRobotsPos(int id, int pos) {
        ModelObject object = new ModelObject(TableRobots.class);
        object.put(TableRobots.id, id);
        object.put(TableRobots.pos, pos);
        sessionTemplate.update(object);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.reloadRobots();
    }
}
