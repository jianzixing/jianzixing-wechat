package com.jianzixing.webapp.service.cooperation;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

public interface FriendLinkService {
    void addFriendLink(ModelObject object) throws ModelCheckerException;

    void delFriendLink(int id);

    void updateFriendLink(ModelObject object) throws ModelCheckerException;

    Paging getFriendLink(int start, int limit);
}
