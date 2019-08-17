package com.jianzixing.webapp.service.wechat;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.wechat.model.AccountConfig;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WeChatInterfaceUtils {
    public static boolean isResponseSuccess(ModelObject object, AccountConfig accountConfig) {
        if (object.containsKey("errcode")
                && object.getIntValue("errcode") != 0) {
            if (object.getIntValue("errcode") == 40001
                    && accountConfig != null) {
                GlobalService.weChatService.emptyAccountToken(accountConfig);
                throw new IllegalArgumentException("调用微信接口失败: 获取AccessToken失败,您可以重试一次" + object.toJSONString());
            } else {
                throw new IllegalArgumentException("调用微信接口失败: " + object.toJSONString());
            }
        }
        return true;
    }

    public static boolean isMiniProgramSuccess(ModelObject object) {
        if (object.containsKey("errcode")
                && object.getIntValue("errcode") != 0) {
            throw new IllegalArgumentException("调用微信接口失败: " + object.toJSONString());
        }
        return true;
    }

    public static ModelObject xmlToModel(String xml) {
        return (ModelObject) xmlToModelObj(xml);
    }

    public static Object xmlToModelObj(String xml) {
        ModelObject map = new ModelObject();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            List<Element> list = rootElt.elements();// 获取根节点下所有节点
            for (Element element : list) { // 遍历节点
                String xmlName = element.getName();
                String name = xmlName;
                if (element.isTextOnly()) {
                    map.put(name, element.getText()); // 节点的name为map的key，text为map的value
                } else {
                    String subXML = element.asXML();
                    subXML = subXML.replace("<" + xmlName + ">", "<xml>");
                    subXML = subXML.replace("</" + xmlName + ">", "</xml>");
                    Object sub = xmlToModelObj(subXML);
                    if (map.get(name) != null) {
                        Object subObj = map.get(name);
                        ModelArray array = null;
                        if (subObj instanceof ModelArray) {
                            array.add(sub);
                        } else {
                            array = new ModelArray();
                            array.add(subObj);
                            array.add(sub);
                        }
                        map.put(name, array);
                    } else {
                        map.put(name, sub);
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    public static String modelToXml(ModelObject object, boolean hasXml) {
        StringBuilder sb = new StringBuilder();
        if (hasXml) sb.append("<xml>");
        Iterator<Map.Entry<Object, Object>> iterator = object.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, Object> entry = iterator.next();
            String key = String.valueOf(entry.getKey());
            sb.append("<" + key + ">");

            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("<![CDATA[" + value + "]]>");
            } else {
                if (value instanceof ModelObject) {
                    String child = modelToXml((ModelObject) value, false);
                    sb.append(child);
                } else if (value instanceof List) {
                    List list = (List) value;
                    for (int i = 0; i < list.size(); i++) {
                        Object o = list.get(i);
                        if (o instanceof ModelObject) {
                            String child = modelToXml((ModelObject) o, false);
                            sb.append(child);
                        } else {
                            sb.append(o);
                        }
                    }
                } else {
                    sb.append(value);
                }
            }


            sb.append("</" + key + ">");
        }
        if (hasXml) sb.append("</xml>");
        return sb.toString();
    }

    public static void main(String[] args) {
        String xml = "<xml>" +
                "<ToUserName>" +
                "<![CDATA[toUser]]>" +
                "</ToUserName>" +
                "<FromUserName>" +
                "<![CDATA[FromUser]]>" +
                "</FromUserName>" +
                "<CreateTime>123456789</CreateTime>" +
                "<MsgType><![CDATA[event]]>" +
                "</MsgType><Event>" +
                "<![CDATA[subscribe]]></Event></xml>";
        System.out.println(xmlToModel(xml));
    }
}
