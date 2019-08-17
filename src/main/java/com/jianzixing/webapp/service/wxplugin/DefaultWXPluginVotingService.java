package com.jianzixing.webapp.service.wxplugin;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.wxplugin.TableWxpluginVotingGroup;
import com.jianzixing.webapp.tables.wxplugin.TableWxpluginVotingItem;
import com.jianzixing.webapp.tables.wxplugin.TableWxpluginVotingLog;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DefaultWXPluginVotingService implements WXPluginVotingService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addGroup(ModelObject group) throws ModelCheckerException {
        if (group != null) {
            group.remove(TableWxpluginVotingGroup.count);
            group.put(TableWxpluginVotingGroup.createTime, new Date());
            group.setObjectClass(TableWxpluginVotingGroup.class);
            group.checkAndThrowable();
            sessionTemplate.save(group);
        }
    }

    @Override
    public void delGroup(int id) {
        sessionTemplate.delete(TableWxpluginVotingGroup.class, id);
    }

    @Override
    public void updateGroup(ModelObject group) throws ModelCheckerException {
        if (group != null) {
            group.remove(TableWxpluginVotingGroup.createTime);
            group.setObjectClass(TableWxpluginVotingGroup.class);
            group.checkUpdateThrowable();
            sessionTemplate.update(group);
        }
    }

    @Override
    public Paging getGroups(ModelObject search, int start, int limit) {
        Query query = Criteria.query(TableWxpluginVotingGroup.class);
        query.limit(start, limit);
        query.order(TableWxpluginVotingGroup.id, false);
        if (search != null) {
            if (search.isNotEmpty("name")) {
                query.like(TableWxpluginVotingGroup.name, "%" + search.getString("name") + "%");
            }
        }
        return sessionTemplate.paging(query);
    }

    @Override
    public void addItem(ModelObject item) throws ModelCheckerException, ModuleException {
        if (item != null) {
            String gid = item.getString(TableWxpluginVotingItem.gid);
            if (StringUtils.isBlank(gid) || !NumberUtils.isNumber(gid)) {
                throw new ModuleException(StockCode.ARG_VALID, "必须有一个投票条目分组");
            }

            item.put(TableWxpluginVotingItem.createTime, new Date());
            item.setObjectClass(TableWxpluginVotingItem.class);
            item.checkAndThrowable();

            ModelObject group = sessionTemplate.get(TableWxpluginVotingGroup.class, gid);
            if (group == null) {
                throw new ModuleException(StockCode.ARG_VALID, "投票条目分组不存在");
            }

            sessionTemplate.save(item);
        }
    }

    @Override
    public void delItem(int id) {
        sessionTemplate.delete(TableWxpluginVotingItem.class, id);
    }

    @Override
    public void updateItem(ModelObject item) throws ModelCheckerException, ModuleException {
        if (item != null) {
            String gid = item.getString(TableWxpluginVotingItem.gid);
            if (StringUtils.isBlank(gid) || !NumberUtils.isNumber(gid)) {
                throw new ModuleException(StockCode.ARG_VALID, "必须有一个投票条目分组");
            }

            item.remove(TableWxpluginVotingItem.createTime);
            item.remove(TableWxpluginVotingItem.count);
            item.setObjectClass(TableWxpluginVotingItem.class);
            item.checkUpdateThrowable();

            ModelObject group = sessionTemplate.get(TableWxpluginVotingGroup.class, gid);
            if (group == null) {
                throw new ModuleException(StockCode.ARG_VALID, "投票条目分组不存在");
            }

            sessionTemplate.update(item);
        }
    }

    @Override
    public Paging getItems(ModelObject search, int start, int limit) {
        Query query = Criteria.query(TableWxpluginVotingItem.class);
        query.subjoin(TableWxpluginVotingGroup.class).eq(TableWxpluginVotingGroup.id, TableWxpluginVotingItem.gid).single();
        query.limit(start, limit);
        if (search != null) {
            if (search.isNotEmpty("gid")) {
                query.eq(TableWxpluginVotingItem.gid, search.getString("gid"));
                query.order(TableWxpluginVotingItem.count, false);
            }
            if (search.isNotEmpty("name")) {
                query.like(TableWxpluginVotingItem.name, "%" + search.getString("name") + "%");
            }
        } else {
            query.order(TableWxpluginVotingItem.id, false);
        }

        return sessionTemplate.paging(query);
    }

    @Override
    public int voting(int iid, long uid) {
        ModelObject item = sessionTemplate.get(Criteria.query(TableWxpluginVotingItem.class).eq(TableWxpluginVotingItem.id, iid));
        if (item != null) {
            int gid = item.getIntValue(TableWxpluginVotingItem.gid);
            ModelObject group = sessionTemplate.get(Criteria.query(TableWxpluginVotingGroup.class).eq(TableWxpluginVotingGroup.id, gid));
            if (group != null) {
                int count = group.getIntValue(TableWxpluginVotingGroup.count);
                int type = group.getIntValue(TableWxpluginVotingGroup.type);

                long votingCount = sessionTemplate.count(Criteria.query(TableWxpluginVotingLog.class)
                        .eq(TableWxpluginVotingLog.userId, uid)
                        .eq(TableWxpluginVotingLog.gid, gid));

                // 判断投票次数
                if (votingCount < count) {

                    if (type == 0) {
                        ModelObject log = sessionTemplate.get(
                                Criteria.query(TableWxpluginVotingLog.class)
                                        .eq(TableWxpluginVotingLog.gid, gid)
                                        .eq(TableWxpluginVotingLog.userId, uid)
                                        .eq(TableWxpluginVotingLog.iid, iid)
                        );
                        if (log != null) {
                            return -200;
                        }
                    }

                    long succ = sessionTemplate.update(
                            Criteria.update(TableWxpluginVotingItem.class)
                                    .addSelf(TableWxpluginVotingItem.count)
                                    .eq(TableWxpluginVotingItem.id, iid)
                    );
                    if (succ > 0) {
                        ModelObject object = new ModelObject(TableWxpluginVotingLog.class);
                        object.put(TableWxpluginVotingLog.iid, iid);
                        object.put(TableWxpluginVotingLog.userId, uid);
                        object.put(TableWxpluginVotingLog.gid, gid);
                        object.put(TableWxpluginVotingLog.createTime, new Date());
                        sessionTemplate.save(object);
                    }
                } else {
                    return -100;
                }
            }
        }
        return 0;
    }

    @Override
    public ModelObject getGroupByCode(String groupCode) {
        return sessionTemplate.get(
                Criteria.query(TableWxpluginVotingGroup.class)
                        .eq(TableWxpluginVotingGroup.code, groupCode)
                        .eq(TableWxpluginVotingGroup.enable, 1)
        );
    }

    @Override
    public int isGroupTimeout(ModelObject group) {
        Date startTime = group.getDate(TableWxpluginVotingGroup.startTime);
        Date finishTime = group.getDate(TableWxpluginVotingGroup.finishTime);
        if (startTime != null && startTime.getTime() > System.currentTimeMillis()) {
            return 1;
        }
        if (finishTime != null && finishTime.getTime() < System.currentTimeMillis()) {
            return 2;
        }
        return 0;
    }

    @Override
    public ModelObject getFrontVotingModel(ModelObject group, long uid) {
        int votingCount = group.getIntValue(TableWxpluginVotingGroup.count);
        int type = group.getIntValue(TableWxpluginVotingGroup.type);
        int gid = group.getIntValue(TableWxpluginVotingItem.id);
        List<ModelObject> items = sessionTemplate.list(
                Criteria.query(TableWxpluginVotingItem.class)
                        .eq(TableWxpluginVotingItem.gid, gid)
                        .order(TableWxpluginVotingItem.count, false)
        );
        List<ModelObject> logs = sessionTemplate.list(
                Criteria.query(TableWxpluginVotingLog.class)
                        .eq(TableWxpluginVotingLog.gid, gid)
                        .eq(TableWxpluginVotingLog.userId, uid)
        );

        group.put("notAllowVoting", false);
        if ((logs != null && logs.size() >= votingCount) || votingCount == 0) {
            group.put("notAllowVoting", true);
        }

        if (logs != null && items != null && type == 0) {
            for (ModelObject item : items) {
                int iid = item.getIntValue(TableWxpluginVotingItem.id);
                item.put("userVoting", false);
                for (ModelObject log : logs) {
                    int logIid = log.getIntValue(TableWxpluginVotingLog.iid);
                    if (iid == logIid) {
                        item.put("userVoting", true);
                    }
                }
            }
        }

        group.put("items", items);

        return group;
    }

    @Override
    public ModelObject getItemById(int id) {
        return sessionTemplate.get(
                Criteria.query(TableWxpluginVotingItem.class)
                        .eq(TableWxpluginVotingItem.id, id)
        );
    }
}
