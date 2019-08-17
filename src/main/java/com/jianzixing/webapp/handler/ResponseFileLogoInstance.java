package com.jianzixing.webapp.handler;

import com.jianzixing.webapp.tables.file.TableFiles;
import org.mimosaframework.core.utils.ResponseUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResponseFileLogoInstance implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        Class c = returnType.getParameterType();
        if (c.isAssignableFrom(ResponseFileLogoWrapper.class)) {
            return true;
        }
        return false;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 设置不使用视图解析器
        mavContainer.setRequestHandled(true);
        if (returnValue != null) {
            ModelObject value = ((ResponseFileLogoWrapper) returnValue).getValue();
            HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
            String type = value.getString(TableFiles.type);
            String ct = ResponseUtils.contentTypes.get(type);
            if (StringUtils.isNotBlank(type)) {
                response.setContentType(ct);
            }

            InputStream inputStream = ResponseFileLogoWrapper.class.getResourceAsStream("/images/logo_" + type + ".png");
            try {
                if (inputStream != null) {
                    response.setContentType(ResponseUtils.contentTypes.get("png"));
                    IOUtils.copy(inputStream, response.getOutputStream());
                } else {
                    if (ct.startsWith("image/")) {
                        File file = new File(value.getString(TableFiles.path));
                        if (file.exists()) {
                            inputStream = new FileInputStream(value.getString(TableFiles.path));
                            IOUtils.copy(inputStream, response.getOutputStream());
                        } else {
                            response.getWriter().print("服务器没有找到图片文件");
                        }
                    } else {
                        inputStream = ResponseFileLogoWrapper.class.getResourceAsStream("/images/logo_unknown_type.png");
                        response.setContentType(ResponseUtils.contentTypes.get("png"));
                        IOUtils.copy(inputStream, response.getOutputStream());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
