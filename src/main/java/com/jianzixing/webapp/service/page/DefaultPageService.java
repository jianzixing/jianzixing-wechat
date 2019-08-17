package com.jianzixing.webapp.service.page;

import com.jianzixing.webapp.tables.page.TablePage;
import com.jianzixing.webapp.tables.page.TablePageContent;
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

/**
 * @author qinmingtao
 */
@Service
public class DefaultPageService implements PageService{
    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public Paging<ModelObject> getPageList(int type, ModelObject search, int start, int limit) {
        Query query= Criteria.query(TablePage.class).eq(TablePage.type, type).limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ModelObject> getPageAndPageContentById(int pageId) {
        Query query=Criteria.query(TablePageContent.class).eq(TablePageContent.pageId, pageId).order(TablePageContent.pos, true);
        return sessionTemplate.list(query);
    }

    @Override
    public void addPage(ModelObject pageModel) throws ModelCheckerException {
        pageModel.setObjectClass(TablePage.class);
        pageModel.put(TablePage.createTime, new Date());
        pageModel.put(TablePage.enable, 1);
        pageModel.checkAndThrowable();
        sessionTemplate.save(pageModel);
    }

    @Override
    public void updatePage(ModelObject pageModel) throws ModelCheckerException {
        pageModel.setObjectClass(TablePage.class);
        pageModel.checkUpdateThrowable();
        sessionTemplate.update(pageModel);
    }

    @Override
    public void deletePage(int pageId) {
        sessionTemplate.delete(Criteria.delete(TablePage.class).eq(TablePage.id, pageId));
    }

    @Override
    public void addPageContent(ModelObject pageContent) throws ModelCheckerException {
        pageContent.setObjectClass(TablePageContent.class);
        pageContent.checkAndThrowable();
        sessionTemplate.save(pageContent);
    }

    @Override
    public void updatePageContent(ModelObject pageContent) throws ModelCheckerException {
        pageContent.setObjectClass(TablePageContent.class);
        pageContent.checkUpdateThrowable();
        sessionTemplate.update(pageContent);
    }

    @Override
    public void deletePageContent(int pageContentId) {
        sessionTemplate.delete(Criteria.delete(TablePageContent.class).eq(TablePageContent.id, pageContentId));
    }

    @Override
    public ModelObject getFrontPage(ModelObject search) {
        Query query=Criteria.query(TablePage.class);

        if(search.containsKey(TablePage.id)){
            query.eq(TablePage.id, search.getIntValue(TablePage.id));
        }
        if(search.containsKey(TablePage.type)){
            query.eq(TablePage.type, search.getIntValue(TablePage.type));
            query.eq(TablePage.enable, 1);
        }
        return sessionTemplate.get(query);
    }

    @Override
    public void setIndexEnable(int pageId) {
        sessionTemplate.update(Criteria.update(TablePage.class).value(TablePage.enable, 0).eq(TablePage.type, 1));
        sessionTemplate.update(Criteria.update(TablePage.class).value(TablePage.enable, 1).eq(TablePage.id, pageId));
    }
}
