package com.jianzixing.webapp.install;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.SystemConfig;
import com.jianzixing.webapp.service.system.SystemLevel;
import com.jianzixing.webapp.tables.system.TableModule;
import com.jianzixing.webapp.tables.system.TableModuleAuth;
import com.jianzixing.webapp.tables.system.TableSystemConfig;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class AfterSpringBeanInstall implements InitializingBean, ApplicationContextAware {
    private static final Log logger = LogFactory.getLog(AfterSpringBeanInstall.class);
    public static final String INSTALL_FLAG = "isInstall";
    private SessionTemplate sessionTemplate;

    public void setSessionTemplate(SessionTemplate sessionTemplate) {
        this.sessionTemplate = sessionTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            ModelObject isInstall = GlobalService.systemService.getSystemConfig(INSTALL_FLAG);
            if (isInstall == null || isInstall.getIntValue(TableSystemConfig.value) == 0
                    || SystemConfig.isDebug) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                Enumeration<URL> enumeration = cl.getResources("/install/");
                if (enumeration != null) {
                    while (enumeration.hasMoreElements()) {
                        URL url = enumeration.nextElement();
                        File file = new File(url.getFile());
                        File[] files = file.listFiles();
                        if (files != null && files.length > 0) {
                            for (int i = 0; i < files.length; i++) {
                                File cf = files[i];
                                String name = files[i].getName();
                                if (name.endsWith(".json") || cf.isDirectory()) {
                                    if (SystemConfig.isSkipArea) {
                                        if (
                                                name.indexOf("install_province") >= 0
                                                        || name.indexOf("install_city") >= 0
                                                        || name.indexOf("install_area") >= 0
                                                        || name.indexOf("install_goods_groups") >= 0
                                        ) {
                                            continue;
                                        }
                                    }

                                    this.saves(cf, file.getPath());
                                    if (name.indexOf("install_goods_groups") >= 0) {
                                        GlobalService.goodsGroupService.checkGroupNeedUpdate();
                                    }
                                }
                            }
                        }
                    }
                }

                GlobalService.systemConfigService.setSystemConfig("是否已安装", INSTALL_FLAG, 1, SystemLevel.HIDDEN.getLevel(), 0);

                logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                logger.info("系统初始化完成,您可以访问后台进行配置");
                logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("初始化系统失败", e);
        }
    }

    private void saves(File file, String basePath) throws IOException, ClassNotFoundException {
        String name = file.getPath().substring(basePath.length() + 1);
        if (file.isDirectory()) {
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 开始遍历文件夹 " + name);
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    this.saves(f, basePath);
                }
            }
        } else {
            if (name.endsWith(".json")) {
                logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 开始安装数据 " + name);
                InputStream inputStream = this.getClass().getResourceAsStream("/install/" + name);
                try {
                    if (inputStream != null) {
                        String cxt = this.readInputStreamToString(inputStream);
                        inputStream.close();
                        ModelObject object = ModelObject.parseObject(cxt);
                        if (object != null) {
                            String clazz = object.getString("class");
                            if (StringUtils.isBlank(clazz)) {
                                throw new IllegalArgumentException("文件" + name + ".json需要配置class信息");
                            }

                            /**
                             * 获取保留ID段并保存到数据中
                             */
                            long initPrimaryKey = object.getLongValue("initPrimaryKey");
                            String primaryKey = object.getString("primaryKey");

                            Class<?> c = Class.forName(clazz);
                            ModelArray items = object.getModelArray("items");
                            if (items != null && items.size() > 0) {
                                for (int j = 0; j < items.size(); j++) {
                                    ModelObject db = items.getModelObject(j);
                                    db.setObjectClass(c);
                                    this.sessionTemplate.saveAndUpdate(db);

                                    // 如果有附带的权限数据一块保存了
                                    this.saveModuleAuths(c, db);
                                }
                            }

                            if (object.containsKey("initPrimaryKey")
                                    && initPrimaryKey > 0
                                    && items != null
                                    && items.size() > 0
                                    && StringUtils.isNotBlank(primaryKey)) {
                                ModelObject first = items.getModelObject(0);
                                first.put(primaryKey, initPrimaryKey);
                                this.sessionTemplate.save(first);
                                this.sessionTemplate.delete(c, initPrimaryKey);
                            }
                        } else {
                            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 开始安装数据 " + name + " 文件是空文件!");
                        }
                    }
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        }
    }

    private void saveModuleAuths(Class c, ModelObject db) {
        if (c == TableModule.class) {
            String page = db.getString(TableModule.module);
            int isTop = db.getIntValue(TableModule.top);
            ModelObject auths = db.getModelObject("authorize");
            if (auths != null && StringUtils.isNotBlank(page) && isTop != 1) {
                this.sessionTemplate.delete(Criteria.delete(TableModuleAuth.class).eq(TableModuleAuth.page, page));
                Set<Object> keys = auths.keySet();
                if (keys != null && keys.size() > 0) {
                    for (Object k : keys) {
                        ModelArray values = auths.getModelArray(k);
                        if (values != null && values.size() > 0) {
                            for (int vlen = 0; vlen < values.size(); vlen++) {
                                String v = values.getString(vlen);
                                ModelObject apis = new ModelObject(TableModuleAuth.class);
                                apis.put(TableModuleAuth.clazz, String.valueOf(k).trim());
                                apis.put(TableModuleAuth.method, String.valueOf(v).trim());
                                apis.put(TableModuleAuth.page, page.trim());
                                this.sessionTemplate.save(apis);
                            }
                        }
                    }
                }
            }
        }
    }

    private String readInputStreamToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            List<String> lines = IOUtils.readLines(inputStream, "UTF-8");
            StringBuilder sb = new StringBuilder();
            for (String s : lines) {
                sb.append(s);
            }
            return sb.toString();
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
