package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

import java.util.List;

@APIController
public class SystemDictController {

    @Printer(name = "添加字典类型")
    public ResponseMessage addDictType(ModelObject object) {

        try {
            GlobalService.systemDict.addType(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return null;
    }

    @Printer(name = "删除字典类型")
    public ResponseMessage delDictType(List<Integer> ids) {
        for (int id : ids) {
            GlobalService.systemDict.delType(id);
        }
        return new ResponseMessage();
    }

    @Printer(name = "更新字典类型")
    public ResponseMessage updateType(ModelObject object) {
        try {
            GlobalService.systemDict.updateType(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return null;
    }

    @Printer(name = "拷贝字典类型")
    public ResponseMessage copyType(ModelObject object) {
        try {
            GlobalService.systemDict.copy(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return null;
    }

    @Printer(name = "查看字段类型")
    public ResponsePageMessage getTypes(SearchForm searchForm, int start, int limit) {
        Paging paging = GlobalService.systemDict.getTypes(searchForm != null ? searchForm.getQuery() : null, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer(name = "添加字典")
    public ResponseMessage addDict(ModelObject object) {
        try {
            GlobalService.systemDict.addDict(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return null;
    }

    @Printer(name = "删除字典")
    public ResponseMessage delDict(List<Integer> ids) {
        for (int id : ids) {
            GlobalService.systemDict.delDict(id);
        }
        return new ResponseMessage();
    }

    @Printer(name = "更新字典")
    public ResponseMessage updateDict(ModelObject object) {
        try {
            GlobalService.systemDict.updateDict(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return null;
    }

    @Printer(name = "查看字典列表")
    public ResponseMessage getDicts(int tid) {
        return new ResponseMessage(GlobalService.systemDict.getDicts(tid));
    }


    @Printer(name = "查看客户性质字典")
    public ResponseMessage getClientNature() {
        //客户性质
        return new ResponseMessage(GlobalService.systemDict.getDicts("TableClientUser", "nature"));
    }

    @Printer(name = "查看客户来源字典")
    public ResponseMessage getClientSource() {
        //客户来源
        return new ResponseMessage(GlobalService.systemDict.getDicts("TableClientUser", "source"));
    }

    @Printer(name = "查看客户类别字典")
    public ResponseMessage getClientCategory() {
        //客户类别
        return new ResponseMessage(GlobalService.systemDict.getDicts("TableClientUser", "category"));
    }

    @Printer(name = "查看所属行业字典")
    public ResponseMessage getClientTMT() {
        //所属行业
        return new ResponseMessage(GlobalService.systemDict.getDicts("TableClientUser", "tmt"));
    }

    @Printer(name = "查看表字典")
    public ResponseMessage getDictsByTable(String tableName, String field) {
        return new ResponseMessage(GlobalService.systemDict.getDicts(tableName, field));
    }
}
