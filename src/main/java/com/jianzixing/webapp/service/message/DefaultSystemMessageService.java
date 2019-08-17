package com.jianzixing.webapp.service.message;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.message.TableSystemMessage;
import com.jianzixing.webapp.tables.system.TableAdmin;
import com.jianzixing.webapp.tables.system.TableSystemDict;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.StringTools;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.criteria.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DefaultSystemMessageService implements SystemMessageService {
    private static final Log logger = LogFactory.getLog(DefaultSystemMessageService.class);

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void sendSystemMessage(ModelObject object, List<Integer> adminIds) throws ModelCheckerException {
        if (adminIds != null) {
            object.setObjectClass(TableSystemMessage.class);
            for (int id : adminIds) {
                object.put(TableSystemMessage.toAdminId, id);
                object.checkAndThrowable();
                object.put(TableSystemMessage.createTime, new Date());
                if (id == object.getIntValue(TableSystemMessage.fromAdminId)) {
                    object.put(TableSystemMessage.isRead, 1);
                }
                sessionTemplate.save(object);
            }
            if (!adminIds.contains(object.getIntValue(TableSystemMessage.fromAdminId))) {
                object.remove(TableSystemMessage.id);
                object.put(TableSystemMessage.toAdminId, object.getIntValue(TableSystemMessage.fromAdminId));
                object.put(TableSystemMessage.isRead, 1);
                sessionTemplate.save(object);
            }
        }
    }

    @Override
    public void sendAllSystemMessage(ModelObject object) throws ModelCheckerException {
        int start = 0;
        int limit = 100;
        int page = 1;
        while (true) {
            List<ModelObject> admins = GlobalService.adminService.getAdmins(start, limit);
            if (admins == null) {
                break;
            }
            for (ModelObject admin : admins) {
                object.put(TableSystemMessage.toAdminId, admin.getIntValue(TableAdmin.id));
                object.checkAndThrowable();
                object.put(TableSystemMessage.createTime, new Date());
                if (admin.getIntValue(TableAdmin.id) == object.getIntValue(TableSystemMessage.fromAdminId)) {
                    object.put(TableSystemMessage.isRead, 1);
                }
                sessionTemplate.save(object);
            }
            try {
                logger.info("发送给所有系统用户消息 第" + page + "页");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            start = page * limit;
            page++;
        }
    }

    @Override
    public void delSystemMessage(List<Integer> ids) {
        if (ids != null && ids.size() > 0) {
            sessionTemplate.delete(Criteria.delete(TableSystemMessage.class).in(TableSystemMessage.id, ids));
        }
    }

    @Override
    public Paging getSystemMessages(ModelObject search, int adminId, int start, int limit) {
        Query query = Criteria.query(TableSystemMessage.class);
        query.subjoin(TableSystemDict.class)
                .eq(TableSystemDict.table, new Value(TableSystemMessage.class.getSimpleName()))
                .eq(TableSystemDict.field, new Value(TableSystemMessage.type.name()))
                .eq(TableSystemDict.value, TableSystemMessage.type).single();
        query.subjoin(TableAdmin.class)
                .eq(TableAdmin.id, TableSystemMessage.fromAdminId).single();
        query.setTableClass(TableSystemMessage.class);
        query.limit(start, limit);
        query.order(TableSystemMessage.id, false);

        if (search != null) {
            if (search.isNotEmpty("fromAdminUserName")) {
                ModelObject admin = GlobalService.adminService.getAdminByUserName(search.getString("fromAdminUserName"));
                if (admin != null) {
                    query.eq(TableSystemMessage.fromAdminId, admin.getIntValue(TableAdmin.id));
                } else {
                    return null;
                }
            }
            if (search.isNotEmpty("toAdminUserName")) {
                ModelObject admin = GlobalService.adminService.getAdminByUserName(search.getString("toAdminUserName"));
                if (admin != null) {
                    query.eq(TableSystemMessage.toAdminId, admin.getIntValue(TableAdmin.id));
                } else {
                    return null;
                }
            }
            if (search.isNotEmpty("title")) {
                query.like(TableSystemMessage.title, "%" + search.getString("title") + "%");
            }
            if (search.isNotEmpty("content")) {
                query.like(TableSystemMessage.content, "%" + search.getString("content") + "%");
            }
            if (search.isNotEmpty("isRead")) {
                query.eq(TableSystemMessage.isRead, search.getIntValue("isRead"));
            }
            if (search.isNotEmpty("createTimeStart")) {
                query.gte(TableSystemMessage.createTime, search.getString("createTimeStart"));
            }
            if (search.isNotEmpty("createTimeEnd")) {
                query.lte(TableSystemMessage.createTime, search.getString("createTimeEnd"));
            }
        }

        Paging paging = sessionTemplate.paging(query);
        return paging;
    }

    @Override
    public void markRead(int adminId, List<Integer> ids) {
        if (ids != null) {
            sessionTemplate.update(Criteria.update(TableSystemMessage.class)
                    .eq(TableSystemMessage.toAdminId, adminId)
                    .in(TableSystemMessage.id, ids)
                    .value(TableSystemMessage.isRead, 1));
        }
    }

    @Override
    public void markAllRead(int adminId) {
        sessionTemplate.update(Criteria.update(TableSystemMessage.class)
                .eq(TableSystemMessage.toAdminId, adminId)
                .value(TableSystemMessage.isRead, 1));
    }

    @Override
    public ModelObject getNotShows(int adminId, int count) {
        Paging notReadPaging = sessionTemplate.paging(
                Criteria.query(TableSystemMessage.class)
                        .eq(TableSystemMessage.isRead, 0)
                        .order(TableSystemMessage.id, false)
                        .limit(0, count)
        );
        ModelObject notShow = sessionTemplate.get(
                Criteria.query(TableSystemMessage.class)
                        .eq(TableSystemMessage.isRead, 0)
                        .eq(TableSystemMessage.isShow, 0)
                        .subjoin(TableAdmin.class)
                        .eq(TableAdmin.id, TableSystemMessage.fromAdminId).single().query()
                        .order(TableSystemMessage.id, false)
                        .limit(0, 1)
        );
        if (notShow != null) {
            String html = StringTools.clearHtml(notShow.getString(TableSystemMessage.content));
            if (StringTools.isNotEmpty(html)) {
                notShow.put("clearContent", html.replaceAll(" ", ""));
            }
        }
        sessionTemplate.update(Criteria.update(TableSystemMessage.class)
                .eq(TableSystemMessage.isShow, 0)
                .value(TableSystemMessage.isShow, 1));

        ModelObject object = new ModelObject();
        object.put("notReads", notReadPaging);
        object.put("notShow", notShow);
        return object;
    }

    @Override
    public ModelObject getReadMessage(int adminId, int id) {
        ModelObject object = sessionTemplate.get(Criteria.query(TableSystemMessage.class)
                .eq(TableSystemMessage.id, id).eq(TableSystemMessage.toAdminId, adminId)
                .subjoin(TableAdmin.class)
                .eq(TableAdmin.id, TableSystemMessage.fromAdminId).single().query());
        if (object != null) {
            sessionTemplate.update(Criteria.update(TableSystemMessage.class)
                    .eq(TableSystemMessage.id, id).value(TableSystemMessage.isRead, 1));
        }
        return object;
    }
}
