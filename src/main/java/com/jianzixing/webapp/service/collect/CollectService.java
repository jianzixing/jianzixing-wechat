package com.jianzixing.webapp.service.collect;

import org.mimosaframework.core.json.ModelObject;

import java.util.List;

public interface CollectService {
    void addCollect(long uid, long gid, long sid, CollectType type);

    void delCollect(long uid, long gid, CollectType type);

    boolean isCollect(long uid, long gid, CollectType type);

    long getUserCollectCount(long uid, CollectType type);

    List<ModelObject> getUserCollects(long uid, CollectType goods, int page);

    List<ModelObject> getCollectGoods(long uid, CollectType goods, int page);
}
