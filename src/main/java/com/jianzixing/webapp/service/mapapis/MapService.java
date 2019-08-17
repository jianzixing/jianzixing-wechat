package com.jianzixing.webapp.service.mapapis;

import java.io.IOException;

public interface MapService {

    /**
     * 通过ip获取ip所在位置
     *
     * @param ip
     * @return
     */
    String getIpAddress(String ip) throws IOException;

    /**
     * 世界行政区域在网站 http://www.xzqh.org/old/waiguo/index.htm 可爬到
     * <p>
     * 更新中国行政区域
     * 可以更新国家、省市县、街道等信息
     * 主要用于地址库，只能更新中国地区
     */
    void updateAdministrativeRegion();
}
