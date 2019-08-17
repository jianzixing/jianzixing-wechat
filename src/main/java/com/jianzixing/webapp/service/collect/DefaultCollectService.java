package com.jianzixing.webapp.service.collect;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.goods.GoodsStatus;
import com.jianzixing.webapp.tables.collect.TableCollect;
import com.jianzixing.webapp.tables.goods.TableGoods;
import com.jianzixing.webapp.tables.goods.TableGoodsProperty;
import com.jianzixing.webapp.tables.goods.TableGoodsSku;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DefaultCollectService implements CollectService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addCollect(long uid, long gid, long sid, CollectType type) {
        ModelObject object = new ModelObject(TableCollect.class);
        object.put(TableCollect.cid, gid);
        object.put(TableCollect.uid, uid);
        if (sid > 0) {
            object.put(TableCollect.sid, sid);
        }
        object.put(TableCollect.type, type.getCode());
        object.put(TableCollect.createTime, new Date());
        ModelObject old = sessionTemplate.get(Criteria.query(TableCollect.class)
                .eq(TableCollect.cid, gid)
                .eq(TableCollect.uid, uid)
                .eq(TableCollect.type, type.getCode()));
        if (old == null) {
            sessionTemplate.save(object);
        }
    }

    @Override
    public void delCollect(long uid, long gid, CollectType type) {
        sessionTemplate.delete(Criteria.delete(TableCollect.class)
                .eq(TableCollect.cid, gid)
                .eq(TableCollect.uid, uid)
                .eq(TableCollect.type, type.getCode()));
    }

    @Override
    public boolean isCollect(long uid, long gid, CollectType type) {
        ModelObject old = sessionTemplate.get(Criteria.query(TableCollect.class)
                .eq(TableCollect.cid, gid)
                .eq(TableCollect.uid, uid)
                .eq(TableCollect.type, type.getCode()));
        if (old != null) {
            return true;
        }
        return false;
    }

    @Override
    public long getUserCollectCount(long uid, CollectType type) {
        return sessionTemplate.count(Criteria.query(TableCollect.class)
                .eq(TableCollect.uid, uid)
                .eq(TableCollect.type, type.getCode()));
    }

    @Override
    public List<ModelObject> getUserCollects(long uid, CollectType goods, int page) {
        int limit = 20;
        int start = (page - 1) * limit;
        return sessionTemplate.list(Criteria.query(TableCollect.class)
                .eq(TableCollect.type, goods.getCode())
                .eq(TableCollect.uid, uid)
                .order(TableCollect.createTime, false)
                .limit(start, limit));
    }

    @Override
    public List<ModelObject> getCollectGoods(long uid, CollectType goods, int page) {
        List<ModelObject> collects = this.getUserCollects(uid, goods, page);
        if (collects != null) {
            List<Long> gids = new ArrayList<>();
            List<Long> skus = new ArrayList<>();
            for (ModelObject collect : collects) {
                long cid = collect.getLongValue(TableCollect.cid);
                long sid = collect.getLongValue(TableCollect.sid);
                if (cid > 0) gids.add(cid);
                if (sid > 0) skus.add(sid);
            }

            List<ModelObject> gl = sessionTemplate.list(
                    Criteria.query(TableGoods.class)
                            .in(TableGoods.id, gids)
                            .eq(TableGoods.isDelete, 0)
                            .in(TableGoods.status, GoodsStatus.UP.getCode(), GoodsStatus.DOWN.getCode())
            );
            List<ModelObject> sl = null;
            if (skus.size() > 0) {
                sl = GlobalService.goodsService.getSimpleSkus(skus);
            }

            if (gl != null && sl != null) {
                for (ModelObject g : gl) {
                    List<ModelObject> name = new ArrayList<>();
                    for (ModelObject s : sl) {
                        if (g.getLongValue(TableGoods.id) == s.getLongValue(TableGoodsSku.goodsId)) {
                            List<ModelObject> ps = s.getArray(TableGoodsProperty.class);
                            if (ps != null) {
                                for (ModelObject p : ps) {
                                    name.add(p);
                                }
                            }
                        }
                    }
                    g.put("skuNameList", name);
                }
            }

            return gl;
        }
        return null;
    }
}
