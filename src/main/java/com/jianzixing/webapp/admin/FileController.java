package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.handler.AuthSkipCheck;
import com.jianzixing.webapp.handler.*;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.file.FileInfo;
import com.jianzixing.webapp.tables.file.TableFiles;
import org.mimosaframework.core.utils.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangankang
 */
@APIController
public class FileController {

    @Printer(name = "查看文件分组")
    public List<ModelObject> getFileGroups(String groupSourceType) {
        return GlobalService.fileService.getFlagFileGroups(groupSourceType);
    }

    @Printer(name = "添加文件分组")
    public ResponseMessage addFileGroup(ModelObject object) {
        try {
            GlobalService.fileService.addFileGroup(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除文件分组")
    public ResponseMessage deleteFileGroup(int id) {
        try {
            GlobalService.fileService.deleteFileGroup(id);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "查看分组文件")
    public ResponsePageMessage getFiles(int gid, int start, int limit) {
        return new ResponsePageMessage(GlobalService.fileService.getFiles(gid, start, limit));
    }

    @Printer(name = "移动文件分组")
    public ResponseMessage moveGroup(List<Integer> fid, int gid) {
        try {
            GlobalService.fileService.moveGroup(fid, gid);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer(name = "删除文件")
    public ResponseMessage deleteFiles(List<Integer> ids) {
        if (ids != null) {
            for (Integer id : ids) {
                if (id != null) {
                    GlobalService.fileService.deleteFile(id);
                }
            }
        }
        return new ResponseMessage();
    }

    @AuthSkipCheck
    @Printer(name = "更新文件")
    public ResponseMessage uploadFile(RequestFileWrapper wrapper) {
        List<ModelObject> files = new ArrayList<>();
        List<FileInfo> fileInfos = wrapper.getCacheFiles();
        if (fileInfos != null) {
            for (FileInfo fileInfo : fileInfos) {
                ModelObject file = new ModelObject();
                file.put("fileName", fileInfo.getFileName());
                file.put("fid", fileInfo.getFid());
                files.add(file);
            }
        }
        return new ResponseMessage(files);
    }

    @AuthSkipCheck
    @AdminSkipLoginCheck
    @Printer(name = "获取图片文件")
    public ResponseFileLogoWrapper writeImageFile(String f) {
        ModelObject object = GlobalService.fileService.getFileByName(f);
        if (object != null) {
            ResponseFileLogoWrapper fileLogoHandler = new ResponseFileLogoWrapper();
            fileLogoHandler.setValue(object);
            return fileLogoHandler;
        } else {
            return null;
        }
    }

    @AuthSkipCheck
    @Printer(name = "下载文件")
    public ResponseFileObjectWrapper download(int id) {
        ResponseFileObjectWrapper responseHandler = new ResponseFileObjectWrapper();
        responseHandler.setValue(GlobalService.fileService.getFile(id));
        return responseHandler;
    }

    @AuthSkipCheck
    @Printer(name = "获取文件URL")
    public ResponseMessage getFileHttpUrl(HttpServletRequest request, int fid) {
        ModelObject file = GlobalService.fileService.getFile(fid);
        if (StringUtils.isNotBlank(file.getString(TableFiles.uri))) {
            return new ResponseMessage(RequestUtils.getWebUrl(request) + file.getString(TableFiles.uri));
        } else {
            return new ResponseMessage(RequestUtils.getWebUrl(request) + "/web/webfile/download.action?f=" + file.getString(TableFiles.fileName));
        }
    }
}
