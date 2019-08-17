package com.jianzixing.webapp.service.balance;

import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;

import java.math.BigDecimal;
import java.util.List;

public interface BalanceService {
    Paging getBalances(ModelObject search, long start, long limit);

    Paging getRecords(ModelObject search, long start, long limit);

    Paging getRecordsByUid(long uid, long start, long limit);

    ModelObject getBalanceByUid(long uid);

    long orderIn(String number, long uid, BigDecimal amount);

    long handBack(String number, long uid, BigDecimal payPrice);

    /**
     * 创建一个充值单，比传入参数 userId , money
     *
     * @return
     */
    long addRechargeOrder(ModelObject object) throws ModelCheckerException;

    ModelObject getRechargeOrder(long id);

    void setRechargeChannel(String number, long channelId, String channelName);

    void setRechargeSuccess(String orderNumber);

    /**
     * 获取用户余额使用记录
     *
     * @param uid
     * @param page
     * @return
     */
    List<ModelObject> getUserRecords(long uid, int page);

    /**
     * 通过充值单号获取一个充值单
     *
     * @param oid
     * @return
     */
    ModelObject getRechargeOrderByNumber(String oid);
}
