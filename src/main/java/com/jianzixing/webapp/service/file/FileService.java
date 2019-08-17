package com.jianzixing.webapp.service.file;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface FileService {
    int SYNC_DISK_GROUP = 2;

    void addFile(ModelObject object) throws ModuleException;

    void deleteFile(int fid);

    List<String> uploadFiles(HttpServletRequest request);

    List<FileInfo> uploadFileMaps(HttpServletRequest request);

    ModelObject getFile(int fid);

    ModelObject getFileByName(String fileName);

    InputStream readFileByName(String fileName) throws FileNotFoundException;

    List<ModelObject> getFiles(int start, int limit);

    Paging getFiles(int gid, int start, int limit);

    List<ModelObject> getFileByMD5(String gid, String md5, String sha1);

    List<ModelObject> getFileGroups();

    List<ModelObject> getFlagFileGroups(String groupSourceType);

    void addFileGroup(ModelObject object) throws ModuleException;

    File getSyncDiskFile(int id);

    void deleteFileGroup(int id) throws ModuleException;

    void moveGroup(List<Integer> fid, int gid) throws ModuleException;

    boolean isSyncDisk(ModelObject object);

    void diskToDatabase();

    File getSystemFile(ModelObject realFile);
}
