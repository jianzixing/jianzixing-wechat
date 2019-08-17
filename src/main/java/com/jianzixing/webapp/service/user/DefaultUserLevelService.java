package com.jianzixing.webapp.service.user;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.user.TableUser;
import com.jianzixing.webapp.tables.user.TableUserLevel;
import com.jianzixing.webapp.tables.user.TableUserLevelRecord;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DefaultUserLevelService implements UserLevelService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public List<ModelObject> getLevels() {
        return sessionTemplate.list(Criteria.query(TableUserLevel.class));
    }

    @Override
    public void addLevel(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableUserLevel.class);
        object.checkAndThrowable();
        sessionTemplate.save(object);
    }

    @Override
    public void updateLevel(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableUserLevel.class);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public void deleteLevel(int id) {
        sessionTemplate.delete(TableUserLevel.class, id);
    }

    @Override
    public ModelObject getLevelByAmount(long levelAmount) {
        List<ModelObject> levels = sessionTemplate.list(Criteria.query(TableUserLevel.class));
        if (levels != null) {
            for (ModelObject level : levels) {
                long start = level.getLongValue(TableUserLevel.startAmount);
                long end = level.getLongValue(TableUserLevel.endAmount);
                if (end == 0) {
                    if (start <= levelAmount) {
                        return level;
                    }
                } else {
                    if (start <= levelAmount && end >= levelAmount) {
                        return level;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void setLevelAmountChange(long uid, int amount, long before, long after, String msg) {
        ModelObject object = new ModelObject(TableUserLevelRecord.class);
        object.put(TableUserLevelRecord.userId, uid);
        object.put(TableUserLevelRecord.changeAmount, amount);
        object.put(TableUserLevelRecord.beforeAmount, before);
        object.put(TableUserLevelRecord.afterAmount, after);
        object.put(TableUserLevelRecord.detail, msg);
        object.put(TableUserLevelRecord.createTime, new Date());
        sessionTemplate.save(object);
    }

    @Override
    public List<ModelObject> getLevels(List<Integer> levelIds) {
        if (levelIds != null && levelIds.size() > 0) {
            return sessionTemplate.list(Criteria.query(TableUserLevel.class).in(TableUserLevel.id, levelIds));
        }
        return null;
    }

    @Override
    public ModelObject getUserLevel(ModelObject user) {
        long levelAmount = user.getLongValue(TableUser.levelAmount);
        return this.getLevelByAmount(levelAmount);
    }
}
