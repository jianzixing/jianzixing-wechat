package com.jianzixing.webapp.handler;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.file.TableFiles;
import org.mimosaframework.core.utils.RequestUtils;
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

public class ResponseFileObjectInstance implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        Class c = returnType.getParameterType();
        if (c.isAssignableFrom(ResponseFileObjectWrapper.class)) {
            return true;
        }
        return false;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        ModelObject value = ((ResponseFileObjectWrapper) returnValue).getValue();
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        // 设置不使用spring mvc 的解析器解析而是直接返回null
        mavContainer.setRequestHandled(true);
        if (value != null) {
            FileInputStream stream = null;
            String type = value.getString(TableFiles.type);
            if (StringUtils.isNotBlank(type)) {
                response.setContentType(ResponseUtils.contentTypes.get(type));
            }
            try {
                File file = GlobalService.fileService.getSystemFile(value);
                if (file.exists()) {
                    stream = new FileInputStream(file);
                    IOUtils.copy(stream, response.getOutputStream());
                } else {
                    response.getWriter().print("服务器没有找到图片文件");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            try {
                response.getWriter().print("服务器没有找到图片文件");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
