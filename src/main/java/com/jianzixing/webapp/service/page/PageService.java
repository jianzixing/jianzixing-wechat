package com.jianzixing.webapp.service.page;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

import java.util.List;

/**
 * @author qinmingtao
 */
public interface PageService {
    //页面列表
    Paging<ModelObject> getPageList(int type, ModelObject search, int start, int limit);

    List<ModelObject> getPageAndPageContentById(int pageId);

    void addPage(ModelObject pageModel) throws ModelCheckerException;

    void updatePage(ModelObject pageModel) throws ModelCheckerException;

    void deletePage(int pageId);

    void addPageContent(ModelObject pageContent) throws ModelCheckerException;

    void updatePageContent(ModelObject pageContent) throws ModelCheckerException;

    void deletePageContent(int pageContentId);

    /**
     * 前台获取页面
     * @param search 查询条件，一种根据id搜索，一种根据类型
     * @return 获取到的页面
     */
    ModelObject getFrontPage(ModelObject search);

    void setIndexEnable(int pageId);
}
