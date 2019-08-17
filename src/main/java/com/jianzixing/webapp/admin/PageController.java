package com.jianzixing.webapp.admin;

import com.alibaba.fastjson.JSON;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.page.TablePageContent;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

/**
 * @author qinmingtao
 */
@APIController
public class PageController {
    @Printer
    public ResponsePageMessage getIndexPages(ModelObject search, String keyword, int start, int limit) {
        if (StringUtils.isNotBlank(keyword)) search.put("name", keyword);
        return new ResponsePageMessage(GlobalService.pageService.getPageList(1, search, start, limit));
    }

    @Printer
    public ResponsePageMessage getActivityPages(ModelObject search, String keyword, int start, int limit) {
        if (StringUtils.isNotBlank(keyword)) search.put("name", keyword);
        return new ResponsePageMessage(GlobalService.pageService.getPageList(2, search, start, limit));
    }

    @Printer
    public ResponseMessage addPage(ModelObject object) {
        try {
            GlobalService.pageService.addPage(object);
        } catch (ModelCheckerException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deletePage(List<Integer> ids) {
        if(ids!=null && ids.size()>0){
            for (int id :ids){
                GlobalService.pageService.deletePage(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updatePage(ModelObject object) {
        try {
            GlobalService.pageService.updatePage(object);
        } catch (ModelCheckerException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage addPageContent(ModelObject object) {
        try {
            object.put(TablePageContent.data, JSON.toJSONString(object.get(TablePageContent.data)));
            GlobalService.pageService.addPageContent(object);
        } catch (ModelCheckerException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deletePageContent(List<Integer> ids) {
        if(ids!=null && ids.size()>0){
            for (int id :ids){
                GlobalService.pageService.deletePageContent(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updatePageContent(ModelObject object) {
        try {
            if(object.containsKey(TablePageContent.data)){
                object.put(TablePageContent.data, JSON.toJSONString(object.get(TablePageContent.data)));
            }
            GlobalService.pageService.updatePageContent(object);
        } catch (ModelCheckerException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getPageContent(int id) {
        return new ResponsePageMessage(GlobalService.pageService.getPageAndPageContentById(id));
    }

    @Printer
    public ResponseMessage setIndexEnable(int id) {
        GlobalService.pageService.setIndexEnable(id);
        return new ResponseMessage();
    }

}
