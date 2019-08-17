package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.handler.RequestAdminWrapper;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.recommend.TableRecommendContent;
import com.jianzixing.webapp.tables.recommend.TableRecommendGroup;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@APIController
public class RecommendController {

    @Printer(name = "添加推荐分组")
    public ResponseMessage addGroup(RequestAdminWrapper adminWrapper, ModelObject object) {
        try {
            object.put(TableRecommendGroup.admin, adminWrapper.getUserName());
            GlobalService.recommendService.addGroup(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除推荐分组")
    public ResponseMessage delGroup(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.recommendService.delGroup(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer(name = "更新推荐分组")
    public ResponseMessage updateGroup(ModelObject object) {
        try {
            GlobalService.recommendService.updateGroup(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "查看推荐分组列表")
    public ResponseMessage getGroups() {
        List<ModelObject> objects = GlobalService.recommendService.getTreeGroup();
        return new ResponseMessage(objects);
    }

    @Printer(name = "添加推荐内容")
    public ResponseMessage addContent(RequestAdminWrapper adminWrapper, ModelObject object) {
        try {
            if (object != null) {
                object.put(TableRecommendContent.admin, adminWrapper.getUserName());
                GlobalService.recommendService.addContent(object);
            }
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除推荐内容")
    public ResponseMessage delContent(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.recommendService.delContent(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer(name = "更新推荐内容")
    public ResponseMessage updateContent(ModelObject object) {
        try {
            GlobalService.recommendService.updateContent(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "查看推荐内容列表")
    public ResponsePageMessage getContents(int gid, int start, int limit) {
        Paging paging = GlobalService.recommendService.getContents(gid, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer(name = "置顶推荐内容")
    public ResponseMessage setRecommendTop(int id, int level) {
        GlobalService.recommendService.setRecommendTop(id, level);
        return new ResponseMessage();
    }

    @Printer(name = "取消推荐置顶")
    public ResponseMessage setRecommendTopNormal(int id) {
        GlobalService.recommendService.setRecommendTop(id, 0);
        return new ResponseMessage();
    }

    @Printer(name = "设置推荐图片")
    public ResponseMessage setRecommendImage(int id, String cover) {
        GlobalService.recommendService.setRecommendImage(id, cover);
        return new ResponseMessage();
    }
}
