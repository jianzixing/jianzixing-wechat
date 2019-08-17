package com.jianzixing.webapp.service.integral;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.integral.TableIntegral;
import com.jianzixing.webapp.tables.integral.TableIntegralRecord;
import com.jianzixing.webapp.tables.user.TableUser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class DefaultIntegralService implements IntegralService {
    private static final Log logger = LogFactory.getLog(DefaultIntegralService.class);

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public Paging getIntegrals(ModelObject search, long start, long limit) {
        Query query = Criteria.query(TableUser.class);
        query.limit(start, limit);
        query.order(TableUser.id, false);
        query.subjoin(TableIntegral.class).eq(TableIntegral.userId, TableUser.id).single();
        return ModelUtils.getSearch("integral", sessionTemplate, search, query, TableUser.id);
    }

    @Override
    public void clearUserIntegrals(List<Long> users) {
        for (long uid : users) {
            ModelObject integral = sessionTemplate.get(TableIntegral.class, uid);
            if (integral != null) {
                long c = integral.getLongValue(TableIntegral.amount);
                sessionTemplate.update(
                        Criteria.update(TableIntegral.class)
                                .value(TableIntegral.amount, 0)
                                .eq(TableIntegral.userId, uid));
                this.addRecord(uid, -c, c, 0, "强制清空积分");
            }
        }
    }

    @Override
    public long changeUserIntegral(long userId, int change, String msg) {
        ModelObject integral = sessionTemplate.get(TableIntegral.class, userId);
        if (integral != null) {
            long c = integral.getLongValue(TableIntegral.amount);
            long after = c + change;
            if (change > 0) {
                sessionTemplate.update(
                        Criteria.update(TableIntegral.class)
                                .addSelf(TableIntegral.amount, change)
                                .eq(TableIntegral.userId, userId));
            } else if (change < 0) {
                sessionTemplate.update(
                        Criteria.update(TableIntegral.class)
                                .subSelf(TableIntegral.amount, Math.abs(change))
                                .eq(TableIntegral.userId, userId));
            }
            return this.addRecord(userId, change, c, after, msg);
        } else {
            long c = 0;
            long after = change;
            if (after < 0) {
                logger.error("用户" + userId + "扣减积分但是初始积分为0无法扣减");
                return 0;
            } else {
                integral = new ModelObject(TableIntegral.class);
                integral.put(TableIntegral.userId, userId);
                integral.put(TableIntegral.amount, after);
                sessionTemplate.save(integral);
                return this.addRecord(userId, change, c, after, msg);
            }
        }
    }

    @Override
    public Paging getRecords(ModelObject search, long start, long limit) {
        Query query = Criteria.query(TableIntegralRecord.class);
        query.limit(start, limit);
        query.order(TableIntegralRecord.id, false);
        query.subjoin(TableUser.class).eq(TableUser.id, TableIntegralRecord.userId).single();
        return ModelUtils.getSearch("integral_record", sessionTemplate, search, query, TableIntegralRecord.userId);
    }

    @Override
    public Paging getRecordsByUid(long uid, long start, long limit) {
        return sessionTemplate.paging(
                Criteria.query(TableIntegralRecord.class)
                        .subjoin(TableUser.class).eq(TableUser.id, TableIntegralRecord.userId).single().query()
                        .eq(TableIntegralRecord.userId, uid)
                        .limit(start, limit).order(TableIntegralRecord.id, false));
    }

    @Override
    public ModelObject getIntegralByUid(long uid) {
        return sessionTemplate.get(
                Criteria.query(TableIntegral.class)
                        .eq(TableIntegral.userId, uid)
        );
    }

    @Override
    public ModelObject getIntegralCanPayByUid(long uid) {
        ModelObject integral = this.getIntegralByUid(uid);
        if (!GlobalService.integralService.isIntegralCanPay(integral)) {
            integral = null;
        }
        return integral;
    }

    @Override
    public long orderIn(String number, long uid, BigDecimal amount, BigDecimal residue) {
        return this.changeUserIntegral(uid, -amount.intValue(), "用户下单消耗积分" + amount.intValue() + "个,订单号[" + number + "]");
    }

    @Override
    public long handBack(String number, long uid, BigDecimal amount) {
        return this.changeUserIntegral(uid, amount.intValue(), "取消订单退回积分" + amount.intValue() + "个,订单号[" + number + "]");
    }

    @Override
    public boolean isIntegralCanPay(ModelObject integral) {
        String integralMinMoney = GlobalService.systemConfigService.getValue("integral_min_money");
        try {
            if (StringUtils.isNotBlank(integralMinMoney)) {
                long integralMinMoneyCount = Long.parseLong(integralMinMoney);
                long integralAmount = integral.getLongValue(TableIntegral.amount);
                if (integralAmount >= integralMinMoneyCount) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("判断用户积分是否可用时报错", e);
        }
        return false;
    }

    @Override
    public List<ModelObject> getUserIntegralRecord(long uid, int page) {
        int limit = 20;
        int start = (page - 1) * limit;

        return sessionTemplate.list(Criteria.query(TableIntegralRecord.class)
                .eq(TableIntegralRecord.userId, uid)
                .order(TableIntegralRecord.id, false)
                .limit(start, limit));
    }

    private long addRecord(long userId, long change, long before, long after, String msg) {
        ModelObject object = new ModelObject(TableIntegralRecord.class);
        object.put(TableIntegralRecord.userId, userId);
        object.put(TableIntegralRecord.changeAmount, change);
        object.put(TableIntegralRecord.beforeAmount, before);
        object.put(TableIntegralRecord.afterAmount, after);
        object.put(TableIntegralRecord.detail, msg);
        object.put(TableIntegralRecord.createTime, new Date());
        sessionTemplate.save(object);
        return object.getLongValue(TableIntegralRecord.id);
    }
}
