package com.jianzixing.webapp.service.balance;

import com.jianzixing.webapp.tables.balance.TableBalance;
import com.jianzixing.webapp.tables.balance.TableBalanceRecharge;
import com.jianzixing.webapp.tables.balance.TableBalanceRecord;
import com.jianzixing.webapp.tables.user.TableUser;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.orm.transaction.Transaction;
import org.mimosaframework.orm.transaction.TransactionCallback;
import org.mimosaframework.orm.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class DefaultBalanceService implements BalanceService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public Paging getBalances(ModelObject search, long start, long limit) {
        Query query = Criteria.query(TableUser.class);
        query.limit(start, limit);
        query.order(TableUser.id, false);
        query.subjoin(TableBalance.class).eq(TableBalance.userId, TableUser.id).single();
        return ModelUtils.getSearch("balance", sessionTemplate, search, query, TableUser.id);
    }

    @Override
    public Paging getRecords(ModelObject search, long start, long limit) {
        Query query = Criteria.query(TableBalanceRecord.class);
        query.limit(start, limit);
        query.order(TableBalanceRecord.id, false);
        query.subjoin(TableUser.class).eq(TableUser.id, TableBalanceRecord.userId).single();
        return ModelUtils.getSearch("balance_record", sessionTemplate, search, query, TableBalanceRecord.userId);
    }

    @Override
    public Paging getRecordsByUid(long uid, long start, long limit) {
        return sessionTemplate.paging(
                Criteria.query(TableBalanceRecord.class)
                        .subjoin(TableUser.class).eq(TableUser.id, TableBalanceRecord.userId).single().query()
                        .eq(TableBalanceRecord.userId, uid)
                        .limit(start, limit).order(TableBalanceRecord.id, false));
    }

    @Override
    public ModelObject getBalanceByUid(long uid) {
        return sessionTemplate.get(
                Criteria.query(TableBalance.class)
                        .eq(TableBalance.userId, uid)
        );
    }

    public long changeUserIntegral(long userId, BigDecimal change, boolean isAdd, String msg) {
        ModelObject integral = sessionTemplate.get(TableBalance.class, userId);
        if (integral == null) {
            ModelObject balance = new ModelObject(TableBalance.class);
            balance.put(TableBalance.userId, userId);
            balance.put(TableBalance.balance, CalcNumber.as(change).toPrice());
            sessionTemplate.save(balance);
            return this.addRecord(userId, change, CalcNumber.as(change).toBigDecimalPrice(), new BigDecimal(0), msg);
        } else {
            BigDecimal c = integral.getBigDecimal(TableBalance.balance);
            change = new BigDecimal(CalcNumber.as(change).toPrice());
            BigDecimal after = c.add(change);
            if (isAdd) {
                sessionTemplate.update(
                        Criteria.update(TableBalance.class)
                                .addSelf(TableBalance.balance, CalcNumber.as(change).toPrice())
                                .eq(TableBalance.userId, userId));
            } else {
                sessionTemplate.update(
                        Criteria.update(TableBalance.class)
                                .subSelf(TableBalance.balance, CalcNumber.as(change).toPrice())
                                .eq(TableBalance.userId, userId));
            }
            return this.addRecord(userId, change, c, after, msg);
        }
    }


    @Override
    public long orderIn(String number, long uid, BigDecimal amount) {
        return this.changeUserIntegral(uid, amount, false, "用户下单(" + number + ")使用余额" + amount.doubleValue() + "元");
    }

    @Override
    public long handBack(String number, long uid, BigDecimal amount) {
        return this.changeUserIntegral(uid, amount, true, "订单退款(" + number + ")退回余额" + amount.doubleValue() + "元");
    }

    @Override
    public long addRechargeOrder(ModelObject object) throws ModelCheckerException {
        if (object != null) {
            object.setObjectClass(TableBalanceRecharge.class);
            object.put(TableBalanceRecharge.number, RandomUtils.uuid());
            object.put(TableBalanceRecharge.createTime, new Date());
            object.checkAndThrowable();
            sessionTemplate.save(object);
            return object.getLongValue(TableBalanceRecharge.id);
        }
        return 0;
    }

    @Override
    public ModelObject getRechargeOrder(long id) {
        return sessionTemplate.get(TableBalanceRecharge.class, id);
    }

    @Override
    public void setRechargeChannel(String number, long channelId, String channelName) {
        sessionTemplate.update(Criteria.update(TableBalanceRecharge.class)
                .eq(TableBalanceRecharge.number, number)
                .value(TableBalanceRecharge.channelId, channelId)
                .value(TableBalanceRecharge.channelName, channelName));
    }

    @Override
    public void setRechargeSuccess(String orderNumber) {
        try {
            ModelObject recharge = sessionTemplate.get(Criteria.query(TableBalanceRecharge.class).eq(TableBalanceRecharge.number, orderNumber));
            long uid = recharge.getLongValue(TableBalanceRecharge.userId);
            BigDecimal money = recharge.getBigDecimal(TableBalanceRecharge.money);
            String lock = (uid + orderNumber).intern();
            synchronized (lock) {
                sessionTemplate.execute(new TransactionCallback<Boolean>() {
                    @Override
                    public Boolean invoke(Transaction transaction) throws Exception {
                        sessionTemplate.update(Criteria.update(TableBalanceRecharge.class)
                                .eq(TableBalanceRecharge.number, orderNumber)
                                .value(TableBalanceRecharge.status, BalanceRechargeStatus.RECHARGED.getCode()));
                        changeUserIntegral(uid, money, true, "充值余额");
                        return true;
                    }
                });
            }


        } catch (TransactionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ModelObject> getUserRecords(long uid, int page) {
        int limit = 20;
        int start = (page - 1) * limit;

        return sessionTemplate.list(Criteria.query(TableBalanceRecord.class)
                .eq(TableBalanceRecord.userId, uid)
                .order(TableBalanceRecord.id, false)
                .limit(start, limit));
    }

    @Override
    public ModelObject getRechargeOrderByNumber(String oid) {
        return sessionTemplate.get(Criteria.query(TableBalanceRecharge.class)
                .eq(TableBalanceRecharge.number, oid));
    }

    private long addRecord(long userId, BigDecimal change, BigDecimal before, BigDecimal after, String msg) {
        ModelObject object = new ModelObject(TableBalanceRecord.class);
        object.put(TableBalanceRecord.userId, userId);
        object.put(TableBalanceRecord.changeBalance, change);
        object.put(TableBalanceRecord.beforeBalance, before);
        object.put(TableBalanceRecord.afterBalance, after);
        object.put(TableBalanceRecord.detail, msg);
        object.put(TableBalanceRecord.createTime, new Date());
        sessionTemplate.save(object);
        return object.getLongValue(TableBalanceRecord.id);
    }
}
