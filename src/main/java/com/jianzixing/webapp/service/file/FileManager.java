package com.jianzixing.webapp.service.file;

import org.mimosaframework.core.json.ModelObject;

import java.io.InputStream;
import java.util.List;

public interface FileManager {

    void addGroup(ModelObject group);

    void deleteGroup(String gid);

    void updateGroup(ModelObject object);

    List<ModelObject> getGroups(String gid);

    void upload(InputStream inputStream);

    void deleteFile(String fid);

    List<ModelObject> getFields(String gid, long start, long limit);

    void moveGroup(List<Integer> fid, String gid);
}
