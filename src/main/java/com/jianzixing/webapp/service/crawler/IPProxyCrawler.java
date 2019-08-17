package com.jianzixing.webapp.service.crawler;

import org.mimosaframework.core.json.ModelObject;

import java.util.List;

/**
 * @author yangankang
 */
public interface IPProxyCrawler extends Crawler {
    boolean isExist(String ip, int port);

    boolean checkProxyValid(String protocol, String ip, int port);

    void clearExpireIps();

    List<ModelObject> getIps(int start, int limit);
}
