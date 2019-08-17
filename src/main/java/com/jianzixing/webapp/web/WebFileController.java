package com.jianzixing.webapp.web;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.handler.ResponseFileObjectWrapper;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import com.jianzixing.webapp.handler.RequestFileWrapper;
import com.jianzixing.webapp.service.file.FileInfo;
import com.jianzixing.webapp.tables.file.TableFiles;
import org.mimosaframework.core.utils.RequestUtils;
import org.mimosaframework.core.utils.ResponseUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yangankang
 */
@APIController
public class WebFileController {

    @RequestMapping("/web/image/load")
    public void fileLoad(HttpServletResponse response, String f) {
        try {
            ModelObject object = GlobalService.fileService.getFileByName(f);
            if (object != null) {
                String type = object.getString(TableFiles.type);
                int isDwn = object.getIntValue(TableFiles.isDwn);
                if (isDwn == 0) {
                    response.getWriter().print("当前文件不允许下载");
                } else {
                    String ct = ResponseUtils.contentTypes.get(type);
                    if (StringUtils.isNotBlank(type)) response.setContentType(ct);
                    File file = GlobalService.fileService.getSystemFile(object);
                    if (file.exists()) {
                        InputStream stream = new FileInputStream(file);
                        OutputStream out = response.getOutputStream();
                        IOUtils.copy(stream, out);
                        out.flush();
                        out.close();
                    } else {
                        response.getWriter().print("服务器没有找到图片文件");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Printer
    public String upload(RequestFileWrapper fileWrapper) {
        List<FileInfo> fileInfos = fileWrapper.getCacheFiles();
        List<ModelObject> files = new ArrayList<>();
        for (FileInfo fileInfo : fileInfos) {
            ModelObject f = new ModelObject();
            f.put("fid", fileInfo.getFid());
            f.put("fileName", fileInfo.getFileName());
            f.put("fileRealName", fileInfo.getFileRealName());
            files.add(f);
        }
        ModelObject result = new ModelObject();
        result.put("files", files);
        result.put("code", 100);
        return result.toJSONString();
    }

    @Printer
    public ResponseFileObjectWrapper load(String f) {
        ResponseFileObjectWrapper responseHandler = new ResponseFileObjectWrapper();
        ModelObject object = GlobalService.fileService.getFileByName(f);
        int isDwn = object.getIntValue(TableFiles.isDwn);
        if (isDwn == 0) {
        } else {
            responseHandler.setValue(object);
        }
        return responseHandler;
    }
}
