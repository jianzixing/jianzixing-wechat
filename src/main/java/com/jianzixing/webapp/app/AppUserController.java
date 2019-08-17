package com.jianzixing.webapp.app;

import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

/**
 * @author yangankang
 */
@APIController
public class AppUserController {

    @Printer
    public ModelObject login(String wrapper) {

        return ModelObject.parseObject("{}");
    }
}
