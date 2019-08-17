package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.spcard.TableShoppingCard;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@APIController
public class ShoppingCardController {

    @Printer
    public ResponsePageMessage getShoppingCards(int start, int limit) {
        Paging paging = GlobalService.shoppingCardService.getShoppingCards(null, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage addShoppingCard(ModelObject object) {
        try {
            GlobalService.shoppingCardService.addShoppingCard(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateShoppingCard(ModelObject object) {
        try {
            GlobalService.shoppingCardService.updateShoppingCard(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage declareShoppingCard(List<Integer> ids) {
        if (ids != null) {
            for (int id : ids) {
                GlobalService.shoppingCardService.declareShoppingCard(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage buildShoppingCard(int id, String password) {
        try {
            GlobalService.shoppingCardService.createShoppingCardList(id, password);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getShoppingCardList(ModelObject search, int id, int start, int limit) {
        Paging paging = GlobalService.shoppingCardService.getShoppingCardLists(search, id, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage declareShoppingCardList(int id, List<String> numbers) {
        GlobalService.shoppingCardService.declareShoppingCardList(id, numbers);
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getShoppingCardSpending(int id, String cardNumber) {
        List<ModelObject> objects = GlobalService.shoppingCardService.getShoppingCardSpending(id, cardNumber);
        return new ResponseMessage(objects);
    }

    @Printer
    public void exportShoppingCardList(HttpServletRequest request,
                                       HttpServletResponse response,
                                       int id, String password) {
        ModelObject object = GlobalService.shoppingCardService.getShoppingCardById(id);
        if (object == null) {
            try {
                response.getWriter().print("购物卡批次不存在");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        OutputStream outputStream = null;
        try {
            response.reset();
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Type", "application/octet-stream");
            String fileName = object.getString(TableShoppingCard.name);
            //获得浏览器信息并转换为大写
            String agent = request.getHeader("User-Agent").toUpperCase();
            //IE浏览器和Edge浏览器
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO") > 0 && agent.indexOf("RV:11") > 0)) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //其他浏览器
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }
            response.setHeader("Content-Disposition", "attachment;fileName="
                    + fileName
                    + "&"
                    + object.getString(TableShoppingCard.number)
                    + ".xlsx");
            outputStream = response.getOutputStream();
            GlobalService.shoppingCardService.exportShoppingCardList(id, password, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Content-Disposition", "");
            try {
                if (outputStream != null) {
                    outputStream.write(e.getMessage() != null ? e.getMessage().getBytes() : e.getClass().getSimpleName().getBytes());
                } else {
                    response.getWriter().print(e.getMessage());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
