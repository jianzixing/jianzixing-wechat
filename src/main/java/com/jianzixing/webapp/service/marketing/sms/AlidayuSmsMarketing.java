package com.jianzixing.webapp.service.marketing.sms;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.jianzixing.webapp.service.marketing.SmsMarketing;
import com.jianzixing.webapp.service.marketing.SmsParams;
import com.jianzixing.webapp.service.marketing.SmsType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

/**
 * 需要配置的参数:
 * app_key,
 * [called_show_num][语言需要],
 * secret,
 * [sms_free_sign_name][短信需要]
 */
@Service
public class AlidayuSmsMarketing implements SmsMarketing {
    private static final Log logger = LogFactory.getLog(AlidayuSmsMarketing.class);

    @Override
    public String getName() {
        return "阿里云通信";
    }

    @Override
    public String getDetail() {
        return "阿里云通信接口实现";
    }

    @Override
    public ModelArray getParams() {
        ModelArray array = new ModelArray();
        ModelObject p1 = new ModelObject();
        p1.put("name", "Access Key ID");
        p1.put("code", "app_key");
        p1.put("detail", "应用ID");
        array.add(p1);
        ModelObject p2 = new ModelObject();
        p2.put("name", "Access Key Secret");
        p2.put("code", "app_secret");
        p2.put("detail", "应用秘钥");
        array.add(p2);
        ModelObject p3 = new ModelObject();
        p3.put("name", "短信签名");
        p3.put("code", "sign_name");
        p3.put("detail", "短信签名");
        array.add(p3);
        return array;
    }

    @Override
    public SmsType getSmsType() {
        return SmsType.TEMPLATE;
    }

    @Override
    public String send(ModelObject params, SmsParams smsParams) {
        String accessKeyId = params.getString("app_key");
        String accessSecret = params.getString("app_secret");
        String signName = params.getString("sign_name");
        String templateCode = smsParams.getTemplateCode();
        DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);

        String json = "";
        if (smsParams.getTemplateParam() != null) {
            json = smsParams.getTemplateParam().toJSONString();
        }
        List<String> phones = smsParams.getAllPhones();
        if (phones != null && phones.size() > 0) {
            StringBuilder sb = new StringBuilder();
            Iterator<String> iterator = phones.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next());
                if (iterator.hasNext()) {
                    sb.append(",");
                }
            }
            CommonRequest request = new CommonRequest();
            //request.setProtocol(ProtocolType.HTTPS);
            request.setMethod(MethodType.POST);
            request.setDomain("dysmsapi.aliyuncs.com");
            request.setVersion("2017-05-25");
            request.setAction("SendSms");
            request.putQueryParameter("PhoneNumbers", sb.toString());
            request.putQueryParameter("SignName", signName.trim());
            request.putQueryParameter("TemplateCode", templateCode);
            request.putQueryParameter("TemplateParam", json);

            logger.info("使用阿里云通信发送短信," +
                    "手机号码:[" + sb.toString() + "]," +
                    "短信模板:[" + templateCode + "]," +
                    "模板数据:[" + json + "]");

            try {
                CommonResponse response = client.getCommonResponse(request);
                HttpResponse httpResponse = response.getHttpResponse();
                if (httpResponse.isSuccess()) {
                    String content = httpResponse.getHttpContentString();
                    return content;
                } else {
                    return "http fail:" + httpResponse.getStatus();
                }
            } catch (ServerException e) {
                e.printStackTrace();
                return e.getMessage();
            } catch (ClientException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }
        return null;
    }
}
