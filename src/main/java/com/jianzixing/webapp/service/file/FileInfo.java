package com.jianzixing.webapp.service.file;

import java.io.File;

public class FileInfo {
    private boolean isAllowFile = true;
    private boolean isExistFile = false;
    private File file;
    private String fileRealName;
    private String fileName;
    private int fid;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isAllowFile() {
        return isAllowFile;
    }

    public void setAllowFile(boolean allowFile) {
        isAllowFile = allowFile;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public boolean isExistFile() {
        return isExistFile;
    }

    public void setExistFile(boolean existFile) {
        isExistFile = existFile;
    }

    public String getFileRealName() {
        return fileRealName;
    }

    public void setFileRealName(String fileRealName) {
        this.fileRealName = fileRealName;
    }
}
