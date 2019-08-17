package com.jianzixing.webapp.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author yangankang
 */
@Service
public class SystemConfig implements InitializingBean {
    public static List<String> compressJsPaths;
    public static String webPath;

    public static boolean isDebug = false;
    public static boolean isSkipArea = false;

    @Value("${system.isDebug}")
    public void setIsDebug(boolean isDebug) {
        SystemConfig.isDebug = isDebug;
    }

    @Value("${system.isSkipArea}")
    public void setIsSkipArea(boolean isSkipArea) {
        SystemConfig.isSkipArea = isSkipArea;
    }

    public void setCompressJsPaths(String[] compressJsPaths) {
        if (compressJsPaths != null) {
            SystemConfig.compressJsPaths = Arrays.asList(compressJsPaths);
        }
    }

    public void setWebPath(String webPath) {
        SystemConfig.webPath = webPath;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            if (SystemConfig.compressJsPaths != null) {
                GlobalService.javaScriptService.compressWebJs(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
