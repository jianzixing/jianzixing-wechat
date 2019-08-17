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
public class SensitiveWordsController {

    @Printer(name = "添加敏感词")
    public ResponseMessage addWord(ModelObject object) {
        try {
            GlobalService.sensitiveWordsService.addWord(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除敏感词")
    public ResponseMessage delWord(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.sensitiveWordsService.delWord(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer(name = "更新敏感词")
    public ResponseMessage updateWord(ModelObject object) {
        try {
            GlobalService.sensitiveWordsService.updateWord(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "查看敏感词列表")
    public ResponsePageMessage getWords(SearchForm form, int start, int limit) {
        Paging paging = GlobalService.sensitiveWordsService.getWords(form != null ? form.getQuery() : null, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer(name = "使敏感词有效")
    public ResponseMessage setEnable(List<Integer> ids) {
        GlobalService.sensitiveWordsService.setEnable(ids);
        return new ResponseMessage();
    }

    @Printer(name = "使敏感词无效")
    public ResponseMessage setDisable(List<Integer> ids) {
        GlobalService.sensitiveWordsService.setDisable(ids);
        return new ResponseMessage();
    }
}
