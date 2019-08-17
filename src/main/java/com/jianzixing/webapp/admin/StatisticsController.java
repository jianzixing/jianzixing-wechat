package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.handler.AuthSkipCheck;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.statistics.TableStatisticsDay;
import com.jianzixing.webapp.tables.statistics.TableStatisticsHour;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@APIController
public class StatisticsController {

    @Printer(name = "查看统计信息列表")
    public ResponsePageMessage getStatistics(ModelObject search, int start, int limit) {
        Paging paging = GlobalService.statisticsService.getStatistics(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer(name = "首页七天统计数据")
    @AuthSkipCheck
    public ResponseMessage getSevenDay() {
        List<ModelObject> objects = GlobalService.statisticsService.getTodaySevenDayStatistics("*");
        if (objects != null) {
            for (ModelObject o : objects) {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                try {
                    o.put("time", format.parse(o.getString(TableStatisticsDay.dayTime)).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ResponseMessage(objects);
    }

    @Printer(name = "首页当天24小时统计数据")
    @AuthSkipCheck
    public ResponseMessage getTodayDayHours() {
        List<ModelObject> objects = GlobalService.statisticsService.getHoursStatistics(24, "*");
        if (objects != null) {
            for (ModelObject o : objects) {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
                try {
                    o.put("time", format.parse(o.getString(TableStatisticsHour.hourTime)).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ResponseMessage(objects);
    }
}
