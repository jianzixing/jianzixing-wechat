package com.jianzixing.webapp.service.history;

import org.mimosaframework.core.json.ModelObject;

import java.util.List;

public interface HistoryService {

    void saveHistory(long uid, long hid, HistoryType type);

    void delHistory(long uid, long hid, HistoryType type);

    List<ModelObject> getHistoryGoods(long uid, HistoryType type, int page);

    long getUserHistoryCount(long uid, HistoryType goods);

    void clearHistory(long uid, HistoryType goods);
}
