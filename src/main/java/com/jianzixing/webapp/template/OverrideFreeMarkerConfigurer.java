package com.jianzixing.webapp.template;

import com.jianzixing.webapp.service.GlobalService;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.util.Map;

public class OverrideFreeMarkerConfigurer extends FreeMarkerConfigurer implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void afterPropertiesSet() throws IOException, TemplateException {
        super.afterPropertiesSet();
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(FreemarkerComponent.class);
        Configuration configuration = this.getConfiguration();
        for (String key : map.keySet()) {
            configuration.setSharedVariable(key, map.get(key));
        }

        BeansWrapper wrapper = new BeansWrapperBuilder(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).build();
        TemplateHashModel staticModels = wrapper.getStaticModels();
        TemplateHashModel fileStatics = (TemplateHashModel) staticModels.get(GlobalService.class.getName());
        configuration.setSharedVariable("GlobalService", fileStatics);
    }
}
