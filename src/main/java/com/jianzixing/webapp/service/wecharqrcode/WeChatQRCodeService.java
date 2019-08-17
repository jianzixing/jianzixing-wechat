package com.jianzixing.webapp.service.wecharqrcode;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.tables.wechat.TableWeChatQrcode;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

public interface WeChatQRCodeService {
    void addQRCode(ModelObject object) throws ModelCheckerException, ModuleException;

    void deleteQRCode(int id);

    void updateQRCode(ModelObject object);

    Paging getQRCodes(Query query, int openType, int accountId, int start, int limit);

    void addOpenid(String sceneId, String openid, boolean isFromMark);

    void removeOpenid(String openid);

    void addCount(TableWeChatQrcode field, String sceneId, int count);

    void subCount(TableWeChatQrcode field, String sceneId, int count);
}
