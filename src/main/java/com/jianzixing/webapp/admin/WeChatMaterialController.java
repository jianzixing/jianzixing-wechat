package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.tables.wechat.TableWeChatImageText;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.utils.RequestUtils;
import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.handler.AuthSkipCheck;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.system.TableAdmin;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.apache.commons.fileupload.FileItem;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@APIController
public class WeChatMaterialController {

    @Printer
    public ResponsePageMessage getTemporaryMaterials(ModelObject search,
                                                     String type,
                                                     int accountId,
                                                     int openType,
                                                     int start,
                                                     int limit) {
        Paging paging = GlobalService.weChatMaterialService.getTemporaryMaterials
                (search, type, accountId, openType, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponsePageMessage getForeverMaterials(ModelObject search,
                                                   String type,
                                                   int accountId,
                                                   int openType,
                                                   int start,
                                                   int limit) {
        Paging paging = null;
        try {
            paging = GlobalService.weChatMaterialService.getForeverMaterials
                    (search, type, accountId, openType, start, limit);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ModuleException e) {
            e.printStackTrace();
        }
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage deleteForeverMaterials(int accountId, int openType, List<String> mediaIds) {
        try {
            GlobalService.weChatMaterialService.deleteForeverMaterials(accountId, openType, mediaIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    /**
     * 如果素材图片被防盗链可以用这个地址作为跳板机
     *
     * @param response
     * @param url
     * @throws IOException
     */
    @RequestMapping("/admin/WeChatMaterial/loadImage")
    @AuthSkipCheck
    public void loadMaterialImage(HttpServletResponse response,
                                  String url) throws IOException {
        HttpUtils.download(url, response.getOutputStream());
    }

    /**
     * 将本地文件上传到微信服务器，返回资源id
     *
     * @param openType
     * @param accountId
     * @param fileName
     * @return
     */
    @Printer
    public ResponseMessage uploadMaterial(int openType, int accountId, String fileName) {
        try {
            ModelObject object = GlobalService.weChatMaterialService.uploadMaterialByFile(openType, accountId, fileName);
            return new ResponseMessage(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
    }

    @Printer
    public ResponseMessage addImageText(HttpServletRequest request, ModelObject object) {
        try {
            GlobalService.weChatMaterialService.addImageText(RequestUtils.getWebUrl(request), object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getImageTexts(int openType, int accountId, int start, int limit) {
        Paging paging = GlobalService.weChatMaterialService.getImageTexts(openType, accountId, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage delImageText(List<Integer> ids) {
        if (ids != null) {
            for (Integer id : ids) {
                try {
                    GlobalService.weChatMaterialService.delImageText(id);
                } catch (Exception e) {
                    return new ResponseMessage(e);
                }
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateImageText(HttpServletRequest request, ModelObject object) {
        try {
            GlobalService.weChatMaterialService.updateImageText(RequestUtils.getWebUrl(request), object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage uploadImageWeChat(int openType, int accountId, String fileName) {
        try {
            String url = GlobalService.weChatMaterialService.uploadImageWeChat(openType, accountId, fileName);
            return new ResponseMessage(url);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
    }
}
