package com.jianzixing.webapp.service.wxplugin;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.user.TableUser;
import com.jianzixing.webapp.tables.wxplugin.*;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.DateUtils;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.orm.transaction.Transaction;
import org.mimosaframework.orm.transaction.TransactionCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DefaultWXPluginSignService implements WXPluginSignService {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addGroup(ModelObject object) throws ModelCheckerException {
        if (object != null) {
            object.setObjectClass(TableWxpluginSignGroup.class);
            object.checkAndThrowable();
            object.put(TableWxpluginSignGroup.createTime, new Date());
            sessionTemplate.save(object);
        }
    }

    @Override
    public void delGroup(int id) {
        sessionTemplate.delete(Criteria.delete(TableWxpluginSignAward.class).eq(TableWxpluginSignAward.gid, id));
        sessionTemplate.delete(Criteria.delete(TableWxpluginSignRecord.class).eq(TableWxpluginSignRecord.gid, id));
        sessionTemplate.delete(Criteria.delete(TableWxpluginSignGroup.class).eq(TableWxpluginSignGroup.id, id));
    }

    @Override
    public void updateGroup(ModelObject object) throws ModelCheckerException {
        if (object != null) {
            object.remove(TableWxpluginSignGroup.code);
            object.remove(TableWxpluginSignGroup.openType);
            object.remove(TableWxpluginSignGroup.accountId);
            object.remove(TableWxpluginSignGroup.createTime);
            object.setObjectClass(TableWxpluginSignGroup.class);
            object.checkUpdateThrowable();
            sessionTemplate.update(object);
        }
    }

    @Override
    public List<ModelObject> getGroups(String keyword) {
        Query query = Criteria.query(TableWxpluginSignGroup.class);
        query.order(TableWxpluginSignGroup.id, false);
        if (StringUtils.isNotBlank(keyword)) {
            query.like(TableWxpluginSignGroup.name, "%" + keyword + "%");
        }
        return sessionTemplate.list(query);
    }

    @Override
    public void addAward(ModelObject object) throws ModelCheckerException, ModuleException {
        if (object != null) {
            object.setObjectClass(TableWxpluginSignAward.class);
            object.put(TableWxpluginSignAward.createTime, new Date());
            object.checkAndThrowable();

            int gid = object.getIntValue(TableWxpluginSignAward.gid);
            int type = object.getIntValue(TableWxpluginSignAward.type);
            int everyday = object.getIntValue(TableWxpluginSignAward.everyday);
            if (type == 0) {
                // 不允许实物每天赠送
                object.put(TableWxpluginSignAward.everyday, 0);
            }
            if (type == 1 && everyday == 1) {
                ModelObject v = sessionTemplate.get(Criteria.query(TableWxpluginSignAward.class).
                        eq(TableWxpluginSignAward.gid, gid)
                        .eq(TableWxpluginSignAward.type, type)
                        .eq(TableWxpluginSignAward.everyday, everyday));
                if (v != null) {
                    throw new ModuleException(StockCode.FAILURE, "一个签到分组内只允许有一个每日赠送虚拟积分奖励项");
                }
            }

            sessionTemplate.save(object);
        }
    }

    @Override
    public void delAward(int id) {
        sessionTemplate.delete(TableWxpluginSignAward.class, id);
    }

    @Override
    public void updateAward(ModelObject object) throws ModelCheckerException {
        if (object != null) {
            object.remove(TableWxpluginSignAward.gid);
            object.remove(TableWxpluginSignAward.type);
            object.remove(TableWxpluginSignAward.everyday);
            object.remove(TableWxpluginSignAward.createTime);
            object.setObjectClass(TableWxpluginSignAward.class);
            object.checkUpdateThrowable();
            sessionTemplate.update(object);
        }
    }

    @Override
    public Paging getAwards(ModelObject search, int start, int limit) {
        Query query = Criteria.query(TableWxpluginSignAward.class);
        query.limit(start, limit);
        query.order(TableWxpluginSignAward.id, false);
        query.subjoin(TableWxpluginSignGroup.class).eq(TableWxpluginSignGroup.id, TableWxpluginSignAward.gid).single();
        if (search != null) {
            if (search.isNotEmpty("gid")) {
                query.eq(TableWxpluginSignAward.gid, search.getString("gid"));
            }
        }
        return sessionTemplate.paging(query);
    }

    @Override
    public Paging getRecords(ModelObject search, int start, int limit) {
        Query query = Criteria.query(TableWxpluginSignRecord.class);
        query.subjoin(TableWxpluginSignGroup.class).eq(TableWxpluginSignGroup.id, TableWxpluginSignRecord.gid).single();
        query.subjoin(TableUser.class).eq(TableUser.id, TableWxpluginSignRecord.userId).single();
        query.limit(start, limit);
        query.order(TableWxpluginSignRecord.createTime, false);
        if (search != null) {
            if (search.isNotEmpty("gid")) {
                query.eq(TableWxpluginSignRecord.gid, search.getString("gid"));
            }
        }
        return sessionTemplate.paging(query);
    }

    @Override
    public void sign(String groupCode, long uid) throws TransactionException, ModuleException {
        ModelObject group = sessionTemplate.get(Criteria.query(TableWxpluginSignGroup.class).eq(TableWxpluginSignGroup.code, groupCode));
        if (group != null) {
            Date startTime = group.getDate(TableWxpluginSignGroup.startTime);
            Date finishTime = group.getDate(TableWxpluginSignGroup.finishTime);
            if (startTime != null && startTime.getTime() > System.currentTimeMillis()) {
                throw new ModuleException(new StockCode(-110), "签到时间还没到");
            }
            if (finishTime != null && finishTime.getTime() < System.currentTimeMillis()) {
                throw new ModuleException(new StockCode(-120), "签到时间已过期");
            }

            int gid = group.getIntValue(TableWxpluginSignGroup.id);
            final ModelObject record = sessionTemplate.get(Criteria.query(TableWxpluginSignRecord.class)
                    .eq(TableWxpluginSignRecord.userId, uid)
                    .eq(TableWxpluginSignRecord.gid, gid));
            final List<ModelObject> awards = sessionTemplate.list(Criteria.query(TableWxpluginSignAward.class)
                    .eq(TableWxpluginSignAward.gid, gid));

            sessionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean invoke(Transaction transaction) throws Exception {
                    boolean runned = false;
                    if (record == null) {
                        ModelObject object = new ModelObject(TableWxpluginSignRecord.class);
                        object.put(TableWxpluginSignRecord.userId, uid);
                        object.put(TableWxpluginSignRecord.gid, gid);
                        object.put(TableWxpluginSignRecord.count, 1);
                        object.put(TableWxpluginSignRecord.cntCount, 1);
                        object.put(TableWxpluginSignRecord.lastTime, format.format(new Date()));
                        object.put(TableWxpluginSignRecord.createTime, new Date());
                        sessionTemplate.save(object);
                        runned = true;
                    } else {
                        String todayTime = format.format(new Date());
                        String yesterdayTime = format.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000l));
                        String lastTime = record.getString(TableWxpluginSignRecord.lastTime);
                        if (!todayTime.equals(lastTime)) {
                            long succ = 0;
                            if (yesterdayTime.equals(lastTime)) {
                                succ = sessionTemplate.update(
                                        Criteria.update(TableWxpluginSignRecord.class)
                                                .addSelf(TableWxpluginSignRecord.count, 1)
                                                .addSelf(TableWxpluginSignRecord.cntCount, 1)
                                                .value(TableWxpluginSignRecord.lastTime, format.format(new Date()))
                                                .eq(TableWxpluginSignRecord.lastTime, yesterdayTime)
                                                .eq(TableWxpluginSignRecord.userId, uid)
                                                .eq(TableWxpluginSignRecord.gid, gid)
                                );
                            } else {
                                succ = sessionTemplate.update(
                                        Criteria.update(TableWxpluginSignRecord.class)
                                                .addSelf(TableWxpluginSignRecord.count, 1)
                                                .value(TableWxpluginSignRecord.cntCount, 1)
                                                .value(TableWxpluginSignRecord.lastTime, format.format(new Date()))
                                                .eq(TableWxpluginSignRecord.userId, uid)
                                                .eq(TableWxpluginSignRecord.gid, gid)
                                );
                            }
                            if (succ > 0) runned = true;
                        }
                    }

                    if (runned) {
                        ModelObject record = sessionTemplate.get(Criteria.query(TableWxpluginSignRecord.class)
                                .eq(TableWxpluginSignRecord.userId, uid)
                                .eq(TableWxpluginSignRecord.gid, gid));

                        int count = record.getIntValue(TableWxpluginSignRecord.count);
                        int cntCount = record.getIntValue(TableWxpluginSignRecord.cntCount);
                        if (awards != null) {
                            List<ModelObject> alreadyGot = sessionTemplate.list(Criteria.query(TableWxpluginSignGet.class)
                                    .eq(TableWxpluginSignGet.gid, gid).eq(TableWxpluginSignGet.userId, uid));

                            for (ModelObject award : awards) {
                                // 判断是否是积累类奖励
                                int awardType = award.getIntValue(TableWxpluginSignAward.type);
                                // 判断是否是每天赠送
                                int awardEveryday = award.getIntValue(TableWxpluginSignAward.everyday);
                                int awardId = award.getIntValue(TableWxpluginSignAward.id);
                                int awardCount = award.getIntValue(TableWxpluginSignAward.count);
                                boolean shouldBeGet = false;

                                if (awardType == 0) {
                                    if (awardCount <= cntCount) {
                                        shouldBeGet = true;
                                        if (shouldBeGet && alreadyGot != null) {
                                            for (ModelObject already : alreadyGot) {
                                                int said = already.getIntValue(TableWxpluginSignGet.said);
                                                if (said == awardId) {
                                                    shouldBeGet = false;
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if (awardEveryday == 1) {
                                        shouldBeGet = true;
                                    } else {
                                        // 判断连续签到次数
                                        if (awardCount < count) {
                                            shouldBeGet = true;
                                        }
                                        if (shouldBeGet && alreadyGot != null) {
                                            for (ModelObject already : alreadyGot) {
                                                int said = already.getIntValue(TableWxpluginSignGet.said);
                                                if (said == awardId) {
                                                    shouldBeGet = false;
                                                }
                                            }
                                        }
                                    }
                                }

                                if (shouldBeGet) {
                                    boolean needSave = true;
                                    int needSaveType = 0;
                                    int needSaveTotalAmount = 0;
                                    if (awardType == 1) {
                                        ModelObject accumulate = sessionTemplate.get(Criteria.query(TableWxpluginSignGet.class)
                                                .eq(TableWxpluginSignGet.gid, gid)
                                                .eq(TableWxpluginSignGet.userId, uid)
                                                .eq(TableWxpluginSignGet.type, 1)
                                                .eq(TableWxpluginSignGet.isUsed, 0));
                                        if (accumulate == null) {
                                            needSaveType = 1;
                                            needSaveTotalAmount = count;
                                        } else {
                                            needSave = false;
                                            sessionTemplate.update(
                                                    Criteria.update(TableWxpluginSignGet.class)
                                                            .addSelf(TableWxpluginSignGet.totalAmount, count)
                                                            .eq(TableWxpluginSignGet.id, accumulate.getIntValue(TableWxpluginSignGet.id))
                                            );
                                        }
                                    }

                                    if (needSave) {
                                        ModelObject awardGetObj = new ModelObject(TableWxpluginSignGet.class);
                                        awardGetObj.put(TableWxpluginSignGet.gid, gid);
                                        awardGetObj.put(TableWxpluginSignGet.type, needSaveType);
                                        awardGetObj.put(TableWxpluginSignGet.userId, uid);
                                        awardGetObj.put(TableWxpluginSignGet.count, count);
                                        awardGetObj.put(TableWxpluginSignGet.cntCount, cntCount);
                                        awardGetObj.put(TableWxpluginSignGet.said, awardId);
                                        awardGetObj.put(TableWxpluginSignGet.totalAmount, needSaveTotalAmount);
                                        awardGetObj.put(TableWxpluginSignGet.createTime, new Date());
                                        sessionTemplate.save(awardGetObj);
                                    }

                                }
                            }
                        }

                        ModelObject log = new ModelObject(TableWxpluginSignLog.class);
                        log.put(TableWxpluginSignLog.gid, gid);
                        log.put(TableWxpluginSignLog.userId, uid);
                        log.put(TableWxpluginSignLog.count, count);
                        log.put(TableWxpluginSignLog.cntCount, cntCount);
                        log.put(TableWxpluginSignLog.createTime, new Date());
                        sessionTemplate.save(log);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public Paging getLogs(int gid, long uid, int start, int limit) {
        Query query = Criteria.query(TableWxpluginSignLog.class);
        query.subjoin(TableWxpluginSignGroup.class).eq(TableWxpluginSignGroup.id, TableWxpluginSignLog.gid).single();
        query.subjoin(TableUser.class).eq(TableUser.id, TableWxpluginSignLog.userId).single();
        query.limit(start, limit);
        query.order(TableWxpluginSignLog.id, false);
        query.eq(TableWxpluginSignLog.gid, gid);
        query.eq(TableWxpluginSignLog.userId, uid);
        return sessionTemplate.paging(query);
    }

    @Override
    public Paging getGets(ModelObject search, int start, int limit) {
        Query query = Criteria.query(TableWxpluginSignGet.class);
        query.subjoin(TableWxpluginSignGroup.class).eq(TableWxpluginSignGroup.id, TableWxpluginSignGet.gid).single();
        query.subjoin(TableWxpluginSignAward.class).eq(TableWxpluginSignAward.id, TableWxpluginSignGet.said).single();
        query.subjoin(TableUser.class).eq(TableUser.id, TableWxpluginSignGet.userId).single();
        query.limit(start, limit);
        query.order(TableWxpluginSignGet.id, false);
        return sessionTemplate.paging(query);
    }

    @Override
    public void setGetUsed(int id, int useCount) throws ModuleException {
        ModelObject get = sessionTemplate.get(
                Criteria.query(TableWxpluginSignGet.class)
                        .eq(TableWxpluginSignGet.id, id)
                        .eq(TableWxpluginSignGet.isUsed, 0));

        if (get != null) {
            int isUsed = get.getIntValue(TableWxpluginSignGet.isUsed);
            if (isUsed == 0) {
                int type = get.getIntValue(TableWxpluginSignGet.type);
                int totalAmount = get.getIntValue(TableWxpluginSignGet.totalAmount);
                if (type == 1) {
                    if (useCount > 0) {
                        if (totalAmount < useCount) {
                            throw new ModuleException(StockCode.FAILURE, "累计奖励不够扣减");
                        }
                        int lastAmount = totalAmount - useCount;
                        if (lastAmount > 0) {
                            get.put(TableWxpluginSignGet.totalAmount, lastAmount);
                            get.remove(TableWxpluginSignGet.id);
                            get.put(TableWxpluginSignGet.isUsed, 0);
                            sessionTemplate.save(get);
                        }
                        ModelObject object = new ModelObject(TableWxpluginSignGet.class);
                        object.put(TableWxpluginSignGet.id, id);
                        object.put(TableWxpluginSignGet.isUsed, 1);
                        object.put(TableWxpluginSignGet.totalAmount, useCount);
                        sessionTemplate.update(object);
                    } else {
                        throw new ModuleException(StockCode.FAILURE, "扣减累计奖励不能为0");
                    }
                } else {
                    ModelObject object = new ModelObject(TableWxpluginSignGet.class);
                    object.put(TableWxpluginSignGet.id, id);
                    object.put(TableWxpluginSignGet.isUsed, 1);
                    sessionTemplate.update(object);
                }
            }
        }
    }

    @Override
    public ModelObject getFrontSignModel(ModelObject group, long uid) {
        int gid = group.getIntValue(TableWxpluginSignGroup.id);
        ModelObject userRecord = sessionTemplate.get(
                Criteria.query(TableWxpluginSignRecord.class)
                        .eq(TableWxpluginSignRecord.userId, uid)
                        .eq(TableWxpluginSignRecord.gid, gid)
        );

        group.put("record", userRecord);

        List<ModelObject> logs = sessionTemplate.list(
                Criteria.query(TableWxpluginSignLog.class)
                        .eq(TableWxpluginSignLog.gid, gid)
                        .eq(TableWxpluginSignLog.userId, uid)
        );

        List<ModelObject> gets = sessionTemplate.list(
                Criteria.query(TableWxpluginSignGet.class)
                        .eq(TableWxpluginSignGet.gid, gid)
                        .eq(TableWxpluginSignGet.userId, uid)
        );

        List<String> signDays = new ArrayList<>();
        if (logs != null) {
            for (ModelObject log : logs) {
                Date createTime = log.getDate(TableWxpluginSignLog.createTime);
                signDays.add(format.format(createTime));
            }
        }

        Date startTime = group.getDate(TableWxpluginSignGroup.startTime);
        Date finishTime = group.getDate(TableWxpluginSignGroup.finishTime);

        String todayStr = format.format(new Date());
        List<Date> c = DateUtils.getDayList(startTime, finishTime);
        Map<String, ModelObject> days = new LinkedHashMap();
        if (c != null) {
            for (int i = 1; i <= c.size(); i++) {
                ModelObject object = new ModelObject();
                object.put("day", i);
                String dayStr = format.format(c.get(i - 1));
                object.put("dayStr", dayStr);
                object.put("dayTime", c.get(i - 1));
                if (signDays.contains(dayStr)) {
                    object.put("signed", true);
                }
                days.put(dayStr, object);
            }
        }

        String lastTime = userRecord.getString(TableWxpluginSignRecord.lastTime);
        Date lastTimeDate = null;
        try {
            if (StringUtils.isNotBlank(lastTime)) {
                lastTimeDate = format.parse(lastTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (lastTimeDate == null) lastTimeDate = new Date();
        int cntCount = userRecord == null ? 0 : userRecord.getIntValue(TableWxpluginSignRecord.cntCount);
        List<ModelObject> awards = group.getArray(TableWxpluginSignAward.class.getSimpleName());
        if (awards != null) {
            for (ModelObject award : awards) {
                int type = award.getIntValue(TableWxpluginSignAward.type);
                int everyday = award.getIntValue(TableWxpluginSignAward.everyday);
                int count = award.getIntValue(TableWxpluginSignAward.count);
                if (type == 0 && everyday == 0 && count > cntCount) {
                    Date afterDate = DateUtils.getAfterDate(lastTimeDate, count - cntCount);
                    String dayStr = format.format(afterDate);
                    ModelObject obj = days.get(dayStr);
                    if (obj != null) {
                        obj.put("award", award);
                        obj.put("hasRealAward", true);
                    }
                }
                if (everyday == 1) {
                    Iterator<Map.Entry<String, ModelObject>> iterator = days.entrySet().iterator();
                    while (iterator.hasNext()) {
                        ModelObject o = iterator.next().getValue();
                        o.put("award", award);
                    }
                }
            }
        }

        if (gets != null) {
            for (ModelObject get : gets) {
                int type = get.getIntValue(TableWxpluginSignGet.type);
                if (type == 0) {
                    int said = get.getIntValue(TableWxpluginSignGet.said);
                    ModelObject getAward = null;
                    if (awards != null) {
                        for (ModelObject award : awards) {
                            int id = award.getIntValue(TableWxpluginSignAward.id);
                            if (id == said) {
                                getAward = award;
                            }
                        }
                    }
                    Date createTime = get.getDate(TableWxpluginSignGet.createTime);
                    String dateStr = format.format(createTime);
                    ModelObject obj = days.get(dateStr);
                    if (obj != null) {
                        obj.put("wasGetAward", true);
                        obj.put("getAward", getAward);
                    }
                }
            }
        }

        List<ModelObject> dayList = new ArrayList<>();
        Iterator<Map.Entry<String, ModelObject>> iterator = days.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ModelObject> entry = iterator.next();
            String dateStr = entry.getKey();
            ModelObject o = entry.getValue();
            dayList.add(o);
            Date dayTime = o.getDate("dayTime");
            if (group.get("nextAward") == null && o.get("award") != null
                    && (dayTime.getTime() > System.currentTimeMillis() || todayStr.equals(dateStr))) {
                int nextDayCount = DateUtils.getDayCount(new Date(), dayTime);
                ModelObject award = o.getModelObject("award");
                ModelObject nextAward = new ModelObject();
                nextAward.put("name", award.getString(TableWxpluginSignAward.name));
                nextAward.put("count", award.getString(TableWxpluginSignAward.count));
                nextAward.put("dayCount", nextDayCount);
                nextAward.put("type", award.getIntValue(TableWxpluginSignAward.type));
                group.put("nextAward", nextAward);
            }
        }
        group.put("days", dayList);
        return group;
    }

    @Override
    public ModelObject getGroupByCode(String groupCode) {
        ModelObject group = sessionTemplate.get(
                Criteria.query(TableWxpluginSignGroup.class)
                        .subjoin(TableWxpluginSignAward.class).eq(TableWxpluginSignAward.gid, TableWxpluginSignGroup.id).query()
                        .eq(TableWxpluginSignGroup.code, groupCode)
                        .eq(TableWxpluginSignGroup.enable, 1)
        );
        return group;
    }

    @Override
    public int isGroupTimeout(ModelObject group) {
        Date startTime = group.getDate(TableWxpluginSignGroup.startTime);
        Date finishTime = group.getDate(TableWxpluginSignGroup.finishTime);
        if (startTime != null && startTime.getTime() > System.currentTimeMillis()) {
            return 1;
        }
        if (finishTime != null && finishTime.getTime() < System.currentTimeMillis()) {
            return 2;
        }
        return 0;
    }

    @Override
    public void enableGroup(int id) {
        ModelObject object = new ModelObject(TableWxpluginSignGroup.class);
        object.put(TableWxpluginSignGroup.id, id);
        object.put(TableWxpluginSignGroup.enable, 1);
        sessionTemplate.update(object);
    }

    @Override
    public void disableGroup(int id) {
        ModelObject object = new ModelObject(TableWxpluginSignGroup.class);
        object.put(TableWxpluginSignGroup.id, id);
        object.put(TableWxpluginSignGroup.enable, 0);
        sessionTemplate.update(object);
    }

    @Override
    public List<ModelObject> getUserGets(int gid, long uid) {
        List<ModelObject> awards = sessionTemplate.list(
                Criteria.query(TableWxpluginSignGet.class)
                        .subjoin(TableWxpluginSignAward.class).eq(TableWxpluginSignAward.id, TableWxpluginSignGet.said).single().query()
                        .eq(TableWxpluginSignGet.gid, gid)
                        .eq(TableWxpluginSignGet.userId, uid)
        );
        return awards;
    }

    @Override
    public Map<String, List<ModelObject>> getUserStatusGets(int gid, long uid) {
        List<ModelObject> awards = sessionTemplate.list(
                Criteria.query(TableWxpluginSignGet.class)
                        .subjoin(TableWxpluginSignAward.class)
                        .eq(TableWxpluginSignAward.id, TableWxpluginSignGet.said)
                        .aliasName("award")
                        .single().query()
                        .eq(TableWxpluginSignGet.gid, gid)
                        .eq(TableWxpluginSignGet.userId, uid)
        );
        Map<String, List<ModelObject>> map = new HashMap<>();
        if (awards != null) {
            List<ModelObject> notUse = new ArrayList<>();
            List<ModelObject> used = new ArrayList<>();
            for (ModelObject award : awards) {
                int isUsed = award.getIntValue(TableWxpluginSignGet.isUsed);
                if (isUsed == 1) {
                    used.add(award);
                } else {
                    notUse.add(award);
                }
            }
            map.put("nu", notUse);
            map.put("ud", used);
        }
        return map;
    }
}
