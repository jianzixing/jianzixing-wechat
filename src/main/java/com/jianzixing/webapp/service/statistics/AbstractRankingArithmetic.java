package com.jianzixing.webapp.service.statistics;

import com.jianzixing.webapp.tables.statistics.TableRankingRule;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractRankingArithmetic implements RankingArithmetic {

    @Autowired
    SessionTemplate sessionTemplate;

    private long getLongTime(int cycle, int unit) {
        if (unit == 3) return cycle * 24 * 60 * 60 * 1000l;
        if (unit == 4) return cycle * 60 * 60 * 1000l;
        if (unit == 5) return cycle * 60 * 1000l;
        return 0;
    }

    public List<ModelObject> getRules(Class<? extends RankingArithmetic> arithmeticClass) {
        List<ModelObject> objects = sessionTemplate.list(
                Criteria.query(TableRankingRule.class)
                        .eq(TableRankingRule.enable, 1)
                        .eq(TableRankingRule.algorithm, arithmeticClass.getName()));
        if (objects != null) {
            List<ModelObject> r = new ArrayList<>();
            for (ModelObject object : objects) {
                Date lastRunTime = object.getDate(TableRankingRule.lastRunTime);
                Date startTime = object.getDate(TableRankingRule.startTime);
                int cycle = object.getIntValue(TableRankingRule.cycle);
                int unit = object.getIntValue(TableRankingRule.unit);
                int count = object.getIntValue(TableRankingRule.count);
                String algorithm = object.getString(TableRankingRule.algorithm);

                if (lastRunTime == null) {
                    lastRunTime = startTime;
                }
                long lastRunTimeNumber = lastRunTime.getTime();
                long cycleNumber = this.getLongTime(cycle, unit);
                if ((lastRunTimeNumber + cycleNumber) < System.currentTimeMillis()) {
                    r.add(object);
                }
            }
            return r;
        }
        return null;
    }

    public long getStartTime(ModelObject object) {
        int cycle = object.getIntValue(TableRankingRule.cycle);
        int unit = object.getIntValue(TableRankingRule.unit);
        long cycleNumber = this.getLongTime(cycle, unit);
        return System.currentTimeMillis() - cycleNumber;
    }

    public void setRuleLastRun(ModelObject old, Date date) {
        ModelObject object = new ModelObject();
        object.setObjectClass(TableRankingRule.class);
        object.put(TableRankingRule.id, old.getIntValue(TableRankingRule.id));
        object.put(TableRankingRule.lastRunTime, date);
        sessionTemplate.update(object);
    }
}
