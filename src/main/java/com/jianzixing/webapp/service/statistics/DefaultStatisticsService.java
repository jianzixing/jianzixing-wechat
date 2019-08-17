package com.jianzixing.webapp.service.statistics;

import com.jianzixing.webapp.tables.log.TableRequestAddress;
import com.jianzixing.webapp.tables.statistics.TableHitStatistics;
import com.jianzixing.webapp.tables.statistics.TableStatisticsDay;
import com.jianzixing.webapp.tables.statistics.TableStatisticsHour;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.encryption.MD5Utils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.DateUtils;
import org.mimosaframework.orm.*;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class DefaultStatisticsService implements StatisticsService {
    private static final Log logger = LogFactory.getLog(DefaultStatisticsService.class);

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void statistics() {
        try {

            {
                Date start = DateUtils.getTodayZeroTime();
                Date end = DateUtils.getTodayEndTime();
                long pv = sessionTemplate.count(Criteria.query(TableRequestAddress.class).between(TableRequestAddress.createTime, start, end));
                long uv = this.cal(TableRequestAddress.sessionid.toString(), start, end);
                long iv = this.cal(TableRequestAddress.ip.toString(), start, end);

                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                String date = format.format(new Date());
                ModelObject object = new ModelObject(TableStatisticsDay.class);
                object.put(TableStatisticsDay.dayTime, date);
                object.put(TableStatisticsDay.md5, MD5Utils.md5("*"));
                object.put(TableStatisticsDay.uri, "*");
                object.put(TableStatisticsDay.pv, pv);
                object.put(TableStatisticsDay.uv, uv);
                object.put(TableStatisticsDay.iv, iv);

                sessionTemplate.saveAndUpdate(object);
            }

            {
                int minute = DateUtils.getNowMinute();

                if (minute <= 5) {
                    Date start = DateUtils.getTimeStartHour(System.currentTimeMillis() - 1 * 60 * 60 * 1000l);
                    Date end = DateUtils.getTimeEndHour(System.currentTimeMillis() - 1 * 60 * 60 * 1000l);
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
                    String date = format.format(end);
                    long pv = sessionTemplate.count(Criteria.query(TableRequestAddress.class).between(TableRequestAddress.createTime, start, end));
                    long uv = this.cal(TableRequestAddress.sessionid.toString(), start, end);
                    long iv = this.cal(TableRequestAddress.ip.toString(), start, end);

                    ModelObject object = new ModelObject(TableStatisticsHour.class);
                    object.put(TableStatisticsHour.hourTime, date);
                    object.put(TableStatisticsHour.md5, MD5Utils.md5("*"));
                    object.put(TableStatisticsHour.uri, "*");
                    object.put(TableStatisticsHour.pv, pv);
                    object.put(TableStatisticsHour.uv, uv);
                    object.put(TableStatisticsHour.iv, iv);

                    sessionTemplate.saveAndUpdate(object);
                } else {
                    Date start = DateUtils.getNowStartHour();
                    Date end = DateUtils.getNowEndHour();
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
                    String date = format.format(end);
                    long pv = sessionTemplate.count(Criteria.query(TableRequestAddress.class).between(TableRequestAddress.createTime, start, end));
                    long uv = this.cal(TableRequestAddress.sessionid.toString(), start, end);
                    long iv = this.cal(TableRequestAddress.ip.toString(), start, end);

                    ModelObject object = new ModelObject(TableStatisticsHour.class);
                    object.put(TableStatisticsHour.hourTime, date);
                    object.put(TableStatisticsHour.md5, MD5Utils.md5("*"));
                    object.put(TableStatisticsHour.uri, "*");
                    object.put(TableStatisticsHour.pv, pv);
                    object.put(TableStatisticsHour.uv, uv);
                    object.put(TableStatisticsHour.iv, iv);

                    sessionTemplate.saveAndUpdate(object);
                }
            }
        } catch (Exception e) {
            logger.error("统计访问数据出错", e);
        }
    }

    @Override
    public Paging getStatistics(ModelObject search, int start, int limit) {
        Query query = Criteria.query(TableStatisticsDay.class);
        query.order(TableStatisticsDay.dayTime, false);
        query.limit(start, limit);
        if (search != null) {
            if (search.isNotEmpty("time")) {
                query.eq(TableStatisticsDay.dayTime, search.getString("time"));
            }
        }
        return sessionTemplate.paging(query);
    }

    @Override
    public void addHitStatistics(int type, long id) {
        ModelObject object = new ModelObject(TableHitStatistics.class);
        object.put(TableHitStatistics.outType, type);
        object.put(TableHitStatistics.outId, id);
        object.put(TableHitStatistics.createTime, new Date());
        sessionTemplate.save(object);
    }

    @Override
    public ModelObject getTodayStatistics(String uri) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String date = format.format(new Date());
        return sessionTemplate.get(
                Criteria.query(TableStatisticsDay.class)
                        .eq(TableStatisticsDay.dayTime, date)
                        .eq(TableStatisticsDay.md5, MD5Utils.md5(uri))
        );
    }

    @Override
    public List<ModelObject> getTodayHoursStatistics(String uri) {
        Date start = DateUtils.getTodayZeroTime();
        Date end = DateUtils.getTodayEndTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
        String startTime = format.format(start);
        String endTime = format.format(end);
        return sessionTemplate.list(Criteria.query(TableStatisticsHour.class)
                .eq(TableStatisticsHour.md5, MD5Utils.md5(uri))
                .between(TableStatisticsHour.hourTime, startTime, endTime)
                .order(TableStatisticsHour.hourTime, false)
        );
    }

    @Override
    public List<ModelObject> getHoursStatistics(int hour, String uri) {
        Date start = new Date(System.currentTimeMillis() - (hour - 1) * 60 * 60 * 1000l);
        Date end = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
        String startTime = format.format(start);
        String endTime = format.format(end);
        return sessionTemplate.list(
                Criteria.query(TableStatisticsHour.class)
                        .eq(TableStatisticsHour.md5, MD5Utils.md5(uri))
                        .between(TableStatisticsHour.hourTime, startTime, endTime)
                        .order(TableStatisticsHour.hourTime, false)
        );
    }

    @Override
    public List<ModelObject> getTodaySevenDayStatistics(String uri) {
        Date start = DateUtils.getZeroTime((System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000l));
        Date end = DateUtils.getTodayEndTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String startTime = format.format(start);
        String endTime = format.format(end);
        return sessionTemplate.list(
                Criteria.query(TableStatisticsDay.class)
                        .eq(TableStatisticsHour.md5, MD5Utils.md5(uri))
                        .between(TableStatisticsDay.dayTime, startTime, endTime)
                        .order(TableStatisticsDay.dayTime, false)
        );
    }

    private long cal(String field, final Date start, final Date end) throws Exception {
        SQLAutonomously.newInstance();

//        final List<ModelObject> objects = sessionTemplate.getAutonomously(new Autonomously() {
//            @Override
//            public List<ModelObject> list(AutoPool autoPool) throws Exception {
//                AutoPoolTable table = autoPool.getTableDataSources(TableRequestAddress.class);
//                if (table != null) {
//                    List<AutoPoolItem> items = table.getItems();
//                    if (items != null) {
//                        for (AutoPoolItem item : items) {
//                            MimosaDataSource dataSource = item.getDataSource();
//                            Connection conn = null;
//                            try {
//                                conn = dataSource.getConnection(true, null, false);
//                                PreparedStatement statement = conn.prepareStatement(
//                                        "select count(t.c) as count from (select count(1) as c from " + item.getTableName() + " where create_time>=? and create_time<=?" + "  group by " + field + ") as t");
//                                statement.setObject(1, start);
//                                statement.setObject(2, end);
//                                ResultSet set = statement.executeQuery();
//                                List<ModelObject> r = new ArrayList<>();
//                                while (set.next()) {
//                                    ModelObject o = new ModelObject();
//                                    o.put("count", set.getObject("count"));
//                                    r.add(o);
//                                }
//                                return r;
//                            } finally {
//                                if (conn != null) {
//                                    try {
//                                        conn.close();
//                                    } catch (SQLException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                return null;
//            }
//        });

//        if (objects.size() == 1) {
//            return objects.get(0).getLongValue("count");
//        }
        return 0;
    }
}
