package com.jianzixing.webapp.service.cooperation;

import com.jianzixing.webapp.tables.cooperation.TableFriendLink;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DefaultFriendLinkService implements FriendLinkService {
    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addFriendLink(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableFriendLink.class);
        object.checkAndThrowable();

        sessionTemplate.save(object);
    }

    @Override
    public void delFriendLink(int id) {
        sessionTemplate.delete(TableFriendLink.class, id);
    }

    @Override
    public void updateFriendLink(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableFriendLink.class);
        object.put(TableFriendLink.createTime, new Date());
        object.checkUpdateThrowable();

        sessionTemplate.update(object);
    }

    @Override
    public Paging getFriendLink(int start, int limit) {
        Query query = Criteria.query(TableFriendLink.class);
        query.limit(start, limit);
        query.order(TableFriendLink.modifiedTime, false);
        return sessionTemplate.paging(query);
    }
}
