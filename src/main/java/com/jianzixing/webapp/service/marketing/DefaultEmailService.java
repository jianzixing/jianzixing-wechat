package com.jianzixing.webapp.service.marketing;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.marketing.TableMarketEmail;
import com.jianzixing.webapp.tables.marketing.TableMarketEmailRecord;
import com.jianzixing.webapp.tables.system.TableSystemConfig;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.DefaultUpdate;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
public class DefaultEmailService implements EmailService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addEmail(ModelObject modelObject) throws ModelCheckerException {
        modelObject.setObjectClass(TableMarketEmail.class);
        modelObject.checkUpdateThrowable();

        modelObject.put(TableMarketEmail.enable, 1);
        sessionTemplate.save(modelObject);
    }

    @Override
    public void updateEmail(ModelObject modelObject) throws ModelCheckerException {
        modelObject.setObjectClass(TableMarketEmail.class);
        modelObject.checkUpdateThrowable();

        sessionTemplate.update(modelObject);
    }

    @Override
    public void delEmail(int id) {
        ModelObject modelObject = new ModelObject();
        modelObject.setObjectClass(TableMarketEmail.class);

        modelObject.put(TableMarketEmail.id, id);
        sessionTemplate.delete(modelObject);
    }

    @Override
    public void enableEmail(int id) {
        ModelObject modelObject = new ModelObject(TableMarketEmail.class);
        modelObject.put(TableMarketEmail.id, id);
        modelObject.put(TableMarketEmail.enable, 1);
        sessionTemplate.update(modelObject);
    }

    @Override
    public void disableEmail(int id) {
        ModelObject modelObject = new ModelObject(TableMarketEmail.class);
        modelObject.put(TableMarketEmail.id, id);
        modelObject.put(TableMarketEmail.enable, 0);
        sessionTemplate.update(modelObject);
    }

    @Override
    public ModelObject getDefaultEmail() {
        return sessionTemplate.get(
                Criteria.query(TableMarketEmail.class).eq(TableMarketEmail.enable, 1)
        );
    }

    private void sendEmail(ModelObject email, List<String> tousers, String subject, String content) throws Exception {
        if (email == null) {
            throw new ModuleException(StockCode.ARG_NULL, "不存在的邮件服务");
        }
        if (tousers != null && tousers.size() > 0) {
            int emailId = email.getIntValue(TableMarketEmail.id);
            String sender = email.getString(TableMarketEmail.smtpUserName);
            String password = email.getString(TableMarketEmail.smtpPassword);
            String smtp = email.getString(TableMarketEmail.smtpAddress);
            String port = email.getString(TableMarketEmail.smtpPort);

            try {
                String encoding = email.getString(TableMarketEmail.encoding);
                int ssl = email.getIntValue(TableMarketEmail.ssl);
                subject = new String(subject.getBytes(), encoding);
                content = new String(content.getBytes(), encoding);

                InternetAddress[] addresses = new InternetAddress[tousers.size()];
                int i = 0;
                for (String user : tousers) {
                    addresses[i] = new InternetAddress(user);
                    i++;
                }

                Properties props = new Properties();
                props.setProperty("mail.debug", "false");  //false
                props.setProperty("mail.smtp.auth", "true");
                props.setProperty("mail.host", smtp);
                props.setProperty("mail.transport.protocol", "smtp");
                props.setProperty("mail.smtp.port", port);
                props.put("mail.smtp.starttls.enable", "true");
                // props.put("mail.smtp.ssl.checkserveridentity", "false");

                if (ssl == 1) {
                    Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
                    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
                    props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
                    props.setProperty("mail.smtp.socketFactory.fallback", "false");
                    props.setProperty("mail.smtp.socketFactory.port", port);
                }

                Session session = Session.getInstance(props);
                MimeMessage msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(sender));
                msg.addRecipients(Message.RecipientType.TO, addresses);
                msg.setSubject(subject);

                // 设置邮件内容
                Multipart multipart = new MimeMultipart();

                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(content);
                multipart.addBodyPart(textPart);

                msg.setContent(multipart);
                Transport transport = session.getTransport();
                transport.connect(sender, password);
                transport.sendMessage(msg, addresses);
                transport.close();

                for (String touser : tousers) {
                    addEmailRecord(subject, emailId, sender, touser, "执行成功");
                }
            } catch (Exception e) {
                for (String touser : tousers) {
                    addEmailRecord(subject, emailId, sender, touser, "执行失败:" + e.getMessage());
                }
                throw e;
            }
        }
    }

    private void addEmailRecord(String subject, int emailId, String sender, String touser, String result) {
        ModelObject object = new ModelObject(TableMarketEmailRecord.class);
        object.put(TableMarketEmailRecord.from, sender);
        object.put(TableMarketEmailRecord.to, touser.trim());
        object.put(TableMarketEmailRecord.emailId, emailId);
        object.put(TableMarketEmailRecord.subject, subject);
        object.put(TableMarketEmailRecord.result, result);
        object.put(TableMarketEmailRecord.createTime, new Date());
        sessionTemplate.save(object);
    }

    @Override
    public void sendEmail(EmailParams emailParams) throws Exception {
        ModelObject email = emailParams.getEmail();
        if (email == null) {
            email = this.getEmailById(emailParams.getSid());
        }
        this.sendEmail(email, emailParams.getAllAddresses(), emailParams.getSubject(), emailParams.getContent());
    }

    @Override
    public void sendDefaultEmail(List<String> tousers, String subject, String content) throws Exception {
        ModelObject email = this.getDefaultEmail();
        this.sendEmail(email, tousers, subject, content);
    }

    @Override
    public List<ModelObject> getEmails() {
        Query query = Criteria.query(TableMarketEmail.class);
        query.order(TableMarketEmail.id, true);
        return sessionTemplate.list(query);
    }

    @Override
    public List<ModelObject> getEnableEmails(String keyword) {
        Query query = Criteria.query(TableMarketEmail.class);
        query.order(TableMarketEmail.id, true);
        query.eq(TableMarketEmail.enable, 1);
        if (StringUtils.isNotBlank(keyword)) {
            query.like(TableMarketEmail.name, "%" + keyword + "%");
        }
        return sessionTemplate.list(query);
    }

    @Override
    public ModelObject getEmailById(long id) {
        return sessionTemplate.get(
                Criteria.query(TableMarketEmail.class)
                        .eq(TableMarketEmail.id, id)
                        .eq(TableMarketEmail.enable, 1)
        );
    }

}
