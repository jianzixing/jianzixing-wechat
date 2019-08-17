package com.jianzixing.webapp.service.marketing;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.util.List;

public interface EmailService {
    void addEmail(ModelObject modelObject) throws ModelCheckerException;

    void updateEmail(ModelObject modelObject) throws ModelCheckerException;

    void delEmail(int id);

    void enableEmail(int id) throws ModelCheckerException;

    void disableEmail(int id) throws ModelCheckerException;

    ModelObject getDefaultEmail();

    void sendEmail(EmailParams emailParams) throws Exception;

    void sendDefaultEmail(List<String> tousers, String subject, String content) throws Exception;

    List<ModelObject> getEmails();

    List<ModelObject> getEnableEmails(String keyword);

    ModelObject getEmailById(long id);
}
