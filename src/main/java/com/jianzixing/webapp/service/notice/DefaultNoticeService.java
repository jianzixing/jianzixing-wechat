package com.jianzixing.webapp.service.notice;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.BasicService;
import com.jianzixing.webapp.tables.notice.TableNotice;
import org.mimosaframework.orm.SessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultNoticeService extends BasicService implements NoticeService {
    @Autowired
    SessionTemplate sessionTemplate;


    @Override
    public SessionTemplate getSessionTemplate() {
        return sessionTemplate;
    }

    @Override
    public Class getTable() {
        return TableNotice.class;
    }

    @Override
    public void delete(int id) throws ModuleException {
        super.delete(id);
    }

    @Override
    public void delete(List ids) throws ModuleException {
        super.delete(ids);
    }
}
