package com.jianzixing.webapp.service.recommend;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.recommend.TableRecommendContent;
import com.jianzixing.webapp.tables.recommend.TableRecommendGroup;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaultRecommendService implements RecommendService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void addGroup(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableRecommendGroup.class);
        object.put(TableRecommendGroup.createTime, new Date());
        object.checkAndThrowable();

        String code = object.getString(TableRecommendGroup.code);
        ModelObject old = sessionTemplate.get(Criteria.query(TableRecommendGroup.class).eq(TableRecommendGroup.code, code));
        if (old != null) {
            throw new ModuleException(StockCode.EXIST_OBJ, "分组码已经存在,请重新填写");
        }

        sessionTemplate.save(object);
    }

    @Override
    public void delGroup(int id) {
        sessionTemplate.delete(TableRecommendGroup.class, id);
    }

    @Override
    public void updateGroup(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableRecommendGroup.class);
        object.put(TableRecommendGroup.createTime, new Date());
        object.remove(TableRecommendGroup.admin);

        object.checkUpdateThrowable();

        String code = object.getString(TableRecommendGroup.code);
        int id = object.getIntValue(TableRecommendGroup.id);
        ModelObject old = sessionTemplate.get(Criteria.query(TableRecommendGroup.class)
                .eq(TableRecommendGroup.code, code).ne(TableRecommendGroup.id, id));
        if (old != null) {
            throw new ModuleException(StockCode.EXIST_OBJ, "分组码已经存在,请重新填写");
        }
        sessionTemplate.update(object);
    }

    @Override
    public List<ModelObject> getTreeGroup() {
        List<ModelObject> objects = sessionTemplate.list(Criteria.query(TableRecommendGroup.class).order(TableRecommendGroup.id, true));
        return ModelUtils.getListToTree(objects, TableRecommendGroup.id, TableRecommendGroup.pid, "children");
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
    public void addContent(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableRecommendContent.class);
        int groupId = object.getIntValue(TableRecommendContent.groupId);
        ModelObject group = sessionTemplate.get(TableRecommendGroup.class, groupId);
        if (group == null) {
            throw new ModuleException(StockCode.ARG_NULL, "推荐分组不存在");
        }
        int type = object.getIntValue(TableRecommendContent.type);
        int groupType = group.getIntValue(TableRecommendGroup.type);
        if (groupType == 100) {

        } else {
            object.put(TableRecommendContent.type, groupType);
            type = groupType;
        }
        object.checkAndThrowable();
        if (type == 0) {
            String url = object.getString(TableRecommendContent.url);
            if (StringUtils.isBlank(url)) {
                throw new ModuleException(StockCode.ARG_NULL, "自定义链接地址必须填写");
            }
        } else {
            long value = object.getLongValue(TableRecommendContent.value);
            if (value == 0) {
                throw new ModuleException(StockCode.ARG_NULL, "内容ID必须填写");
            }
            AbstractRecommend impl = this.getRecommendImpl(type);
            if (impl == null) {
                throw new ModuleException(StockCode.ARG_NULL, "关联的推荐内容不存在或无效");
            }
            ModelObject relObj = impl.getRecommend(value);
            if (relObj == null) {
                throw new ModuleException(StockCode.ARG_NULL, "关联的推荐内容不存在或无效");
            }
            if (relObj.containsKey("type")) {
                object.put(TableRecommendContent.subType, relObj.getString("type"));
            }
        }
        object.put(TableRecommendContent.createTime, new Date());

        sessionTemplate.save(object);
    }

    @Override
    public void delContent(int id) {
        sessionTemplate.delete(TableRecommendContent.class, id);
    }

    @Override
    public void updateContent(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableRecommendContent.class);
        object.checkUpdateThrowable();

        int type = object.getIntValue(TableRecommendContent.type);
        if (type == 0) {
            String url = object.getString(TableRecommendContent.url);
            if (StringUtils.isBlank(url)) {
                object.remove(TableRecommendContent.url);
            }
        } else {
            long value = object.getLongValue(TableRecommendContent.value);
            if (value == 0) {
                object.remove(TableRecommendContent.value);
            } else {
                AbstractRecommend impl = this.getRecommendImpl(type);
                if (impl == null) {
                    throw new ModuleException(StockCode.ARG_NULL, "关联的推荐内容不存在或无效");
                }
                ModelObject relObj = impl.getRecommend(value);
                if (relObj == null) {
                    throw new ModuleException(StockCode.ARG_NULL, "关联的推荐内容不存在或无效");
                }
                if (relObj.containsKey("type")) {
                    object.put(TableRecommendContent.subType, relObj.getString("type"));
                }
            }
        }

        sessionTemplate.update(object);
    }

    @Override
    public Paging getContents(int gid, int start, int limit) {
        Query query = Criteria.query(TableRecommendContent.class).limit(start, limit);
        query.eq(TableRecommendContent.groupId, gid);
        query.order(TableRecommendContent.top, false);
        query.order(TableRecommendContent.createTime, false);
        return sessionTemplate.paging(query);
    }

    @Override
    public List<ModelObject> getRecommends(String code, int count) {
        ModelObject object = this.getRecommendGroupByCode(code);
        return this.getRecommendsByObject(object, count);
    }

    @Override
    public List<ModelObject> getRecommendContents(String code, int count) {
        ModelObject object = this.getRecommendGroupByCode(code);
        List<ModelObject> recommends = this.getRecommendsByObject(object, count);
        if (recommends != null) {
            Map<Integer, List<ModelObject>> map = new LinkedHashMap<>();
            for (ModelObject o : recommends) {
                int type = o.getIntValue(TableRecommendContent.type);
                List<ModelObject> c = map.get(type);
                if (c == null) {
                    c = new ArrayList<>();
                }
                c.add(o);
                map.put(type, c);
            }
            for (Map.Entry<Integer, List<ModelObject>> entry : map.entrySet()) {
                int type = entry.getKey();
                List<ModelObject> rs = this.getRecommendContentsByObjects(type, entry.getValue());
                if (rs != null) {
                    for (ModelObject m : rs) {
                        for (ModelObject o : recommends) {
                            int t = o.getIntValue(TableRecommendContent.type);
                            long v = o.getLongValue(TableRecommendContent.value);
                            if (t == type && m.getLongValue("id") == v) {
                                o.put("Content", m);
                            }
                        }
                    }
                }
            }
            return recommends;
        }
        return null;
    }

    public List<ModelObject> getRecommendContentsByObjects(int type, List<ModelObject> objects) {
        List<Long> ids = new ArrayList<>();
        if (objects != null) {
            objects.forEach(c -> ids.add(c.getLongValue(TableRecommendContent.value)));
        }

        AbstractRecommend impl = this.getRecommendImpl(type);
        if (impl != null) {
            return impl.getRecommends(ids);
        }
        return null;
    }

    @Override
    public ModelObject getLevelRecommends(String code, int count) {
        ModelObject object = this.getRecommendGroupByCode(code);

        if (object != null) {
            List<ModelObject> childs = this.getChildRecommendGroup(object.getIntValue(TableRecommendGroup.id));
            if (childs != null) {
                ModelObject result = new ModelObject();
                for (ModelObject c : childs) {
                    String codeStr = c.getString(TableRecommendGroup.code);
                    result.put(codeStr, this.getRecommendsByObject(c, count));
                }
                return result;
            }
        }
        return null;
    }

    @Override
    public ModelObject getRecommendGroupByCode(String code) {
        return sessionTemplate.get(Criteria.query(TableRecommendGroup.class).eq(TableRecommendGroup.code, code));
    }

    @Override
    public List<ModelObject> getRecommendsByObject(ModelObject object, int count) {
        if (object != null) {
            List<ModelObject> res = sessionTemplate.list(
                    Criteria.query(TableRecommendContent.class)
                            .eq(TableRecommendContent.groupId, object.getIntValue(TableRecommendGroup.id))
                            .eq(TableRecommendContent.top, 0)
                            .order(TableRecommendContent.createTime, false)
                            .limit(0, count)
            );
            List<ModelObject> tops = sessionTemplate.list(
                    Criteria.query(TableRecommendContent.class)
                            .eq(TableRecommendContent.groupId, object.getIntValue(TableRecommendGroup.id))
                            .ne(TableRecommendContent.top, 0)
                            .order(TableRecommendContent.top, false)
                            .limit(0, count)
            );

            List<ModelObject> result = new ArrayList<>();
            if (tops != null) {
                result.addAll(tops);
                if (res != null) {
                    for (ModelObject o : res) {
                        if (result.size() >= count) {
                            break;
                        } else {
                            result.add(o);
                        }
                    }
                }
            } else {
                if (res != null) {
                    result.addAll(res);
                }
            }

            return result;
        }
        return null;
    }

    @Override
    public List<ModelObject> getChildRecommendGroup(int pid) {
        return sessionTemplate.list(Criteria.query(TableRecommendGroup.class).eq(TableRecommendGroup.pid, pid));
    }

    @Override
    public void setRecommendTop(int id, int level) {
        ModelObject object = new ModelObject(TableRecommendContent.class);
        object.put(TableRecommendContent.id, id);
        object.put(TableRecommendContent.top, level);
        sessionTemplate.update(object);
    }

    @Override
    public void setRecommendImage(int id, String cover) {
        ModelObject object = new ModelObject(TableRecommendContent.class);
        object.put(TableRecommendContent.id, id);
        object.put(TableRecommendContent.cover, cover);
        sessionTemplate.update(object);
    }
}
