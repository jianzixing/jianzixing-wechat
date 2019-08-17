package com.jianzixing.webapp.service.statistics;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.recommend.AbstractRecommend;
import com.jianzixing.webapp.tables.statistics.TableRankingContent;
import com.jianzixing.webapp.tables.statistics.TableRankingRule;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DefaultRankingService implements RankingService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void addRule(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableRankingRule.class);
        object.checkAndThrowable();

        String code = object.getString(TableRankingRule.code);
        ModelObject old = sessionTemplate.get(Criteria.query(TableRankingRule.class).eq(TableRankingRule.code, code));
        if (old != null) {
            throw new ModuleException(StockCode.EXIST_OBJ, "当前排行码已经存在!");
        }

        sessionTemplate.save(object);
    }

    @Override
    public void delRule(int id) {
        sessionTemplate.delete(TableRankingRule.class, id);
    }

    @Override
    public void updateRule(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableRankingRule.class);
        object.checkUpdateThrowable();

        String code = object.getString(TableRankingRule.code);
        int id = object.getIntValue(TableRankingRule.id);
        ModelObject old = sessionTemplate.get(Criteria.query(TableRankingRule.class).eq(TableRankingRule.code, code)
                .ne(TableRankingRule.id, id));
        if (old != null) {
            throw new ModuleException(StockCode.EXIST_OBJ, "当前排行码已经存在!");
        }

        sessionTemplate.update(object);
    }

    @Override
    public Paging getRules(int start, int limit) {
        return sessionTemplate.paging(Criteria.query(TableRankingRule.class).limit(start, limit).order(TableRankingRule.id, false));
    }

    @Override
    public Paging getContents(int ruleId, int start, int limit) {
        ModelObject ruleObject = sessionTemplate.get(
                Criteria.query(TableRankingRule.class).eq(TableRankingRule.enable, 1).eq(TableRankingRule.id, ruleId));
        if (ruleObject != null) {
            int type = ruleObject.getIntValue(TableRankingRule.type);
            AbstractRecommend recommend = this.getRecommendImpl(type);
            if (recommend != null) {
                Paging paging = sessionTemplate.paging(
                        Criteria.query(TableRankingContent.class)
                                .eq(TableRankingContent.isPassed, 0)
                                .limit(start, limit).order(TableRankingContent.score, false));
                List<Long> ids = new ArrayList<>();
                if (paging != null) {
                    List<ModelObject> objects = paging.getObjects();
                    for (ModelObject object : objects) {
                        ids.add(object.getLong(TableRankingContent.value));
                    }

                    List<ModelObject> rels = recommend.getRecommends(ids);
                    if (rels != null) {
                        for (ModelObject object : objects) {
                            long id = object.getLongValue(TableRankingContent.value);
                            for (ModelObject rel : rels) {
                                if (recommend.equalsPrimaryKey(id, rel)) {
                                    object.put("Content", rel);
                                }
                            }
                        }
                    }
                }
                return paging;
            }
        }
        return null;
    }

    @Override
    public void executor() {
        Map<String, RankingArithmetic> beans = applicationContext.getBeansOfType(RankingArithmetic.class);
        if (beans != null) {
            for (Map.Entry<String, RankingArithmetic> entry : beans.entrySet()) {
                RankingArithmetic arithmetic = entry.getValue();
                if (arithmetic.canRun()) {
                    arithmetic.run();
                }
            }
        }
    }


    public AbstractRecommend getRecommendImpl(int type) {
        Map<String, AbstractRecommend> beans = applicationContext.getBeansOfType(AbstractRecommend.class);
        if (beans != null) {
            for (Map.Entry<String, AbstractRecommend> entry : beans.entrySet()) {
                if (entry.getValue().getType() == type) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public List<ModelObject> getArithmetics(int type) {
        Map<String, RankingArithmetic> beans = applicationContext.getBeansOfType(RankingArithmetic.class);
        List<ModelObject> result = new ArrayList<>();
        if (beans != null) {
            for (Map.Entry<String, RankingArithmetic> entry : beans.entrySet()) {
                RankingArithmetic arithmetic = entry.getValue();
                String name = arithmetic.getName();
                int t = arithmetic.getType();
                if (type == t || t < 0) {
                    ModelObject object = new ModelObject();
                    object.put("name", name);
                    object.put("type", t);
                    object.put("clazz", arithmetic.getClass().getName());
                    result.add(object);
                }
            }
        }
        return result;
    }
}
