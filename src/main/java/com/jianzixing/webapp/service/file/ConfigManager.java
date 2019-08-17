package com.jianzixing.webapp.service.file;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.mimosaframework.core.utils.RequestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConfigManager {
    private static JSONObject object;
    private HttpServletRequest request;
    private String resource;

    public ConfigManager(HttpServletRequest request, String resource) throws IOException {
        this.request = request;
        this.resource = resource;

        this.init();
    }

    private void init() throws IOException {
        if (object == null) {
            InputStream inputStream = ConfigManager.class.getResourceAsStream("/" + this.resource);
            try {
                List<String> lines = IOUtils.readLines(inputStream);
                StringBuilder sb = new StringBuilder();
                if (lines != null) {
                    for (String s : lines) {
                        sb.append(s);
                    }
                }
                object = JSONObject.parseObject(sb.toString());
            } finally {
                inputStream.close();
            }
        }
    }

    public String getConfigFileRootPath() {
        String absoluteFilePath = object.getString("absoluteFilePath");
        if (absoluteFilePath.startsWith("../")) {
            File file = RequestUtils.getWebAppRoot(absoluteFilePath);
            try {
                absoluteFilePath = file.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return absoluteFilePath;
    }

    public String getFileRootPath() {
        String absoluteFilePath = object.getString("absoluteFilePath");
        if (absoluteFilePath.startsWith("../")) {
            File file = RequestUtils.getWebAppRoot(absoluteFilePath);
            try {
                absoluteFilePath = file.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.isBlank(absoluteFilePath)) {
            ServletContext context = request.getSession().getServletContext();
            String rootPath = context.getRealPath("/");
            return rootPath;
        }
        return absoluteFilePath;
    }

    public String getFilePath(String fileName) {
        String absoluteFilePath = this.getFileRootPath();
        String filePath = object.getString("filePathFormat");
        String[] s = fileName.split("\\.");
        if (s.length == 2) {
            filePath = filePath.replace("{type}", s[1]);
        } else {
            filePath = filePath.replace("{type}", "unknown");
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        filePath = filePath.replace("{time}", format.format(new Date()));

        if (StringUtils.isNotBlank(absoluteFilePath)) {
            if (StringUtils.isNotBlank(fileName)) {
                return absoluteFilePath + filePath + fileName;
            } else {
                return absoluteFilePath + filePath;
            }
        }
        return null;
    }

    public boolean isAllowUpload(String fileType) {
        JSONArray allowType = object.getJSONArray("allowTypes");
        return allowType.contains(fileType.toLowerCase());
    }
}
