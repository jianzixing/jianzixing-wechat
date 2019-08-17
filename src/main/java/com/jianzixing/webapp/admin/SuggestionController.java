package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

import java.util.List;

@APIController
public class SuggestionController {

    @Printer(name = "查看用户建议列表")
    public ResponsePageMessage getSuggestions(SearchForm form, int start, int limit) {
        Paging paging = GlobalService.suggestionService.getSuggestions(form != null ? form.getQuery() : null, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer(name = "删除用户建议列表")
    public ResponseMessage delSuggestions(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.suggestionService.delSuggestion(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer(name = "发送建议回复")
    public ResponseMessage sendReply(int id, String reply) {
        GlobalService.suggestionService.replyContent(id, reply);
        return new ResponseMessage();
    }
}
