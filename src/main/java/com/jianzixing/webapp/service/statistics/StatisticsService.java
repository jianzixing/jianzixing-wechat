package com.jianzixing.webapp.service.statistics;

import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.util.List;

public interface StatisticsService {
    void statistics();

    Paging getStatistics(ModelObject search, int start, int limit);

    void addHitStatistics(int type, long id);

    ModelObject getTodayStatistics(String uri);

    List<ModelObject> getTodayHoursStatistics(String uri);

    List<ModelObject> getHoursStatistics(int hour, String uri);

    List<ModelObject> getTodaySevenDayStatistics(String uri);
}
