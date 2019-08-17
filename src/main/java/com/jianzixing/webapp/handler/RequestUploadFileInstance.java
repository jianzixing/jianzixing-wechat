package com.jianzixing.webapp.handler;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.file.ConfigManager;
import com.jianzixing.webapp.service.file.FileInfo;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author yangankang
 */
public class RequestUploadFileInstance implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class type = parameter.getParameterType();
        if (type.isAssignableFrom(RequestFileWrapper.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request instanceof MultipartHttpServletRequest) {
            RequestFileWrapper uploadFileWrapper = new RequestFileWrapper();
            List<FileInfo> fileInfos = GlobalService.fileService.uploadFileMaps(request);
            uploadFileWrapper.setCacheFiles(fileInfos);
            return uploadFileWrapper;
        } else {
            return null;
        }
    }
}
