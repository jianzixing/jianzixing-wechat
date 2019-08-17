package com.jianzixing.webapp.service.wechatsm;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface WeChatMaterialService {
    Paging getTemporaryMaterials(ModelObject search, String type, int accountId, int openType, int start, int limit);

    Paging getForeverMaterials(ModelObject search, String type, int accountId, int openType, int start, int limit) throws IOException, ModuleException;

    void deleteForeverMaterials(int accountId, int openType, List<String> mediaIds) throws IOException, ModuleException;

    void addImageText(String host, ModelObject object) throws ModelCheckerException, IOException, ModuleException, TransactionException;

    Paging getImageTexts(int openType, int accountId, int start, int limit);

    void delImageText(int id) throws IOException, ModuleException;

    void updateImageText(String host, ModelObject object) throws ModelCheckerException, IOException, ModuleException, TransactionException;

    ModelObject getImageText(int id);

    ModelObject getImageTextSub(String id, String index);

    String uploadImageWeChat(int openType, int accountId, String fileName) throws IOException, ModuleException;

    ModelObject uploadMaterialByFile(int openType, int accountId, String fileName) throws ModuleException, IOException;
}
