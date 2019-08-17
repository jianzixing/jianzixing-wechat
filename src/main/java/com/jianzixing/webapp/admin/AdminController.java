package com.jianzixing.webapp.admin;

import com.google.zxing.WriterException;
import com.jianzixing.webapp.handler.AdminSkipLoginCheck;
import com.jianzixing.webapp.handler.RequestAdminWrapper;
import com.jianzixing.webapp.handler.AuthSkipCheck;
import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.SystemConfig;
import com.jianzixing.webapp.service.goods.GoodsStatus;
import com.jianzixing.webapp.service.order.OrderStatus;
import com.jianzixing.webapp.tables.statistics.TableStatisticsHour;
import com.jianzixing.webapp.tables.system.TableAdmin;
import org.mimosaframework.core.utils.QRCodeUtils;
import org.mimosaframework.core.utils.RequestUtils;
import com.jianzixing.webapp.web.ValCodeController;
import com.jianzixing.webapp.handler.ResponseFileWrapper;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author yangankang
 */
@APIController
public class AdminController {
    private static final Log logger = LogFactory.getLog(AdminController.class);
    public static final String LOGIN_SESS_STR = "session_login_obj";

    public static final ModelObject getLoginUer(HttpServletRequest request) {
        return (ModelObject) request.getSession().getAttribute(LOGIN_SESS_STR);
    }

    public static final boolean isSuperUser(HttpServletRequest request) {
        ModelObject object = getLoginUer(request);
        return object.getString(TableAdmin.userName).equals("admin");
    }

    @AuthSkipCheck
    @AdminSkipLoginCheck
    @RequestMapping("/admin/index")
    public String toAdminIndexPage(HttpServletRequest request) {
        ModelObject object = (ModelObject) request.getSession().getAttribute(LOGIN_SESS_STR);
        if (object == null) {
            return "redirect:/admin/login.jhtml";
        }
        request.setAttribute("user", object);
        return "admin/index";
    }

    @AuthSkipCheck
    @AdminSkipLoginCheck
    @RequestMapping("/admin/login")
    public String toAdminLoginPage() {
        return "admin/login";
    }

    @Printer
    @AuthSkipCheck
    @AdminSkipLoginCheck
    public String loginOut(HttpServletRequest request) {
        request.getSession().removeAttribute(LOGIN_SESS_STR);
        return "ok";
    }

    @Printer
    @AdminSkipLoginCheck
    public ResponseMessage login(HttpServletRequest request,
                                 String userName, String password, String code) {
        logger.info("尝试登陆后台: IP:" + RequestUtils.getIpAddr(request) + " userName:" + userName);
        Boolean isVal = (Boolean) request.getSession().getAttribute(ValCodeController.SUCCESS_VAL);

        if (SystemConfig.isDebug) {
            isVal = true;
            userName = "admin";
            password = "123456";
        }

        if (isVal == null || isVal == false) {
            return new ResponseMessage(-130, "验证码输入错误");
        }

        ModelObject modelObject = GlobalService.adminService.getAdminByUserName(userName);
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            return new ResponseMessage(-120, "用户名和密码不能为空");
        }
        if (modelObject == null) {
            return new ResponseMessage(-100, "用户不存在");
        }
        if (!modelObject.getString(TableAdmin.password).toUpperCase()
                .equals(GlobalService.adminService.getPasswordString(password))) {
            return new ResponseMessage(-110, "密码错误");
        }
        request.getSession().setAttribute(LOGIN_SESS_STR, modelObject);

        try {
            String ip = RequestUtils.getIpAddr(request);
            GlobalService.adminService.updateLoginInfo(modelObject.getIntValue(TableAdmin.id), ip, new Date());
            request.getSession().removeAttribute(ValCodeController.SUCCESS_VAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseMessage();
    }

    @AuthSkipCheck
    @Printer
    public ResponseMessage editSelfPassword(HttpServletRequest request, String oldPassword, String newPassword) {
        ModelObject user = getLoginUer(request);
        if (user.getString(TableAdmin.password).toUpperCase().equals(GlobalService.adminService.getPasswordString(oldPassword))) {
            GlobalService.adminService.updateAdminPassword(user.getIntValue(TableAdmin.id), newPassword);
        } else {
            return new ResponseMessage(-110, "原密码输入错误");
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponsePageMessage getAdmins(int start, int limit, ModelObject search) {
        Paging objects = GlobalService.adminService.getAdmins(start, limit, search);

        return new ResponsePageMessage(objects.getCount(), objects.getObjects());
    }

    @Printer
    public ResponseMessage addAdmin(ModelObject object) {
        try {
            GlobalService.adminService.addAdmin(object);
        } catch (ModuleException e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteAdmin(List<Integer> ids) {
        try {
            for (int id : ids) {
                GlobalService.adminService.deleteAdmin(id);
            }
        } catch (ModuleException e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateAdmin(HttpServletRequest request, ModelObject object) {
        ModelObject user = getLoginUer(request);
        try {
            GlobalService.adminService.updateAdmin(object);
        } catch (ModuleException e) {
            return new ResponseMessage(e);
        }
        try {
            GlobalService.adminService.updateEditUser(user.getIntValue(TableAdmin.id), object.getIntValue(TableAdmin.id), new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updatePassword(HttpServletRequest request, int id, String loginPassword, String password) {
        ModelObject user = getLoginUer(request);
        if (GlobalService.adminService.checkAdminPassword(user.getIntValue(TableAdmin.id), loginPassword)) {
            GlobalService.adminService.updateAdminPassword(id, password);
            try {
                GlobalService.adminService.updateEditUser(user.getIntValue(TableAdmin.id), id, new Date());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return new ResponseMessage(-100, "管理员密码不正确");
        }
        return new ResponseMessage();
    }

    @Printer
    @AuthSkipCheck
    public ResponseMessage getTimerTaskData(RequestAdminWrapper wrapper) {
        int adminId = wrapper.getId();
        ModelObject object = new ModelObject();

        try {
            ModelObject message = GlobalService.systemMessageService.getNotShows(adminId, 5);
            object.put("message", message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ModelObject todayViews = new ModelObject();
        try {
            todayViews = GlobalService.statisticsService.getTodayStatistics("*");
            List<ModelObject> todayHoursViews = GlobalService.statisticsService.getHoursStatistics(24, "*");
            object.put("todayViews", todayViews);
            if (todayHoursViews != null) {
                for (ModelObject o : todayHoursViews) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
                    o.put("time", format.parse(o.getString(TableStatisticsHour.hourTime)).getTime());
                }
                object.put("todayHoursViews", todayHoursViews);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (todayViews == null) todayViews = new ModelObject();
        long userCount = GlobalService.userService.getUserCount();
        long goodsCount = GlobalService.goodsService.getGoodsCount(GoodsStatus.UP);
        long orderCount = GlobalService.orderService.getOrderCount(OrderStatus.PAY);
        todayViews.put("userCount", userCount);
        todayViews.put("goodsCount", goodsCount);
        todayViews.put("orderCount", orderCount);

        return new ResponseMessage(object);
    }

    @Printer
    @AuthSkipCheck
    public ResponseMessage getLoginAdmin(RequestAdminWrapper wrapper) {
        int id = wrapper.getId();
        ModelObject admin = GlobalService.adminService.getAdmin(id);
        return new ResponseMessage(admin);
    }

    @Printer
    @AuthSkipCheck
    public ResponseMessage updateLoginAdmin(RequestAdminWrapper wrapper, ModelObject object) {
        object.clearEmpty();
        int id = wrapper.getId();
        object.put(TableAdmin.id, id);
        object.put(TableAdmin.userName, "hello");
        object.retain(TableAdmin.id, TableAdmin.realName, TableAdmin.phoneNumber, TableAdmin.logo,
                TableAdmin.extension, TableAdmin.gender, TableAdmin.birthday, TableAdmin.homeNumber,
                TableAdmin.email, TableAdmin.education, TableAdmin.description);
        try {
            GlobalService.adminService.updateAdmin(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    @AuthSkipCheck
    public ResponseFileWrapper getFiles(String name) {
        if (name.equalsIgnoreCase("discount")) {
            name = "discount.xlsx";
        }
        InputStream stream = this.getClass().getResourceAsStream("/files/" + name);
        if (stream != null) {
            return new ResponseFileWrapper(stream, name);
        }
        return null;
    }


    @RequestMapping("/create/qrcode")
    public void createQRCode(HttpServletResponse response, String str) {
        response.setDateHeader("Expires", 0L);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/png");

        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            QRCodeUtils.createQrCode(outputStream, str, 300, "png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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


    /***部门***/
    @Printer
    public ResponseMessage addDepartment(ModelObject object) {
        try {
            GlobalService.adminService.addDepartment(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteDepartment(int id) {
        GlobalService.adminService.deleteDepartment(id);
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateDepartment(ModelObject object) {
        try {
            GlobalService.adminService.updateDepartment(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getDepartment() {
        List<ModelObject> objects = GlobalService.adminService.getDepartment();
        return new ResponseMessage(objects);
    }

    /***职位***/
    @Printer
    public ResponseMessage addPosition(ModelObject object) {
        try {
            GlobalService.adminService.addPosition(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deletePosition(int id) {
        GlobalService.adminService.deletePosition(id);
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updatePosition(ModelObject object) {
        try {
            GlobalService.adminService.updatePosition(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getPosition() {
        List<ModelObject> objects = GlobalService.adminService.getPosition();
        return new ResponseMessage(objects);
    }
}
