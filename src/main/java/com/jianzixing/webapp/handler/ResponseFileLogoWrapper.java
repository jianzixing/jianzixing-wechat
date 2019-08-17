package com.jianzixing.webapp.handler;

import org.mimosaframework.core.json.ModelObject;

/**
 * @author yangankang
 */
public class ResponseFileLogoWrapper {
    private ModelObject value;

    public void setValue(ModelObject value) {
        this.value = value;
    }

    public ModelObject getValue() {
        return value;
    }
}
