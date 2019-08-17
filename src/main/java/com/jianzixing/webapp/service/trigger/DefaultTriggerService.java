package com.jianzixing.webapp.service.trigger;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.tables.trigger.TableTriggerValue;
import com.jianzixing.webapp.tables.trigger.TableTrigger;
import com.jianzixing.webapp.tables.trigger.TableTriggerRecord;
import com.jianzixing.webapp.tables.trigger.TableTriggerRule;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.orm.transaction.Transaction;
import org.mimosaframework.orm.transaction.TransactionCallback;
import org.mimosaframework.springmvc.utils.FreeMarkerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DefaultTriggerService implements TriggerService {
    private static final Log logger = LogFactory.getLog(DefaultTriggerService.class);

    @Autowired
    SessionTemplate sessionTemplate;

    @Autowired
    ApplicationContext context;

    /**
     * @param type
     * @return 如果触发器绑定了TriggerEventReturn则需要返回相应的参数, 注意优先级
     */
    @Override
    public ModelObject trigger(long uid, EventType type, ModelObject params) {
        params.put("userId", uid);
        // 先检查数据库中注册的事件关联的触发器
        String eventName = type.name();
        List<ModelObject> triggers = getEnableTriggers(eventName);
        if (triggers != null) {
            for (ModelObject trigger : triggers) {
                long tid = trigger.getLongValue(TableTrigger.id);
                String name = trigger.getString(TableTrigger.name);
                Date startTime = trigger.getDate(TableTrigger.startTime);
                Date finishTime = trigger.getDate(TableTrigger.finishTime);
                int timeType = trigger.getIntValue(TableTrigger.timeType);
                long triggerCount = trigger.getLongValue(TableTrigger.triggerCount);
                long totalCount = trigger.getLongValue(TableTrigger.totalCount);
                long triggerInfinite = trigger.getLongValue(TableTrigger.triggerInfinite);
                int useRule = trigger.getIntValue(TableTrigger.useRule);
                long now = System.currentTimeMillis();

                if (useRule == 1) {
                    if (startTime != null && startTime.getTime() >= now) {
                        // 时间还没开始直接跳过
                        logger.info("触发器[" + name + "]还没有到开始时间");
                        continue;
                    }
                    if (finishTime != null && finishTime.getTime() <= now) {
                        // 时间已经结束直接跳过
                        logger.info("触发器[" + name + "]已经过期");
                        // 设置禁用
                        ModelObject update = new ModelObject(TableTrigger.class);
                        update.put(TableTrigger.id, tid);
                        update.put(TableTrigger.enable, 0);
                        sessionTemplate.update(update);
                        continue;
                    }
                    List<ModelObject> rules = trigger.getArray(TableTriggerRule.class.getSimpleName());
                    // 然后开始检查计算规则，计算规则符合则继续

                    boolean isRuleNotPass = false;
                    if (rules != null) {
                        for (ModelObject rule : rules) {
                            String ruleName = rule.getString(TableTriggerRule.name);
                            String code = rule.getString(TableTriggerRule.code);
                            String symbol = rule.getString(TableTriggerRule.symbol);
                            String value = rule.getString(TableTriggerRule.value);
                            Object fromValue = params.get(code);
                            if (params.isEmpty(code) || fromValue == null || "".equals(fromValue)) {
                                logger.info("触发器[" + name + "]计算规则[" + ruleName + "]不在参数列表中");
                                continue;
                            }

                            boolean isPass = false;
                            if (SymbolType.EQ.getName().equalsIgnoreCase(symbol)) {
                                if (String.valueOf(fromValue).equals(value)) {
                                    isPass = true;
                                }
                            } else {
                                // 如果两个都是数字则通过数字判断
                                String source = String.valueOf(fromValue);
                                if (NumberUtils.isNumber(value) && NumberUtils.isNumber(source)) {
                                    BigDecimal bd2 = NumberUtils.createBigDecimal(value);
                                    BigDecimal bd1 = NumberUtils.createBigDecimal(source);
                                    if (SymbolType.GT.getName().equalsIgnoreCase(symbol) && bd1.compareTo(bd2) > 0)
                                        isPass = true;
                                    if (SymbolType.GTE.getName().equalsIgnoreCase(symbol) && bd1.compareTo(bd2) >= 0)
                                        isPass = true;
                                    if (SymbolType.LT.getName().equalsIgnoreCase(symbol) && bd1.compareTo(bd2) < 0)
                                        isPass = true;
                                    if (SymbolType.LTE.getName().equalsIgnoreCase(symbol) && bd1.compareTo(bd2) <= 0)
                                        isPass = true;
                                } else {
                                    if (SymbolType.GT.getName().equalsIgnoreCase(symbol) && source.compareTo(value) > 0)
                                        isPass = true;
                                    if (SymbolType.GTE.getName().equalsIgnoreCase(symbol) && source.compareTo(value) >= 0)
                                        isPass = true;
                                    if (SymbolType.LT.getName().equalsIgnoreCase(symbol) && source.compareTo(value) < 0)
                                        isPass = true;
                                    if (SymbolType.LTE.getName().equalsIgnoreCase(symbol) && source.compareTo(value) <= 0)
                                        isPass = true;
                                }
                            }
                            if (!isPass) {
                                isRuleNotPass = true;
                            }
                        }
                    }

                    if (!isRuleNotPass) {
                        // 开始判断触发次数
                        ModelObject record = this.getTriggerRecord(tid, uid, eventName, name);
                        boolean isCanRun = false;
                        if (triggerInfinite == 1 && timeType == 7) {
                            isCanRun = true;
                        } else {
                            long recordCount = 0;
                            if (timeType == TimeType.YEAR.getCode()) {
                                recordCount = getRecordCount(tid, uid, TableTriggerRecord.year, record.getString(TableTriggerRecord.year));
                            }
                            if (timeType == TimeType.MONTH.getCode()) {
                                recordCount = getRecordCount(tid, uid, TableTriggerRecord.month, record.getString(TableTriggerRecord.month));
                            }
                            if (timeType == TimeType.DAY.getCode()) {
                                recordCount = getRecordCount(tid, uid, TableTriggerRecord.day, record.getString(TableTriggerRecord.day));
                            }
                            if (timeType == TimeType.HOUR.getCode()) {
                                recordCount = getRecordCount(tid, uid, TableTriggerRecord.hour, record.getString(TableTriggerRecord.hour));
                            }
                            if (timeType == TimeType.MINUTE.getCode()) {
                                recordCount = getRecordCount(tid, uid, TableTriggerRecord.minute, record.getString(TableTriggerRecord.minute));
                            }
                            if (timeType == TimeType.SECOND.getCode()) {
                                recordCount = getRecordCount(tid, uid, TableTriggerRecord.second, record.getString(TableTriggerRecord.second));
                            }
                            if (recordCount < triggerCount) {
                                if (totalCount > 0) {
                                    recordCount = getRecordCount(tid, uid, null, null);
                                    if (recordCount < totalCount) {
                                        isCanRun = true;
                                    } else {
                                        logger.info("触发器[" + name + "]未执行,执行次数超过最大值");
                                    }
                                } else {
                                    isCanRun = true;
                                }
                            } else {
                                logger.info("触发器[" + name + "]未执行,执行次数超过本次时间范围");
                            }
                        }


                        if (isCanRun) {
                            // 开始执行处理器
                            try {
                                this.runProcessor(trigger, params);
                            } catch (ModuleException e) {
                                logger.error("执行处理器出错", e);
                            } finally {
                                sessionTemplate.save(record);
                            }
                        }
                    } else {
                        logger.info("触发器[" + name + "]计算规则不通过");
                    }
                } else {
                    try {
                        this.runProcessor(trigger, params);
                    } catch (ModuleException e) {
                        logger.error("执行处理器出错", e);
                    } finally {
                        ModelObject record = this.getTriggerRecord(tid, uid, eventName, name);
                        sessionTemplate.save(record);
                    }
                }

                logger.info("触发器[" + name + "]执行成功!");
            }
        }

        // 如果返回True则开始执行触发器，如果有返回值则返回
        return null;
    }

    private ModelObject getTriggerRecord(long tid, long uid, String eventName, String name) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatTime = format.format(new Date());
        String year = formatTime.substring(0, 4);
        String month = formatTime.substring(4, 6);
        String day = formatTime.substring(6, 8);
        String hour = formatTime.substring(8, 10);
        String minute = formatTime.substring(10, 12);
        String second = formatTime.substring(12, 14);

        ModelObject record = new ModelObject(TableTriggerRecord.class);
        record.put(TableTriggerRecord.triggerId, tid);
        record.put(TableTriggerRecord.event, eventName);
        record.put(TableTriggerRecord.userId, uid);
        record.put(TableTriggerRecord.triggerName, name);
        record.put(TableTriggerRecord.year, year);
        record.put(TableTriggerRecord.month, year + month);
        record.put(TableTriggerRecord.day, year + month + day);
        record.put(TableTriggerRecord.hour, year + month + day + hour);
        record.put(TableTriggerRecord.minute, year + month + day + hour + minute);
        record.put(TableTriggerRecord.second, year + month + day + hour + minute + second);
        record.put(TableTriggerRecord.createTime, new Date());
        return record;
    }

    private long getRecordCount(long tid, long uid, TableTriggerRecord field, String t) {
        Query query = Criteria.query(TableTriggerRecord.class);
        query.eq(TableTriggerRecord.triggerId, tid);
        if (uid > 0) {
            query.eq(TableTriggerRecord.userId, uid);
        }
        if (StringUtils.isNotBlank(t)) {
            query.eq(field, t);
        }
        return sessionTemplate.count(query);
    }

    private ProcessorField getFieldByCode(ProcessorField[] fields, String code) {
        for (ProcessorField f : fields) {
            if (f.getCode() != null && f.getCode().equalsIgnoreCase(code)) {
                return f;
            }
        }
        return null;
    }

    private List<ModelObject> getEnableTriggers(String eventName) {
        return sessionTemplate.list(
                Criteria.query(TableTrigger.class)
                        .eq(TableTrigger.event, eventName)
                        .eq(TableTrigger.enable, 1)
                        .subjoin(TableTriggerRule.class).eq(TableTriggerRule.triggerId, TableTrigger.id).query()
        );
    }

    private void setRuleAndValue(ModelObject object, List<ModelObject> rules, List<ModelObject> values, Object id) throws ModuleException {
        List<ModelObject> rm = new ArrayList<>();
        if (rules != null) {
            int index = 0;
            for (ModelObject rule : rules) {
                if (rule.isEmpty(TableTriggerRule.symbol)
                        || rule.isEmpty(TableTriggerRule.value)
                        || rule.isEmpty(TableTriggerRule.code)) {
                    rm.add(rule);
                } else {
                    rule.setObjectClass(TableTriggerRule.class);
                    rule.put(TableTriggerRule.index, ++index);
                    rule.put(TableTriggerRule.name, rule.getString("detail"));
                    if (id != null) {
                        rule.put(TableTriggerRule.triggerId, id);
                    }
                }
            }
            rules.removeAll(rm);
        }
        rm.clear();
        if (values != null) {
            int index = 0;
            for (ModelObject value : values) {
                if (value.isEmpty(TableTriggerValue.value)
                        || value.isEmpty(TableTriggerRule.code)) {
                    rm.add(value);
                } else {
                    value.setObjectClass(TableTriggerValue.class);
                    value.put(TableTriggerValue.index, ++index);
                    if (id != null) {
                        value.put(TableTriggerValue.triggerId, id);
                    }
                }
            }
            values.removeAll(rm);
        }

        int processorType = object.getIntValue(TableTrigger.processorType);
        if (processorType == ProcessorValueType.EMAIL.getCode()
                && object.isEmpty(TableTrigger.sid)) {
            throw new ModuleException(StockCode.ARG_NULL, "发送邮件必须选择一个邮件服务");
        }
        if (processorType == ProcessorValueType.SMS.getCode()
                && object.isEmpty(TableTrigger.sid)) {
            throw new ModuleException(StockCode.ARG_NULL, "发送短信必须选择一个短信服务");
        }

        int useRule = object.getIntValue(TableTrigger.useRule);
        if (useRule == 0) {
            object.put(TableTrigger.timeType, TimeType.PERPETUAL.getCode());
            object.put(TableTrigger.triggerCount, 0);
            object.put(TableTrigger.totalCount, 0);
            object.put(TableTrigger.triggerInfinite, 1);
            object.remove(TableTrigger.startTime);
            object.remove(TableTrigger.finishTime);
        }
        if (object.isEmpty(TableTrigger.triggerCount)) {
            object.put(TableTrigger.triggerCount, 0);
        }
        if (object.isEmpty(TableTrigger.totalCount)) {
            object.put(TableTrigger.totalCount, 0);
        }
        if (object.isEmpty(TableTrigger.startTime)) {
            object.put(TableTrigger.startTime, null);
        }
        if (object.isEmpty(TableTrigger.finishTime)) {
            object.put(TableTrigger.finishTime, null);
        }
    }

    @Override
    public void addTrigger(ModelObject object) throws ModelCheckerException, TransactionException, ModuleException {
        List<ModelObject> rules = object.getArray("rules");
        List<ModelObject> values = object.getArray("values");
        object.remove("rules");
        object.remove("values");
        object.setObjectClass(TableTrigger.class);
        object.checkAndThrowable();
        this.setRuleAndValue(object, rules, values, null);

        object.put(TableTrigger.createTime, new Date());

        sessionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean invoke(Transaction transaction) throws Exception {
                sessionTemplate.save(object);
                if (rules != null && rules.size() > 0) {
                    for (ModelObject rule : rules) {
                        rule.put(TableTriggerRule.triggerId, object.getLongValue(TableTrigger.id));
                    }
                    sessionTemplate.save(rules);
                }
                if (values != null && values.size() > 0) {
                    for (ModelObject value : values) {
                        value.put(TableTriggerValue.triggerId, object.getLongValue(TableTrigger.id));
                    }
                    sessionTemplate.save(values);
                }
                return null;
            }
        });
    }

    @Override
    public void updateTrigger(ModelObject object) throws ModelCheckerException, TransactionException, ModuleException {
        List<ModelObject> rules = object.getArray("rules");
        List<ModelObject> values = object.getArray("values");
        object.remove("rules");
        object.remove("values");
        object.setObjectClass(TableTrigger.class);
        object.checkUpdateThrowable();
        long id = object.getLongValue(TableTrigger.id);

        this.setRuleAndValue(object, rules, values, id);
        object.remove(TableTrigger.createTime);

        sessionTemplate.delete(Criteria.delete(TableTriggerRule.class).eq(TableTriggerRule.triggerId, id));
        sessionTemplate.delete(Criteria.delete(TableTriggerValue.class).eq(TableTriggerValue.triggerId, id));
        sessionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean invoke(Transaction transaction) throws Exception {
                sessionTemplate.update(object);
                if (rules != null && rules.size() > 0) sessionTemplate.save(rules);
                if (values != null && values.size() > 0) sessionTemplate.save(values);
                return null;
            }
        });
    }

    @Override
    public List<ModelObject> getEvents() {
        EventType[] types = EventType.values();
        List<ModelObject> objects = new ArrayList<>();
        for (EventType type : types) {
            ModelObject object = this.getEventObject(type);
            objects.add(object);
        }
        return objects;
    }

    private ModelObject getEventObject(EventType type) {
        ProcessorField[] fields = type.getParams();
        ModelObject object = new ModelObject();
        object.put("name", type.getMsg());
        object.put("code", type.getCode());
        object.put("value", type.name());
        if (fields != null) {
            List<ModelObject> params = new ArrayList<>();
            for (ProcessorField field : fields) {
                params.add(ModelObject.parseObject(ModelObject.toJSONString(field)));
            }
            object.put("params", params);
        }
        return object;
    }

    @Override
    public Paging getTriggers(Query query, long start, long limit) {
        if (query == null) {
            query = Criteria.query(TableTrigger.class);
        }
        query.limit(start, limit);
        query.order(TableTrigger.id, false);
        query.subjoin(TableTriggerRule.class).eq(TableTriggerRule.triggerId, TableTrigger.id);
        query.subjoin(TableTriggerValue.class).eq(TableTriggerValue.triggerId, TableTrigger.id);
        Paging paging = sessionTemplate.paging(query);
        List<ModelObject> objects = paging.getObjects();
        if (objects != null) {
            for (ModelObject object : objects) {
                object.put("eventName", EventType.valueOf(object.getString(TableTrigger.event)).getMsg());
                object.put("eventObject", getEventObject(EventType.valueOf(object.getString(TableTrigger.event))));
            }
        }
        return paging;
    }

    @Override
    public void deleteTrigger(long id) {
        sessionTemplate.delete(Criteria.delete(TableTriggerRule.class).eq(TableTriggerRule.triggerId, id));
        sessionTemplate.delete(TableTrigger.class, id);
        sessionTemplate.delete(Criteria.delete(TableTriggerValue.class).eq(TableTriggerValue.triggerId, id));
    }


    @Override
    public List<ModelObject> getProcessorImpls() {
        Map<String, ProcessorInterface> processorMap = context.getBeansOfType(ProcessorInterface.class);
        List<ModelObject> objects = new ArrayList<>();
        if (processorMap != null) {
            Iterator<Map.Entry<String, ProcessorInterface>> iterator = processorMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ProcessorInterface> entry = iterator.next();
                ProcessorInterface processor = entry.getValue();
                ProcessorField[] field = processor.getParams();
                ModelObject object = new ModelObject();
                object.put("name", processor.getName());
                object.put("type", processor.getValueType().getCode());
                object.put("value", processor.getClass().getName());
                object.put("params", ModelArray.toJSONString(field));
                object.put("detail", processor.getDetail());
                objects.add(object);
            }
        }
        return objects;
    }


    @Override
    public void runProcessor(ModelObject trigger, ModelObject params) throws ModuleException {
        List<ModelObject> triggerValues = sessionTemplate.list(
                Criteria.query(TableTriggerValue.class)
                        .eq(TableTriggerValue.triggerId, trigger.getLongValue(TableTrigger.id))
        );
        String className = trigger.getString(TableTrigger.processor);
        if (StringUtils.isNotBlank(className)) {
            String content = trigger.getString(TableTrigger.content);
            if (params == null) params = new ModelObject();
            ModelObject values = new ModelObject();
            if (triggerValues != null) {
                for (int i = 0; i < triggerValues.size(); i++) {
                    ModelObject v = triggerValues.get(i);
                    values.put(v.getString("code"), v.getString("value"));
                }
            }
            try {
                Class c = Class.forName(className);
                ProcessorInterface processorInterface = (ProcessorInterface) context.getBean(c);
                processorInterface.processor(trigger, values, params);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ModuleException(StockCode.FAILURE, "执行触发处理器出错", e);
            }
        }
    }

    @Override
    public String executeContentExpression(String content, ModelObject params) throws ModuleException {
        try {
            return FreeMarkerUtils.process(params, "template", content);
        } catch (Exception e) {
            throw new ModuleException(StockCode.FAILURE, "解析字符串模板出错", e);
        }
    }
}
