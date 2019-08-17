package com.jianzixing.webapp.service.system;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.system.TableSystemConfig;
import com.jianzixing.webapp.tables.system.TableSystemConfigGroup;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.BasicFunction;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DefaultSystemConfigService implements SystemConfigService {

    @Autowired
    SessionTemplate sessionTemplate;

    private String webUrl = "";

    @Override
    public void addGroup(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableSystemConfigGroup.class);
        object.checkAndThrowable();
        sessionTemplate.save(object);
    }

    @Override
    public void delGroup(int id) throws ModuleException {
        ModelObject old = sessionTemplate.get(TableSystemConfigGroup.class, id);
        if (old != null) {
            ModelObject has = sessionTemplate.get(Criteria.query(TableSystemConfig.class).eq(TableSystemConfig.gid, id));
            if (has != null) {
                throw new ModuleException(StockCode.EXIST_OBJ, "当前分组下有配置不允许删除");
            }
            if (old.getIntValue(TableSystemConfigGroup.isSystem) == 1) {
                throw new ModuleException(StockCode.NOT_ALLOW, "系统分组不允许删除");
            }
            sessionTemplate.delete(TableSystemConfigGroup.class, id);
        }
    }

    @Override
    public void updateGroup(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableSystemConfigGroup.class);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public List<ModelObject> getGroups() {
        return sessionTemplate.list(Criteria.query(TableSystemConfigGroup.class));
    }

    @Override
    public void setSystemConfig(String name, String key, Object value, int isSystem, int pos) {
        ModelObject object = sessionTemplate.get(Criteria.query(TableSystemConfig.class).eq(TableSystemConfig.key, key));
        if (object == null) {
            object = new ModelObject(TableSystemConfig.class);
        }
        object.put(TableSystemConfig.name, name);
        object.put(TableSystemConfig.key, key);
        object.put(TableSystemConfig.value, String.valueOf(value));
        object.put(TableSystemConfig.isSystem, isSystem);
        object.put(TableSystemConfig.pos, pos);
        sessionTemplate.saveAndUpdate(object);
    }

    @Override
    public void setSystemConfig(String key, Object value) {
        ModelObject object = sessionTemplate.get(Criteria.query(TableSystemConfig.class).eq(TableSystemConfig.key, key));
        if (object == null) {
            ModelObject max = sessionTemplate.calculate(Criteria.fun(TableSystemConfig.class).addFunction(BasicFunction.MAX, TableSystemConfig.pos, "pos"));
            long maxPos = max.getLongValue("pos");
            object = new ModelObject(TableSystemConfig.class);
            object.put(TableSystemConfig.isSystem, SystemLevel.NORMAL.getLevel());
            object.put(TableSystemConfig.pos, maxPos++);
        }
        object.put(TableSystemConfig.key, key);
        object.put(TableSystemConfig.value, String.valueOf(value));
        sessionTemplate.saveAndUpdate(object);
    }

    @Override
    public void addSystemConfig(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableSystemConfig.class);
        object.checkAndThrowable();
        String key = object.getString(TableSystemConfig.key);
        ModelObject old = sessionTemplate.get(TableSystemConfig.class, key);
        if (old != null) {
            throw new ModuleException(StockCode.EXIST_OBJ, "当前系统配置键已经存在,请重新填写!");
        }

        object.put(TableSystemConfig.isSystem, 0);
        sessionTemplate.save(object);
    }

    @Override
    public void delSystemConfig(String key) throws ModuleException {
        ModelObject config = sessionTemplate.get(TableSystemConfig.class, key);
        if (config != null) {
            int isSystem = config.getIntValue(TableSystemConfig.isSystem);
            if (isSystem == 0) {
                sessionTemplate.delete(TableSystemConfig.class, key);
            } else {
                throw new ModuleException(StockCode.SYSTEM_MUST, "当前系统配置是内置配置不允许删除!");
            }
        }
    }

    @Override
    public void updateSystemConfig(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableSystemConfig.class);
        object.checkUpdateThrowable();

        sessionTemplate.update(object);
    }

    @Override
    public Paging getSystemConfigs(Query query, int gid, int start, int limit) {
        if (query == null) {
            query = Criteria.query(TableSystemConfig.class);
        }
        query.eq(TableSystemConfig.gid, gid);
        query.order(TableSystemConfig.pos, true);
        query.ne(TableSystemConfig.isSystem, 2);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public String getValue(String key) {
        ModelObject obj = sessionTemplate.get(TableSystemConfig.class, key);
        if (obj != null) {
            return obj.getString(TableSystemConfig.value);
        }
        return null;
    }

    @Override
    public Map<String, String> getValues(String... keys) {
        List<ModelObject> objects = sessionTemplate.list(Criteria.query(TableSystemConfig.class).in(TableSystemConfig.key, keys));
        if (objects != null) {
            Map<String, String> map = new LinkedHashMap<>();
            for (ModelObject object : objects) {
                map.put(object.getString(TableSystemConfig.key), object.getString(TableSystemConfig.value));
            }
            return map;
        }
        return null;
    }

    @Override
    public void setWebUrl(String url) {
        if (url.equals(webUrl)) {
            return;
        }
        ModelObject object = new ModelObject(TableSystemConfig.class);
        object.put(TableSystemConfig.key, "__web_root_url");
        object.put(TableSystemConfig.value, url);
        object.put(TableSystemConfig.gid, 0);
        object.put(TableSystemConfig.name, "系统访问根地址");
        object.put(TableSystemConfig.type, 0);
        object.put(TableSystemConfig.isSystem, 2);
        sessionTemplate.saveAndUpdate(object);
    }

    @Override
    public String getWebUrl() {
        if (StringUtils.isBlank(webUrl)) {
            ModelObject url = sessionTemplate.get(Criteria.query(TableSystemConfig.class)
                    .eq(TableSystemConfig.key, "__web_root_url"));

            if (url != null) {
                webUrl = url.getString(TableSystemConfig.value);
            }
        }
        return webUrl;
    }
}
