package com.jianzixing.webapp.handler;


import com.jianzixing.webapp.service.file.FileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangankang
 */
public class RequestFileWrapper {
    private List<FileInfo> cacheFiles;
    private Exception exception;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public List<FileInfo> getCacheFiles() {
        return cacheFiles;
    }

    public void addCacheFile(FileInfo file) {
        if (this.cacheFiles == null) this.cacheFiles = new ArrayList<>();
        this.cacheFiles.add(file);
    }

    public void setCacheFiles(List<FileInfo> cacheFiles) {
        this.cacheFiles = cacheFiles;
    }
}
