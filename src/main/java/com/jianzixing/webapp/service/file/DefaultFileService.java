package com.jianzixing.webapp.service.file;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.file.TableFileGroup;
import com.jianzixing.webapp.tables.file.TableFiles;
import org.mimosaframework.core.utils.RequestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.FileUniquenessUtils;
import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class DefaultFileService implements FileService {
    private static final Log logger = LogFactory.getLog(DefaultFileService.class);

    @Autowired
    private SessionTemplate sessionTemplate;

    @Override
    public void addFile(ModelObject object) throws ModuleException {
        object.setObjectClass(TableFiles.class);

        try {
            object.checkAndThrowable();
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }
        object.put(TableFiles.createTime, new Date());
        sessionTemplate.save(object);
    }

    @Override
    public void deleteFile(int fid) {
        ModelObject object = sessionTemplate.get(TableFiles.class, fid);
        String path = object.getString(TableFiles.path);
        if (path.startsWith("/upload")) {
            path = RequestUtils.getWebAppRoot() + File.separator + path;
        }
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        sessionTemplate.delete(TableFiles.class, fid);
    }

    private ConfigManager getConfigManager(HttpServletRequest request) {
        ConfigManager configManager = null;
        try {
            configManager = new ConfigManager(request, "properties/upload.json");
        } catch (IOException e) {
        }
        return configManager;
    }

    public List<String> uploadFiles(HttpServletRequest request) {
        List<String> list = new ArrayList<>();
        List<FileInfo> fileInfos = this.uploadFileMaps(request);
        if (fileInfos != null) {
            for (FileInfo fileInfo : fileInfos) {
                list.add(fileInfo.getFileName());
            }
        }
        return list;
    }

    public List<FileInfo> uploadFileMaps(HttpServletRequest request) {
        CommonsMultipartResolver coMultipartResolver = new CommonsMultipartResolver(request.getSession()
                .getServletContext());
        List<FileInfo> fileInfos = new ArrayList<>();
        List<ModelObject> objects = new ArrayList<>();
        if (coMultipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            ConfigManager configManager = this.getConfigManager(multiRequest);

            String gid = request.getParameter("gid");
            String canDownload = request.getParameter("canDownload");
            if (gid == null || !NumberUtils.isNumber(gid)) gid = "0";
            File isSyncDiskFile = GlobalService.fileService.getSyncDiskFile(Integer.parseInt(gid));
            File webappRootFile = new File(RequestUtils.getWebAppRoot());


            Map<String, MultipartFile> multipartFileMap = multiRequest.getFileMap();
            if (multipartFileMap != null) {
                Iterator<Map.Entry<String, MultipartFile>> iterator = multipartFileMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, MultipartFile> entry = iterator.next();
                    MultipartFile multipartFile = entry.getValue();
                    String formFileName = entry.getKey();
                    if (!multipartFile.isEmpty()) {
                        String fileRealName = multipartFile.getOriginalFilename();


                        if (StringUtils.isNotBlank(fileRealName)) {
                            FileInfo fileInfo = new FileInfo();
                            String fileType = fileRealName.substring(fileRealName.lastIndexOf(".") + 1);
                            fileType = fileType.toLowerCase();

                            if (!configManager.isAllowUpload(fileType)) {
                                fileInfo.setAllowFile(false);
                                break;
                            }

                            String diskFileName = null;
                            if (isSyncDiskFile == null) {
                                diskFileName = RandomUtils.uuid() + "." + fileType;
                            } else {
                                diskFileName = formFileName;
                            }
                            String physicalPathRoot = configManager.getConfigFileRootPath();
                            String physicalPath = configManager.getFilePath(diskFileName);
                            File localFile = null;
                            if (isSyncDiskFile == null) {
                                localFile = new File(physicalPath);
                            } else {
                                localFile = new File(isSyncDiskFile.getPath() + File.separator + diskFileName);
                            }
                            try {
                                if (!localFile.getParentFile().exists()) {
                                    boolean is = localFile.getParentFile().mkdirs();
                                    if (!is) {

                                    }
                                }
                                multipartFile.transferTo(localFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    multipartFile.getInputStream().close();
                                } catch (Exception e) {
                                }
                            }
                            try {
                                long md5Time = System.currentTimeMillis();
                                String md5 = FileUniquenessUtils.getMD5(localFile);
                                String sha1 = FileUniquenessUtils.getSha1(localFile);

                                if (md5 != null && sha1 != null) {
                                    System.out.println("文件MD5的时间是(" + (double) localFile.length() / 1024 + ") : " + (System.currentTimeMillis() - md5Time) / 1000d);
                                    List<ModelObject> files = GlobalService.fileService.getFileByMD5(gid, md5, sha1);
                                    if (files != null && files.size() > 0) {
                                        if (localFile.exists()) localFile.delete();
                                        fileInfo.setExistFile(true);
                                        fileInfo.setFileName(files.get(0).getString(TableFiles.fileName));
                                        fileInfo.setFile(new File(files.get(0).getString(TableFiles.path)));
                                        fileInfo.setFid(files.get(0).getIntValue(TableFiles.id));

                                        if (canDownload != null && !canDownload.equals(files.get(0).getString(TableFiles.isDwn))) {
                                            ModelObject update = new ModelObject();
                                            update.put(TableFiles.id, fileInfo.getFid());
                                            update.put(TableFiles.isDwn, canDownload.equals("0") ? 0 : 1);
                                            sessionTemplate.update(update);
                                        }
                                    } else {
                                        String uri = null;
                                        if (localFile.getPath().startsWith(webappRootFile.getPath())) {
                                            uri = localFile.getPath().replace(webappRootFile.getPath(), "");
                                            uri = uri.replaceAll("\\\\", "/");
                                        }
                                        String localFilePath = localFile.getPath();
                                        ModelObject object = new ModelObject();
                                        if (localFilePath.startsWith(physicalPathRoot)) {
                                            object.put(TableFiles.path, localFilePath.substring(physicalPathRoot.length()));
                                            object.put(TableFiles.isRelativePath, 2);
                                        } else {
                                            object.put(TableFiles.path, localFilePath);
                                        }
                                        object.put(TableFiles.uri, uri);
                                        object.put(TableFiles.originalName, fileRealName);
                                        object.put(TableFiles.fileName, diskFileName);
                                        object.put(TableFiles.type, fileType);
                                        object.put(TableFiles.md5, md5);
                                        object.put(TableFiles.sha1, sha1);
                                        object.put(TableFiles.size, localFile.length());
                                        if (canDownload != null && canDownload.equals("0")) {
                                            object.put(TableFiles.isDwn, 0);
                                        }
                                        if (StringUtils.isNotBlank(gid)) {
                                            object.put(TableFiles.gid, gid);
                                        }
                                        GlobalService.fileService.addFile(object);
                                        fileInfo.setFid(object.getIntValue(TableFiles.id));

                                        fileInfo.setFile(localFile);
                                        fileInfo.setFileName(diskFileName);
                                        fileInfo.setFileRealName(fileRealName);
                                    }
                                    fileInfos.add(fileInfo);
                                }
                            } catch (ModuleException e) {
                                e.printStackTrace();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        if (objects != null && objects.size() > 0) {
            sessionTemplate.save(objects);
        }
        return fileInfos;
    }

    @Override
    public ModelObject getFile(int fid) {
        return sessionTemplate.get(TableFiles.class, fid);
    }

    @Override
    public ModelObject getFileByName(String fileName) {
        return sessionTemplate.get(Criteria.query(TableFiles.class).eq(TableFiles.fileName, fileName));
    }

    public InputStream readFileByName(String fileName) throws FileNotFoundException {
        ModelObject object = this.getFileByName(fileName);
        if (object != null) {
            int isRelativePath = object.getIntValue(TableFiles.isRelativePath);
            String path = null;
            path = object.getString(TableFiles.path);
            if (isRelativePath == 1) {
                path = RequestUtils.getWebAppRoot() + File.separator + path;
            }
            File file = new File(path);
            if (file.exists()) {
                return new FileInputStream(file);
            }
        }
        return null;
    }

    @Override
    public List<ModelObject> getFiles(int start, int limit) {
        return sessionTemplate.query(TableFiles.class).limit().limit(start, limit).goQuery().queries();
    }

    @Override
    public Paging getFiles(int gid, int start, int limit) {
        List<Integer> ids = new ArrayList<>();
        ids.add(gid);
        List<ModelObject> objects = getObjects(gid);
        if (objects != null) {
            objects.stream().forEach(o -> ids.add(o.getIntValue(TableFileGroup.id)));
        }
        return sessionTemplate.query(TableFiles.class)
                .in(TableFiles.gid, ids)
                .eq(TableFiles.isDwn, 1)
                .order(TableFiles.createTime, false)
                .limit().limit(start, limit).goQuery().paging();
    }

    @Override
    public List<ModelObject> getFileByMD5(String gid, String md5, String sha1) {
        List<ModelObject> files = null;
        if (StringUtils.isNotBlank(gid)) {
            files = sessionTemplate.list(Criteria.query(TableFiles.class)
                    .addFilter().eq(TableFiles.md5, md5).query()
                    .eq(TableFiles.gid, gid)
                    .addFilter().eq(TableFiles.sha1, sha1).query());
        } else {
            files = sessionTemplate.list(Criteria.query(TableFiles.class)
                    .addFilter().eq(TableFiles.md5, md5).query()
                    .addFilter().eq(TableFiles.sha1, sha1).query());
        }
        if (files != null) {
            List<ModelObject> delete = new ArrayList<>();
            for (ModelObject file : files) {
                String path = file.getString(TableFiles.path);
                File f = new File(path);
                if (!f.exists()) {
                    f.delete();
                    delete.add(file);
                }
            }
            files.removeAll(delete);
        }
        return files;
    }

    @Override
    public List<ModelObject> getFileGroups() {
        List<ModelObject> objects = sessionTemplate.query(TableFileGroup.class)
                .queries();
        return ModelUtils.getListToTree(objects, TableFileGroup.id, TableFileGroup.pid, "children");
    }

    @Override
    public List<ModelObject> getFlagFileGroups(String groupSourceType) {

        List<ModelObject> objects = null;
        int type = 0;
        if (groupSourceType != null && groupSourceType.equals("1")) type = 1;
        if (groupSourceType != null && groupSourceType.equals("2")) type = 2;
        if (StringUtils.isNotBlank(groupSourceType)) {
            objects = sessionTemplate.query(TableFileGroup.class).eq(TableFileGroup.groupSourceType, type).queries();
        } else {
            objects = sessionTemplate.query(TableFileGroup.class).queries();
        }
        return ModelUtils.getListToTree(objects, TableFileGroup.id, TableFileGroup.pid, "children");
    }

    @Override
    public void addFileGroup(ModelObject object) throws ModuleException {
        object.setObjectClass(TableFileGroup.class);

        try {
            object.checkAndThrowable();
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }

        int pid = object.getIntValue(TableFileGroup.pid);
        if (isParentSyncDisk(pid)) {
            object.put(TableFileGroup.groupSourceType, 1);
        } else {
            object.put(TableFileGroup.groupSourceType, 0);
        }

        String groupName = object.getString(TableFileGroup.groupName);
        // char[] chars = groupName.toCharArray();
        // for (char c : chars) {
        //     if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z') && c != '-' && c != '_') {
        //         throw new ModuleException(StockCode.ARG_VALID, "文件夹名称只能包含(a-z)和(A-Z)或者(-)(_)字符");
        //     }
        // }
        if ((groupName.equalsIgnoreCase("page")
                || groupName.equalsIgnoreCase("admin")
                || groupName.equalsIgnoreCase("web")
                || groupName.equalsIgnoreCase("WEB-INF"))
                && object.getIntValue(TableFileGroup.pid) == SYNC_DISK_GROUP) {
            throw new ModuleException(StockCode.ARG_VALID, "不能使用page,admin,web,WEB-INF作为系统文件下目录");
        }

        ModelObject exist = sessionTemplate.get(
                Criteria.query(TableFileGroup.class)
                        .eq(TableFileGroup.pid, pid)
                        .eq(TableFileGroup.groupName, object.getString(TableFileGroup.groupName)));
        if (exist != null) {
            throw new ModuleException(StockCode.EXIST_OBJ, "当前目录下已经存在相同名称文件夹");
        }

        if (pid == SYNC_DISK_GROUP) {
            String webRoot = RequestUtils.getWebAppRoot();
            File file = new File(webRoot);
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().equalsIgnoreCase(object.getString(TableFileGroup.groupName))) {
                        throw new ModuleException(StockCode.EXIST_DIR, "分组名称文件目录已经(硬盘)存在");
                    }
                }
            }
        }

        sessionTemplate.save(object);

        startSyncDisk();
    }

    private boolean isParentSyncDisk(int pid) {
        ModelObject object = sessionTemplate.get(TableFileGroup.class, pid);
        if (object != null) {
            int ppid = object.getIntValue(TableFileGroup.pid);
            if (object.getIntValue(TableFileGroup.groupSourceType) == 1) {
                return true;
            } else {
                return isParentSyncDisk(ppid);
            }
        }
        return false;
    }

    private void startSyncDisk() throws ModuleException {
        String webRoot = RequestUtils.getWebAppRoot();
        File file = new File(webRoot);
        this.startSyncDisk(0, file);
    }

    private void startSyncDisk(int pid, File file) throws ModuleException {
        List<ModelObject> objects = sessionTemplate.list(Criteria.query(TableFileGroup.class).eq(TableFileGroup.pid, pid));
        if (objects != null) {
            for (ModelObject object : objects) {
                int groupSourceType = object.getIntValue(TableFileGroup.groupSourceType);
                if (groupSourceType == 1) {
                    String name = object.getString(TableFileGroup.groupName);
                    int cpid = object.getIntValue(TableFileGroup.pid);
                    int id = object.getIntValue(TableFileGroup.id);
                    if (cpid != 0) {
                        File dist = new File(file.getPath() + File.separator + name);
                        if (!dist.exists()) {
                            boolean succ = dist.mkdirs();
                            if (!succ) {
                                throw new ModuleException(StockCode.CREATE_FOLDER_FAIL, "创建文件夹失败");
                            }
                        }
                        this.startSyncDisk(id, dist);
                    } else {
                        this.startSyncDisk(id, file);
                    }
                }
            }
        }
    }

    public File getSyncDiskFile(int id) {
        ModelObject p = sessionTemplate.get(TableFileGroup.class, id);
        if (p != null) {
            int groupSourceType = p.getIntValue(TableFileGroup.groupSourceType);
            if (groupSourceType == 1) {
                List<ModelObject> objects = getParents(id);
                Collections.reverse(objects);
                String webRoot = RequestUtils.getWebAppRoot();
                if (objects != null) {
                    String f = "";
                    for (ModelObject o : objects) {
                        if (o != null) {
                            if (o.getIntValue(TableFileGroup.pid) != 0) {
                                f = f + File.separator + o.getString(TableFileGroup.groupName);
                            }
                        }
                    }
                    return new File(webRoot + File.separator + f);
                }
            }
        }
        return null;
    }

    @Override
    public void deleteFileGroup(int id) throws ModuleException {
        List<Integer> ids = new ArrayList<>();
        ids.add(id);
        List<ModelObject> objects = getObjects(id);
        if (objects != null) {
            for (ModelObject object : objects) {
                ids.add(object.getIntValue(TableFileGroup.id));
            }
        }
        File file = this.getSyncDiskFile(id);
        if (file != null && file.exists()) {
            try {
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                } else {
                    FileUtils.deleteQuietly(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new ModuleException(StockCode.DELETE_FILE_FAIL, "删除文件夹失败");
            }
        }

        sessionTemplate.delete(TableFileGroup.class)
                .in(TableFileGroup.id, ids).delete();

        sessionTemplate.update(TableFiles.class)
                .in(TableFiles.gid, ids)
                .value(TableFiles.gid, 0)
                .update();
    }

    private List<ModelObject> getParents(int id) {
        ModelObject p = sessionTemplate.get(TableFileGroup.class, id);
        List<ModelObject> ps = null;
        if (p != null) {
            int pid = p.getIntValue(TableFileGroup.pid);
            ps = getParents(pid);
        }
        if (ps == null) {
            ps = new ArrayList<>();
            ps.add(p);
        } else {
            List<ModelObject> n = new ArrayList<>();
            n.add(p);
            n.addAll(ps);
            return n;
        }

        return ps;
    }

    private List<ModelObject> getObjects(int pid) {
        List<ModelObject> objects = sessionTemplate.query(TableFileGroup.class)
                .eq(TableFileGroup.pid, pid)
                .queries();

        List<ModelObject> childList = new ArrayList<>();
        if (objects == null) {
            return objects;
        }
        for (ModelObject object : objects) {
            int id = object.getIntValue(TableFileGroup.id);
            List<ModelObject> children = getObjects(id);
            if (children != null) {
                childList.addAll(children);
            }
        }

        objects.addAll(childList);

        return objects;
    }

    private void updateFileGroup(ModelObject object) throws ModuleException {
        int id = object.getIntValue(TableFileGroup.id);
        object.setObjectClass(TableFileGroup.class);

        try {
            object.checkUpdateThrowable(TableFileGroup.pid);
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }
        File file = this.getSyncDiskFile(id);
        if (file != null && file.exists()) {
            boolean succ = file.renameTo(new File(file.getParentFile().getPath() + File.separator + object.getString(TableFileGroup.groupName)));
            if (!succ) {
                throw new ModuleException(StockCode.RENAME_FILE_FAIL, "重命名文件夹失败");
            }
        }
        sessionTemplate.update(object);
    }

    @Override
    public void moveGroup(List<Integer> fid, int gid) throws ModuleException {
        File file = this.getSyncDiskFile(gid);
        if (file != null && file.exists()) {
            List<ModelObject> files = sessionTemplate.list(Criteria.query(TableFiles.class).in(TableFiles.id, fid));
            if (files != null) {
                for (ModelObject object : files) {
                    String path = object.getString(TableFiles.path);
                    try {
                        FileUtils.moveFileToDirectory(new File(path), file, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new ModuleException(StockCode.COPY_FILE_FAIL, "拷贝文件到文件夹失败");
                    }
                }
            }
        }
        sessionTemplate.update(TableFiles.class)
                .in(TableFiles.id, fid)
                .value(TableFiles.gid, gid)
                .update();
    }

    @Override
    public boolean isSyncDisk(ModelObject object) {
        int gid = object.getIntValue(TableFiles.gid);
        ModelObject group = sessionTemplate.get(TableFileGroup.class, gid);
        if (group != null) {
            return group.getIntValue(TableFileGroup.groupSourceType) == 1;
        }
        return false;
    }

    @Override
    public void diskToDatabase() {
        try {
            String webRoot = RequestUtils.getWebAppRoot();
            if (webRoot != null) {
                List<ModelObject> topGroups = sessionTemplate.list(
                        Criteria.query(TableFileGroup.class)
                                .eq(TableFileGroup.groupSourceType, 1)
                                .eq(TableFileGroup.pid, 0));
                if (topGroups != null) {
                    for (ModelObject topGroup : topGroups) {
                        List<ModelObject> objects = sessionTemplate.list(Criteria.query(TableFileGroup.class).eq(TableFileGroup.pid, topGroup.getIntValue(TableFileGroup.id)));
                        if (objects != null) {
                            for (ModelObject object : objects) {
                                String groupName = object.getString(TableFileGroup.groupName);
                                int gid = object.getIntValue(TableFileGroup.id);
                                if (gid != 0) {
                                    File file = new File(webRoot + File.separator + groupName);
                                    if (!file.exists()) {
                                        file.mkdirs();
                                    } else {
                                        this.diskToDatabase(gid, file);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("同步磁盘文件到数据库出错", e);
        }
    }

    @Override
    public File getSystemFile(ModelObject realFile) {
        int isAF = realFile.getIntValue(TableFiles.isRelativePath);
        String path = realFile.getString(TableFiles.path);
        if (isAF == 0) {
            return new File(path);
        }
        if (isAF == 1) {
            return RequestUtils.getWebAppRoot(path);
        }
        if (isAF == 2) {
            ConfigManager configManager = this.getConfigManager(null);
            return new File(configManager.getConfigFileRootPath() + path);
        }
        return null;
    }

    private void diskToDatabase(int gid, File file) throws IOException, NoSuchAlgorithmException {
        File webappRootFile = new File(RequestUtils.getWebAppRoot());
        if (file != null && file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        String md5 = FileUniquenessUtils.getMD5(f);
                        String sha1 = FileUniquenessUtils.getSha1(f);
                        ModelObject old = sessionTemplate.get(Criteria.query(TableFiles.class).eq(TableFiles.gid, gid).eq(TableFiles.fileName, f.getName()));
                        if (old == null || !old.getString(TableFiles.md5).equals(md5)
                                || !old.getString(TableFiles.sha1).equals(sha1)) {

                            String uri = null;
                            if (f.getPath().startsWith(webappRootFile.getPath())) {
                                uri = f.getPath().replace(webappRootFile.getPath(), "");
                                uri = uri.replaceAll("\\\\", "/");
                            }
                            ModelObject object = new ModelObject(TableFiles.class);
                            if (old != null) {
                                old.put(TableFiles.id, old.getIntValue(TableFiles.id));
                            }
                            object.put(TableFiles.gid, gid);
                            object.put(TableFiles.path, f.getPath());
                            if (uri != null) {
                                object.put(TableFiles.uri, uri);
                            }
                            object.put(TableFiles.fileName, f.getName());
                            object.put(TableFiles.originalName, f.getName());
                            String suffix = f.getName().substring(f.getName().lastIndexOf(".") + 1);
                            object.put(TableFiles.type, suffix);
                            object.put(TableFiles.size, f.length());
                            object.put(TableFiles.md5, md5);
                            object.put(TableFiles.sha1, sha1);
                            object.put(TableFiles.createTime, new Date());
                            sessionTemplate.saveAndUpdate(object);
                        }
                    }
                    if (f.isDirectory()) {
                        ModelObject old = sessionTemplate.get(Criteria.query(TableFileGroup.class).eq(TableFileGroup.pid, gid).eq(TableFileGroup.groupName, f.getName()));
                        int pid = 0;
                        if (old == null) {
                            ModelObject object = new ModelObject(TableFileGroup.class);
                            object.put(TableFileGroup.pid, gid);
                            object.put(TableFileGroup.groupName, f.getName());
                            object.put(TableFileGroup.expanded, 1);
                            object.put(TableFileGroup.leaf, 0);
                            object.put(TableFileGroup.groupSourceType, 1);
                            sessionTemplate.save(object);
                            pid = object.getIntValue(TableFileGroup.id);
                        } else {
                            pid = old.getIntValue(TableFileGroup.id);
                        }
                        this.diskToDatabase(pid, f);
                    }
                }
            }
        }
    }
}
