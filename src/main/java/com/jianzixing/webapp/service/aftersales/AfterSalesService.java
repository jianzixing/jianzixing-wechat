package com.jianzixing.webapp.service.aftersales;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.exception.TransactionException;

import java.sql.SQLException;
import java.util.List;

public interface AfterSalesService {
    /**
     * 添加售后单 type , orderGoodsId , amount , userId , reason , detail 为必须传入
     * 如果有图片则 images = [{fileName:'xxx.jpg'}]
     *
     * @param object
     * @throws ModuleException
     * @throws ModelCheckerException
     */
    void addAfterSales(ModelObject object) throws ModuleException, ModelCheckerException, TransactionException;

    Paging getAfterSales(ModelObject search, int type, long start, long limit);

    Paging getAfterSaleProgress(long asid, long start, long limit);

    void addAfterSalesByOrder(ModelObject object) throws ModuleException, ModelCheckerException, TransactionException;

    void addProgress(long asid, long adminId, String detail);

    void cancelAfterSales(long id) throws ModuleException;

    void auditPass(long id, String realName, String phone, String address) throws ModuleException;

    void auditRefused(long id) throws ModuleException;

    ModelObject getAfterSalesById(long id);

    ModelObject getSimpleAfterSalesById(long id);

    void sureCheckGoodsFailure(long id) throws ModuleException;

    void setGetGoods(long id) throws ModuleException;

    void sureCheckGoodsSuccess(long id) throws ModuleException;

    void sureStartRepair(long id) throws ModuleException;

    void repairSuccess(long id) throws ModuleException;

    void repairFailure(long id) throws ModuleException;

    /**
     * 创建退款单，如果整个支付没有延时支付方式
     * 则直接退款不需要创建退款单
     *
     * @param object
     * @throws ModuleException
     * @throws TransactionException
     */
    void createRefundOrder(ModelObject object) throws ModuleException, TransactionException;

    ModelObject getRefundMoney(long id) throws ModuleException;

    void rebackGoods(ModelObject object) throws ModuleException, ModelCheckerException, TransactionException;

    void resendGoods(ModelObject object) throws ModuleException, ModelCheckerException, TransactionException;

    /**
     * 获取用户的订单列表，用来申请售后使用
     *
     * @param uid
     * @param keyword
     * @param page
     * @return
     */
    List<ModelObject> getOrderByAfterSales(long uid, String keyword, int page);

    /**
     * 获取正在售后的售后单
     *
     * @param uid
     * @param page
     * @return
     */
    List<ModelObject> getProcessAfterSaleList(long uid, int page);

    /**
     * 获得用户全部的售后单
     *
     * @param uid
     * @param keyword
     * @param page
     * @return
     */
    List<ModelObject> getAfterSaleList(long uid, String keyword, int page);

    /**
     * 获得用户下单的一个订单商品
     *
     * @param og
     * @return
     */
    ModelObject getAfterSaleOrderGoods(long uid, long og);

    /**
     * 通过售后单号获取售后单信息
     *
     * @param number
     * @return
     */
    ModelObject getUserAfterSaleDetail(long uid, String number);

    /**
     * 获取售后单处理列表
     *
     * @param uid
     * @param number
     * @return
     */
    List<ModelObject> getAfterSaleProcess(long uid, String number);

    /**
     * 取消用户的售后单
     *
     * @param uid
     * @param number
     */
    void cancelUserAfterSalesByNumber(long uid, String number) throws ModuleException;

    /**
     * 用户寄回售后商品
     *
     * @param uid
     * @param as
     */
    void deliveryGoodsByUser(long uid, ModelObject as) throws ModuleException, TransactionException;

    /**
     * 通过退款单获取相关的售后单
     *
     * @param fromId
     * @param refundId
     * @return
     */
    ModelObject getAfterSalesByRefund(long fromId, long refundId);

    /**
     * 设置售后单完成
     *
     * @param asid
     */
    void setAfterSalesFinish(long asid) throws ModuleException;

    /**
     * 获取一个售后单的退款记录
     *
     * @param asid
     * @return
     */
    ModelObject getAfterSalesRefund(long asid);

    /**
     * 获得某个状态的售后单数量
     *
     * @param type
     * @return
     */
    long getAfterSaleCount(int type);
}
