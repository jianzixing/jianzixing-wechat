package com.jianzixing.webapp.service.system;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.util.List;
import java.util.Map;

public interface SystemConfigService {
    void addGroup(ModelObject object) throws ModelCheckerException;

    void delGroup(int id) throws ModuleException;

    void updateGroup(ModelObject object) throws ModelCheckerException;

    List<ModelObject> getGroups();

    void setSystemConfig(String name, String key, Object value, int isSystem, int pos);

    void setSystemConfig(String key, Object value);

    void addSystemConfig(ModelObject object) throws ModelCheckerException, ModuleException;

    void delSystemConfig(String key) throws ModuleException;

    void updateSystemConfig(ModelObject object) throws ModelCheckerException;

    Paging getSystemConfigs(Query query, int gid, int start, int limit);

    String getValue(String key);

    Map<String, String> getValues(String... keys);

    /**
     * 设置整个系统的根url，比如 http://localhost/
     *
     * @param url
     */
    void setWebUrl(String url);

    String getWebUrl();
}
