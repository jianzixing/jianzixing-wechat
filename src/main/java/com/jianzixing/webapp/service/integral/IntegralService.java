package com.jianzixing.webapp.service.integral;

import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.math.BigDecimal;
import java.util.List;

public interface IntegralService {
    Paging getIntegrals(ModelObject search, long start, long limit);

    void clearUserIntegrals(List<Long> users);

    long changeUserIntegral(long userId, int change, String msg);

    Paging getRecords(ModelObject search, long start, long limit);

    Paging getRecordsByUid(long uid, long start, long limit);

    ModelObject getIntegralByUid(long uid);

    ModelObject getIntegralCanPayByUid(long uid);

    /**
     * 用户下单时使用的积分
     *
     * @param number  订单号
     * @param uid     用户id
     * @param amount  扣除积分数量
     * @param residue 剩余积分数量
     */
    long orderIn(String number, long uid, BigDecimal amount, BigDecimal residue);

    long handBack(String number, long uid, BigDecimal amount);

    /**
     * 判断用户积分是否达到了最低可用额
     *
     * @param integral
     * @return
     */
    boolean isIntegralCanPay(ModelObject integral);

    /**
     * 获得用户的积分消费记录
     *
     * @param uid
     * @param page
     * @return
     */
    List<ModelObject> getUserIntegralRecord(long uid, int page);
}
