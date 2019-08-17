package com.jianzixing.webapp.service.promotions.luckwheel;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.promotions.TablePlwAward;
import com.jianzixing.webapp.tables.promotions.TablePlwRecord;
import com.jianzixing.webapp.tables.promotions.TablePlwRule;
import org.mimosaframework.core.algorithm.gaussian.GaussianUtils;
import org.mimosaframework.core.algorithm.lottery.Lottery;
import org.mimosaframework.core.algorithm.lottery.LotteryUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.calculator.CalcNumber;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DefaultLuckyWheelService implements LuckyWheelService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addRule(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TablePlwRule.class);
        object.checkAndThrowable();
        sessionTemplate.save(object);
    }

    @Override
    public void deleteRule(long id) {
        ModelObject update = new ModelObject(TablePlwRule.class);
        update.put(TablePlwRule.id, id);
        update.put(TablePlwRule.isDel, 1);
        sessionTemplate.update(update);
    }

    @Override
    public void updateRule(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TablePlwRule.class);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public Paging getRules(Query query, long start, long limit) {
        if (query == null) {
            query = Criteria.query(TablePlwRule.class);
        }
        query.limit(start, limit);
        query.order(TablePlwRule.id, false);
        return sessionTemplate.paging(query);
    }

    @Override
    public void addAwards(List<ModelObject> awards) throws ModelCheckerException, ModuleException {
        if (awards != null) {
            CalcNumber number = CalcNumber.as(0);
            for (ModelObject award : awards) {
                award.setObjectClass(TablePlwAward.class);
                award.checkAndThrowable();

                BigDecimal odds = award.getBigDecimal(TablePlwAward.odds);
                number.add(odds);
            }
            if (number.toDouble() != 100) {
                throw new ModuleException(StockCode.ARG_VALID, "所有奖品中奖概率总和不是100%");
            }
            sessionTemplate.save(awards);
        }
    }

    @Override
    public void updateAwards(List<ModelObject> awards) throws ModelCheckerException, ModuleException {
        if (awards != null) {
            CalcNumber number = CalcNumber.as(0);
            for (ModelObject award : awards) {
                award.setObjectClass(TablePlwAward.class);
                award.checkUpdateThrowable();

                BigDecimal odds = award.getBigDecimal(TablePlwAward.odds);
                number.add(odds);
            }
            if (number.toDouble() != 100) {
                throw new ModuleException(StockCode.ARG_VALID, "所有奖品中奖概率总和不是100%");
            }
            sessionTemplate.update(awards);
        }
    }

    @Override
    public void randomAward(long uid, long rid) throws ModuleException {
        ModelObject object = sessionTemplate.get(TablePlwRule.class, rid);
        if (object == null) {
            throw new ModuleException(StockCode.NOT_EXIST, "抽奖活动不存在");
        }
        int enable = object.getIntValue(TablePlwRule.enable);
        if (enable == 0) {
            throw new ModuleException(StockCode.DISABLE, "抽奖活动已过期");
        }
        Date startTime = object.getDate(TablePlwRule.startTime);
        Date finishTime = object.getDate(TablePlwRule.finishTime);
        long currTime = System.currentTimeMillis();
        if (startTime.getTime() > currTime) {
            throw new ModuleException(StockCode.NOT_START, "抽奖活动还未开始");
        }
        if (finishTime.getTime() < currTime) {
            throw new ModuleException(StockCode.NOT_START, "抽奖活动已经结束");
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String dayTime = format.format(new Date());
        int count = object.getIntValue(TablePlwRule.count);
        int type = object.getIntValue(TablePlwRule.type);
        long recordCount = 0;
        if (type == 0) {
            recordCount = sessionTemplate.count(Criteria.query(TablePlwRecord.class).eq(TablePlwRecord.uid, uid));
        }
        if (type == 1) {
            recordCount = sessionTemplate.count(Criteria.query(TablePlwRecord.class).eq(TablePlwRecord.uid, uid)
                    .eq(TablePlwRecord.dayTime, dayTime));
        }
        if (recordCount >= count) {
            throw new ModuleException(StockCode.TOO_LARGE, "该用户抽奖次数用尽");
        }

        List<ModelObject> awards = sessionTemplate.list(Criteria.query(TablePlwAward.class).eq(TablePlwAward.rid, rid));
        if (awards != null) {
            List<Lottery<ModelObject>> lotteries = new ArrayList<>();
            BigDecimal bg = new BigDecimal(100);
            for (ModelObject award : awards) {
                double odds = award.getDoubleValue(TablePlwAward.odds);
                BigDecimal last = bg.subtract(new BigDecimal(odds));
                lotteries.add(new Lottery(award, odds));
                bg = last;
            }

            Lottery<ModelObject> lottery = LotteryUtils.randomLottery(lotteries);
            long aid = lottery.getAward().getLongValue(TablePlwAward.id);

            long c = sessionTemplate.update(Criteria.update(TablePlwAward.class)
                    .subSelf(TablePlwAward.amount, 1).eq(TablePlwAward.id, aid));

            if (c > 0) {
                ModelObject record = new ModelObject(TablePlwRecord.class);
                record.put(TablePlwRecord.uid, uid);
                record.put(TablePlwRecord.rid, rid);
                record.put(TablePlwRecord.aid, aid);
                record.put(TablePlwRecord.status, LuckWheelStatus.NOT_DELIVERY.getCode());
                record.put(TablePlwRecord.createTime, new Date());
                sessionTemplate.save(record);
            } else {
                throw new ModuleException(StockCode.NOT_ENOUGH, "奖品数量不足");
            }
        }
    }

    public static void main1(String[] args) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int j = 0; j < 1000; j++) {
            List<Lottery<ModelObject>> lotteries = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                ModelObject o = new ModelObject();
                double odds = 0;
                if (i == 0) odds = 10;
                if (i == 1) odds = 20;
                if (i == 2) odds = 30;
                if (i == 3) odds = 15;
                if (i == 4) odds = 25;
                o.put("odds", "odds:" + odds);
                lotteries.add(new Lottery(o, odds));
            }
            Lottery<ModelObject> lottery = LotteryUtils.randomLottery(lotteries);
            Integer count = map.get(lottery.getAward().getString("odds"));
            if (count == null) count = 0;
            count = count + 1;
            map.put(lottery.getAward().getString("odds"), count);
            System.out.println(lottery.getAward());
        }

        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            BigDecimal bd = (new BigDecimal(entry.getValue())).divide(new BigDecimal(1000));
            bd.setScale(4, BigDecimal.ROUND_DOWN);
            System.out.println(entry.getKey() + " = " + bd.doubleValue());
        }
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new TreeMap<>();
        for (int i = 0; i < 10000; i++) {
            double d = GaussianUtils.random();
            String s = "Other";
            if (d >= -3 && d < -2.5) s = "A:-2.5";
            if (d >= -2.5 && d < -2) s = "B:-2";
            if (d >= -2 && d < -1.5) s = "C:-1.5";
            if (d >= -1.5 && d < -1) s = "D:-1";
            if (d >= -1 && d < -0.5) s = "E:-0.5";
            if (d >= -0.5 && d < 0) s = "F:0";
            if (d >= 0 && d < 0.5) s = "G:0.5";
            if (d >= 0.5 && d < 1) s = "H:1";
            if (d >= 1.5 && d < 2) s = "I:1.5";
            if (d >= 2 && d < 2.5) s = "J:2";
            if (d >= 2.5 && d < 3) s = "K:2.5";
            Integer c = map.get(s);
            if (c == null) c = 0;
            c = c + 1;
            map.put(s, c);
        }

        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            BigDecimal bd = (new BigDecimal(entry.getValue())).divide(new BigDecimal(10000));
            bd.setScale(4, BigDecimal.ROUND_DOWN);
            System.out.println(entry.getKey() + " = " + bd.doubleValue());
        }
    }
}
