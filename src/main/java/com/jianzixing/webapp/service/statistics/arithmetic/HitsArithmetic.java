package com.jianzixing.webapp.service.statistics.arithmetic;

import com.jianzixing.webapp.service.statistics.AbstractRankingArithmetic;
import com.jianzixing.webapp.tables.statistics.TableHitStatistics;
import com.jianzixing.webapp.tables.statistics.TableRankingContent;
import com.jianzixing.webapp.tables.statistics.TableRankingRule;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.BasicFunction;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class HitsArithmetic extends AbstractRankingArithmetic {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void run() {
        List<ModelObject> objects = this.getRules(this.getClass());
        if (objects != null) {
            for (ModelObject object : objects) {
                
            }
        }
    }

    @Override
    public String getName() {
        return "点击量排行";
    }

    @Override
    public int getType() {
        return -1;
    }

    @Override
    public boolean canRun() {
        return true;
    }
}
