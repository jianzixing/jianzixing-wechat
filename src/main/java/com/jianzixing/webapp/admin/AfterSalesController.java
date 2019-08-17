package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.aftersales.AfterSalesType;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

@APIController
public class AfterSalesController {

    @Printer
    public ResponsePageMessage getAfterSales(ModelObject search, String type, long start, long limit) {
        try {
            if (type != null && AfterSalesType.valueOf(type) == null) type = null;
        } catch (Exception e) {
            type = null;
        }
        Paging paging = GlobalService.afterSalesService.getAfterSales(search, type != null ? AfterSalesType.valueOf(type).getCode() : 0, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponsePageMessage getAfterSaleProgress(long asid, long start, long limit) {
        Paging paging = GlobalService.afterSalesService.getAfterSaleProgress(asid, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage getAfterSalesById(long id) {
        ModelObject object = GlobalService.afterSalesService.getAfterSalesById(id);

        ModelObject addressObj = new ModelObject();
        String name = GlobalService.systemConfigService.getValue("after_sale_address_name");
        String phone = GlobalService.systemConfigService.getValue("after_sale_address_phone");
        String address = GlobalService.systemConfigService.getValue("after_sale_address_address");
        addressObj.put("name", name);
        addressObj.put("phone", phone);
        addressObj.put("address", address);
        object.put("auditBackAddress", addressObj);

        return new ResponseMessage(object);
    }

    @Printer
    public ResponseMessage addAfterSales(ModelObject object) {
        try {
            GlobalService.afterSalesService.addAfterSalesByOrder(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    //取消售后申请单
    @Printer
    public ResponseMessage cancelAfterSales(long id) {
        try {
            GlobalService.afterSalesService.cancelAfterSales(id);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    //商品寄回
    @Printer
    public ResponseMessage rebackGoods(ModelObject object) {
        try {
            GlobalService.afterSalesService.rebackGoods(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    //维修失败
    @Printer
    public ResponseMessage repairFailure(long id) {
        try {
            GlobalService.afterSalesService.repairFailure(id);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    //维修成功
    @Printer
    public ResponseMessage repairSuccess(long id) {
        try {
            GlobalService.afterSalesService.repairSuccess(id);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    //重新发货
    @Printer
    public ResponseMessage resendGoods(ModelObject object) {
        try {
            GlobalService.afterSalesService.resendGoods(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    //创建退款单
    @Printer
    public ResponseMessage createRefundOrder(ModelObject object) {
        try {
            GlobalService.afterSalesService.createRefundOrder(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getRefundMoney(long id) {
        try {
            ModelObject object = GlobalService.afterSalesService.getRefundMoney(id);
            object.put("refundPrice", CalcNumber.as(object.getBigDecimal("refundPrice")).toPrice());
            return new ResponseMessage(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
    }

    //确认开始维修
    @Printer
    public ResponseMessage sureStartRepair(long id) {
        try {
            GlobalService.afterSalesService.sureStartRepair(id);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    //确认验货通过
    @Printer
    public ResponseMessage sureCheckGoodsSuccess(long id) {
        try {
            GlobalService.afterSalesService.sureCheckGoodsSuccess(id);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    //确认验货失败
    @Printer
    public ResponseMessage sureCheckGoodsFailure(long id) {
        try {
            GlobalService.afterSalesService.sureCheckGoodsFailure(id);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    //确认已收货
    @Printer
    public ResponseMessage sureGetGoods(long id) {
        try {
            GlobalService.afterSalesService.setGetGoods(id);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    //审核拒绝
    @Printer
    public ResponseMessage auditRefused(long id) {
        try {
            GlobalService.afterSalesService.auditRefused(id);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    //审核通过
    @Printer
    public ResponseMessage auditPass(long id,
                                     String realName,
                                     String phoneNumber,
                                     String address) {
        try {

            GlobalService.afterSalesService.auditPass(id, realName, phoneNumber, address);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }
}
