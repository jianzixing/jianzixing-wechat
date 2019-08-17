package com.jianzixing.webapp.service.history;

import com.jianzixing.webapp.service.goods.GoodsStatus;
import com.jianzixing.webapp.tables.goods.TableGoods;
import com.jianzixing.webapp.tables.history.TableHistory;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DefaultHistoryService implements HistoryService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void saveHistory(long uid, long hid, HistoryType type) {
        if (uid != 0 && hid != 0 && type != null) {
            ModelObject object = new ModelObject(TableHistory.class);

            ModelObject old = sessionTemplate.get(Criteria.query(TableHistory.class)
                    .eq(TableHistory.uid, uid)
                    .eq(TableHistory.hid, hid)
                    .eq(TableHistory.type, type.getCode()));
            if (old == null) {
                object.put(TableHistory.uid, uid);
                object.put(TableHistory.hid, hid);
                object.put(TableHistory.type, type.getCode());
                object.put(TableHistory.lastTime, new Date());
                object.put(TableHistory.createTime, new Date());
                sessionTemplate.save(object);
            } else {
                ModelObject update = new ModelObject(TableHistory.class);
                update.put(TableHistory.uid, uid);
                update.put(TableHistory.hid, hid);
                update.put(TableHistory.type, type.getCode());
                update.put(TableHistory.lastTime, new Date());
                sessionTemplate.update(update);
            }
        }
    }

    @Override
    public void delHistory(long uid, long hid, HistoryType type) {
        sessionTemplate.delete(Criteria.delete(TableHistory.class)
                .eq(TableHistory.uid, uid)
                .eq(TableHistory.hid, hid)
                .eq(TableHistory.type, type.getCode()));
    }


    @Override
    public List<ModelObject> getHistoryGoods(long uid, HistoryType type, int page) {
        int limit = 20;
        int start = (page - 1) * limit;
        List<ModelObject> historys = sessionTemplate.list(
                Criteria.query(TableHistory.class)
                        .eq(TableHistory.uid, uid)
                        .eq(TableHistory.type, type.getCode())
                        .limit(start, limit)
                        .order(TableHistory.createTime, false));

        if (historys != null) {
            List<Long> gids = new ArrayList<>();
            for (ModelObject history : historys) {
                long cid = history.getLongValue(TableHistory.hid);
                if (cid > 0) gids.add(cid);
            }

            if (gids.size() > 0) {
                List<ModelObject> gl = sessionTemplate.list(
                        Criteria.query(TableGoods.class)
                                .in(TableGoods.id, gids)
                                .eq(TableGoods.isDelete, 0)
                                .in(TableGoods.status, GoodsStatus.UP.getCode(), GoodsStatus.DOWN.getCode())
                );

                return gl;
            }
        }
        return null;
    }

    @Override
    public long getUserHistoryCount(long uid, HistoryType goods) {
        return sessionTemplate.count(Criteria.query(TableHistory.class)
                .eq(TableHistory.uid, uid)
                .eq(TableHistory.type, goods.getCode()));
    }

    @Override
    public void clearHistory(long uid, HistoryType goods) {
        sessionTemplate.delete(Criteria.delete(TableHistory.class)
                .eq(TableHistory.uid, uid)
                .eq(TableHistory.type, goods.getCode()));
    }
}
