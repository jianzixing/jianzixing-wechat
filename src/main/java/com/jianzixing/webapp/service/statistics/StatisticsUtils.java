package com.jianzixing.webapp.service.statistics;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.log.RequestAddressUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StatisticsUtils {
    private static final Log logger = LogFactory.getLog(StatisticsUtils.class);

    public static void statisticsView(HttpServletRequest request, HttpServletResponse response) {
        try {
            ModelObject object = RequestAddressUtils.getRequestAddress(request, response);
            if (object != null) {
                GlobalService.requestAddressService.addAddress(object);
            }
        } catch (Exception e) {
            logger.error("统计访问信息出错", e);
        }
    }
}
