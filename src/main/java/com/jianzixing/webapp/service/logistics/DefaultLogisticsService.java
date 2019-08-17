package com.jianzixing.webapp.service.logistics;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.order.OrderGoodsModel;
import com.jianzixing.webapp.service.order.OrderModel;
import com.jianzixing.webapp.tables.goods.TableGoods;
import com.jianzixing.webapp.tables.logistics.*;
import com.jianzixing.webapp.tables.order.TableUserAddress;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.orm.transaction.Transaction;
import org.mimosaframework.orm.transaction.TransactionCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class DefaultLogisticsService implements LogisticsService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public List<ModelObject> getSimpleTemplates(String keyword) {
        Query query = Criteria.query(TableLogisticsTemplate.class);
        if (StringUtils.isNotBlank(keyword)) {
            query.like(TableLogisticsTemplate.name, "%" + keyword + "%");
        }
        query.limit(0, 25);
        return sessionTemplate.list(query);
    }

    @Override
    public Paging getTemplates(ModelObject search, long start, long limit) {
        Query query = Criteria.query(TableLogisticsTemplate.class);
        query.limit(start, limit);
        query.order(TableLogisticsTemplate.id, false);
        return sessionTemplate.paging(query);
    }

    @Override
    public ModelObject getCompanyById(int logisticsCompanyId) {
        return sessionTemplate.get(Criteria.query(TableLogisticsCompany.class)
                .eq(TableLogisticsCompany.id, logisticsCompanyId)
                .eq(TableLogisticsCompany.enable, 1));
    }

    @Override
    public ModelObject getCompanyByCode(String code) {
        return sessionTemplate.get(
                Criteria.query(TableLogisticsCompany.class)
                        .eq(TableLogisticsCompany.code, code)
                        .eq(TableLogisticsCompany.enable, 1)
        );
    }

    @Override
    public ModelObject getTemplateById(long tid) {
        ModelObject obj = sessionTemplate.get(
                Criteria.query(TableLogisticsTemplate.class)
                        .subjoin(TableLogisticsMoneyRule.class).eq(TableLogisticsMoneyRule.templateId, TableLogisticsTemplate.id).query()
                        .subjoin(TableLogisticsFreeRule.class).eq(TableLogisticsFreeRule.templateId, TableLogisticsTemplate.id).query()
                        .eq(TableLogisticsTemplate.id, tid)
        );
        if (obj != null) {
            List<ModelObject> moneyRules = obj.getArray(TableLogisticsMoneyRule.class.getSimpleName());
            List<ModelObject> freeRules = obj.getArray(TableLogisticsFreeRule.class.getSimpleName());
            if (moneyRules != null) {
                for (ModelObject moneyRule : moneyRules) {
                    long templateId = moneyRule.getLongValue(TableLogisticsMoneyRule.templateId);
                    long index = moneyRule.getLongValue(TableLogisticsMoneyRule.index);
                    List<ModelObject> areas = sessionTemplate.list(
                            Criteria.query(TableLogisticsAddress.class)
                                    .eq(TableLogisticsAddress.type, 1)
                                    .eq(TableLogisticsAddress.templateId, templateId)
                                    .eq(TableLogisticsAddress.index, index)
                    );
                    moneyRule.put("TableLogisticsAddress", areas);
                }
            }
            if (freeRules != null) {
                for (ModelObject freeRule : freeRules) {
                    long templateId = freeRule.getLongValue(TableLogisticsMoneyRule.templateId);
                    long index = freeRule.getLongValue(TableLogisticsMoneyRule.index);
                    List<ModelObject> areas = sessionTemplate.list(
                            Criteria.query(TableLogisticsAddress.class)
                                    .eq(TableLogisticsAddress.type, 2)
                                    .eq(TableLogisticsAddress.templateId, templateId)
                                    .eq(TableLogisticsAddress.index, index)
                    );
                    freeRule.put("TableLogisticsAddress", areas);
                }
            }
        }
        return obj;
    }

    private BigDecimal getProductLogistics(ModelObject template, ModelObject address, OrderGoodsModel goodsModel) {
        if (template != null && address != null && goodsModel != null) {
            long templateId = template.getLongValue(TableLogisticsTemplate.id);
            int free = template.getIntValue(TableLogisticsTemplate.free);
            int type = template.getIntValue(TableLogisticsTemplate.type);
            int deliveryType = goodsModel.getDeliveryType();
            int provinceCode = address.getIntValue(TableUserAddress.provinceCode);
            int cityCode = address.getIntValue(TableUserAddress.cityCode);

            if (free == 1) {
                return new BigDecimal(0);
            } else {
                int buyAmount = goodsModel.getBuyAmount();
                BigDecimal price = goodsModel.getTotalPrice();
                BigDecimal weight = goodsModel.getTotalWeight();
                BigDecimal volume = goodsModel.getTotalVolume();

                List<ModelObject> areas = sessionTemplate.list(
                        Criteria.query(TableLogisticsAddress.class)
                                .eq(TableLogisticsAddress.templateId, templateId)
                                .eq(TableLogisticsAddress.provinceCode, provinceCode)
                                .eq(TableLogisticsAddress.cityCode, cityCode));
                if (areas != null && areas.size() > 0) {
                    // 计算包邮条件
                    List<ModelObject> freeSourceRules = sessionTemplate.list(
                            Criteria.query(TableLogisticsFreeRule.class)
                                    .eq(TableLogisticsFreeRule.deliveryType, deliveryType)
                                    .eq(TableLogisticsFreeRule.templateId, templateId));
                    if (freeSourceRules != null && freeSourceRules.size() > 0) {
                        List<ModelObject> freeRules = new ArrayList<>();
                        for (ModelObject object : freeSourceRules) {
                            int index = object.getIntValue(TableLogisticsFreeRule.index);
                            boolean is = false;
                            for (ModelObject fa : areas) {
                                if (index == fa.getIntValue(TableLogisticsAddress.index)
                                        && fa.getIntValue(TableLogisticsAddress.type) == 2) {
                                    is = true;
                                }
                            }
                            if (is) {
                                freeRules.add(object);
                            }
                        }
                        for (ModelObject object : freeRules) {
                            int freeRule = object.getIntValue(TableLogisticsFreeRule.freeRule);
                            int condition = object.getIntValue(TableLogisticsFreeRule.condition);
                            double value1 = object.getDoubleValue(TableLogisticsFreeRule.value1);
                            double value2 = object.getDoubleValue(TableLogisticsFreeRule.value2);

                            double matchValue = 0;
                            if (freeRule == TemplateFreeRuleType.AMOUNT.getCode()) {
                                matchValue = buyAmount;
                            }
                            if (freeRule == TemplateFreeRuleType.WEIGHT.getCode()) {
                                matchValue = weight.doubleValue();
                            }
                            if (freeRule == TemplateFreeRuleType.VOLUME.getCode()) {
                                matchValue = volume.doubleValue();
                            }

                            if (condition == TemplateConditionType.TYPE.getCode()) {
                                if (value1 <= matchValue) {
                                    return new BigDecimal(0);
                                }
                            }
                            if (condition == TemplateConditionType.MONEY.getCode()) {
                                if (value2 <= price.doubleValue()) {
                                    return new BigDecimal(0);
                                }
                            }
                            if (condition == TemplateConditionType.TYPE_MONEY.getCode()) {
                                if (value1 <= matchValue && value2 <= price.doubleValue()) {
                                    return new BigDecimal(0);
                                }
                            }
                        }
                    }
                }

                // 如果不符合包邮条件则计算邮费
                List<ModelObject> templates = sessionTemplate.list(
                        Criteria.query(TableLogisticsMoneyRule.class)
                                .eq(TableLogisticsMoneyRule.templateId, templateId)
                                .eq(TableLogisticsMoneyRule.deliveryType, deliveryType));
                if (templates != null && templates.size() > 0) {
                    List<ModelObject> moneyRules = new ArrayList<>();
                    ModelObject defaultRule = null;
                    for (ModelObject object : templates) {
                        int index = object.getIntValue(TableLogisticsMoneyRule.index);
                        boolean is = false;
                        if (areas != null) {
                            for (ModelObject fa : areas) {
                                if (index == fa.getIntValue(TableLogisticsAddress.index)
                                        && fa.getIntValue(TableLogisticsAddress.type) == 1) {
                                    is = true;
                                }
                            }
                        }
                        if (is) {
                            moneyRules.add(object);
                        }
                        if (object.getIntValue(TableLogisticsMoneyRule.isDefault) == 1) {
                            defaultRule = object;
                        }
                    }

                    if (moneyRules.size() == 0) {
                        return getLogisticsTemplatePrice(type, defaultRule, goodsModel);
                    } else {
                        BigDecimal logisticsPrice = null;
                        for (ModelObject temp : moneyRules) {
                            BigDecimal rp = this.getLogisticsTemplatePrice(type, temp, goodsModel);
                            if (logisticsPrice == null) {
                                logisticsPrice = rp;
                            }
                            if (logisticsPrice != null) {
                                logisticsPrice = logisticsPrice.max(rp);
                            }
                        }
                        return logisticsPrice;
                    }
                }
            }
        }
        return null;
    }

    private BigDecimal getLogisticsTemplatePrice(int type, ModelObject rule, OrderGoodsModel goodsModel) {
        BigDecimal first = new BigDecimal(rule.getString(TableLogisticsMoneyRule.first));
        BigDecimal firstMoney = new BigDecimal(rule.getString(TableLogisticsMoneyRule.firstMoney));
        BigDecimal next = new BigDecimal(rule.getString(TableLogisticsMoneyRule.next));
        BigDecimal nextMoney = new BigDecimal(rule.getString(TableLogisticsMoneyRule.nextMoney));

        BigDecimal buyAmount = new BigDecimal(goodsModel.getBuyAmount());
        BigDecimal wight = goodsModel.getTotalWeight();
        BigDecimal volume = goodsModel.getTotalVolume();
        BigDecimal total = new BigDecimal(firstMoney.toPlainString());
        BigDecimal matchNumber = null;
        if (type == TemplateFreeRuleType.AMOUNT.getCode()) {
            matchNumber = buyAmount;
        }
        if (type == TemplateFreeRuleType.WEIGHT.getCode()) {
            matchNumber = wight;
        }
        if (type == TemplateFreeRuleType.VOLUME.getCode()) {
            matchNumber = volume;
        }

        if (matchNumber != null) {
            BigDecimal countBigDecimal = matchNumber.subtract(first).divide(next);
            int count = (int) Math.ceil(countBigDecimal.doubleValue());
            if (count > 0) {
                total = total.add(nextMoney.multiply(new BigDecimal(count)));
            }
        }
        return total;
    }

    @Override
    public LogisticsFreightModel calFreight(OrderModel orderModel, List<OrderGoodsModel> products) {
        LogisticsFreightModel model = new LogisticsFreightModel();
        ModelObject address = orderModel.getAddress();
        BigDecimal lp = null;
        for (OrderGoodsModel p : products) {
            ModelObject product = p.getGoods();
            long pwid = product.getLongValue(TableGoods.pwid);
            ModelObject template = sessionTemplate.get(TableLogisticsTemplate.class, pwid);
            BigDecimal price = this.getProductLogistics(template, address, p);
            if (price != null) {
                if (lp == null) {
                    lp = price;
                } else {
                    lp = lp.min(price);
                }
            }
        }
        model.setFreight(lp);
        return model;
    }

    @Override
    public List<ModelObject> getGoodsLogistics(List<Long> goodsIds) {
        Set<Long> pwids = new LinkedHashSet<>();
        List<ModelObject> goods = GlobalService.goodsService.getSimpleGoods(goodsIds);
        if (goods != null) {
            for (ModelObject g : goods) {
                long pwid = g.getLongValue(TableGoods.pwid);
                pwids.add(pwid);
            }
        }
        List<ModelObject> logistics = sessionTemplate.list(Criteria.query(TableLogisticsTemplate.class)
                .in(TableLogisticsTemplate.id, pwids));

        Map<String, ModelObject> rmap = new TreeMap<>();
        if (logistics != null) {
            for (ModelObject l : logistics) {
                String deliveryTypes = l.getString(TableLogisticsTemplate.deliveryType);
                int free = l.getIntValue(TableLogisticsTemplate.free);
                String[] types = deliveryTypes.split(",");
                for (String t : types) {
                    String key = t.trim();
                    ModelObject v = rmap.get(key);
                    if (v == null) {
                        v = new ModelObject();
                        int intType = Integer.parseInt(t);
                        v.put("type", intType);
                        v.put("free", free);
                        v.put("name", DeliveryType.get(intType).getMsg());
                        rmap.put(key, v);
                    } else {
                        if (free == 1) {
                            v.put("free", 1);
                        }
                    }
                }
            }
        }

        List<ModelObject> r = new ArrayList<>();
        Iterator<Map.Entry<String, ModelObject>> iterator = rmap.entrySet().iterator();
        while (iterator.hasNext()) {
            r.add(iterator.next().getValue());
        }

        return r;
    }

    @Override
    public List<ModelObject> getDeliveryTypes(List<ModelObject> products) {
        if (products != null) {
            List<Long> templateIds = new ArrayList<>();
            List<Long> gids = new ArrayList<>();
            for (ModelObject product : products) {
                gids.add(product.getLongValue("pid"));
            }
            List<ModelObject> goods = sessionTemplate.list(Criteria.query(TableGoods.class).in(TableGoods.id, gids));
            if (goods != null) {
                for (ModelObject g : goods) {
                    long pwid = g.getLongValue(TableGoods.pwid);
                    templateIds.add(pwid);
                }
            }

            if (templateIds.size() > 0) {
                List<ModelObject> templates = sessionTemplate.list(Criteria.query(TableLogisticsTemplate.class).in(TableLogisticsTemplate.id, templateIds));
                List<ModelObject> rules = sessionTemplate.list(Criteria.query(TableLogisticsMoneyRule.class).in(TableLogisticsMoneyRule.templateId, templateIds));
                Set<Integer> dts = new LinkedHashSet<>();
                if (templates != null) {
                    for (ModelObject object : templates) {
                        int free = object.getIntValue(TableLogisticsTemplate.free);
                        if (free == 1) {
                            dts.add(DeliveryType.FREE.getCode());
                        }
                    }
                }
                if (rules != null) {
                    for (ModelObject object : rules) {
                        int dt = object.getIntValue(TableLogisticsTemplate.deliveryType);
                        dts.add(dt);
                    }
                }
                List<ModelObject> r = new ArrayList();
                for (Integer i : dts) {
                    ModelObject o = new ModelObject();
                    o.put("name", DeliveryType.get(i).getMsg());
                    o.put("id", DeliveryType.get(i).getCode());
                    r.add(o);
                }
                return r;
            }
        }
        return null;
    }

    @Override
    public List<ModelObject> getCompanies() {
        return sessionTemplate.list(
                Criteria.query(TableLogisticsCompany.class)
                        .eq(TableLogisticsCompany.enable, 1)
                        .order(TableLogisticsCompany.id, false)
        );
    }

    @Override
    public void addCompany(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableLogisticsCompany.class);
        object.checkAndThrowable();

        String code = object.getString(TableLogisticsCompany.code);
        ModelObject old = sessionTemplate.get(
                Criteria.query(TableLogisticsCompany.class).eq(TableLogisticsCompany.code, code));
        if (old != null) {
            throw new ModuleException(StockCode.EXIST_OBJ, "物流公司标识码已经存在");
        }

        sessionTemplate.save(object);
    }

    @Override
    public void deleteCompany(int cid) {
        sessionTemplate.delete(TableLogisticsCompany.class, cid);
    }

    @Override
    public void updateCompany(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableLogisticsCompany.class);
        object.checkUpdateThrowable();
        String code = object.getString(TableLogisticsCompany.code);
        ModelObject old = sessionTemplate.get(
                Criteria.query(TableLogisticsCompany.class)
                        .eq(TableLogisticsCompany.code, code)
                        .ne(TableLogisticsCompany.id, object.getIntValue(TableLogisticsCompany.id))
        );
        if (old != null) {
            throw new ModuleException(StockCode.EXIST_OBJ, "物流公司标识码已经存在");
        }

        sessionTemplate.update(object);
    }

    @Override
    public List<ModelObject> getCompany() {
        return sessionTemplate.list(
                Criteria.query(TableLogisticsCompany.class)
                        .order(TableLogisticsCompany.id, false)
        );
    }

    @Override
    public void setDefaultCompany(int id) {
        sessionTemplate.update(
                Criteria.update(TableLogisticsCompany.class)
                        .eq(TableLogisticsCompany.isDefault, 1)
                        .value(TableLogisticsCompany.isDefault, 0)
        );
        ModelObject object = new ModelObject(TableLogisticsCompany.class);
        object.put(TableLogisticsCompany.id, id);
        object.put(TableLogisticsCompany.isDefault, 1);
        sessionTemplate.update(object);
    }

    @Override
    public void saveTemplate(ModelObject object) throws ModuleException, ModelCheckerException, TransactionException {
        object.setObjectClass(TableLogisticsTemplate.class);
        object.checkAndThrowable();

        long id = object.getLongValue(TableLogisticsTemplate.id);
        int isFree = object.getIntValue("free");
        object.put(TableLogisticsTemplate.deliveryType, "");

        List<ModelObject> moneyRuleObjects = null;
        List<ModelObject> freeRuleObjects = null;
        if (isFree != 1) {
            //保存邮费规则
            List<ModelObject> deliveryValue = object.getArray("deliveryValue");
            moneyRuleObjects = new ArrayList<>();
            if (deliveryValue != null && deliveryValue.size() > 0) {
                Set<String> deliveryTypeSet = new TreeSet<>();
                for (ModelObject deliveryTypeValue : deliveryValue) {
                    deliveryTypeValue.setObjectClass(TableLogisticsMoneyRule.class);
                    moneyRuleObjects.add(deliveryTypeValue);

                    int isDefault = deliveryTypeValue.getIntValue(TableLogisticsMoneyRule.isDefault);
                    int deliveryType = deliveryTypeValue.getIntValue(TableLogisticsMoneyRule.deliveryType);
                    String codes = deliveryTypeValue.getString("code");
                    if (StringUtils.isBlank(codes) && isDefault != 1) {
                        throw new ModuleException(StockCode.ARG_NULL, "非默认运费缺少地区信息");
                    }
                    if (StringUtils.isNotBlank(codes)) {
                        List<ModelObject> areas = makeRuleAreas(codes);
                        deliveryTypeValue.remove("code");
                        deliveryTypeValue.put("TableLogisticsAddress", areas);
                    }
                    deliveryTypeSet.add(String.valueOf(deliveryType).intern());
                    if (DeliveryType.getByType(deliveryType) == null) {
                        throw new ModuleException(StockCode.ARG_VALID, "运送方式类型[" + deliveryType + "]不存在");
                    }
                }
                String deliveryTypeStr = String.join(",", deliveryTypeSet);
                object.put(TableLogisticsTemplate.deliveryType, deliveryTypeStr);
            }

            freeRuleObjects = new ArrayList<>();
            if (object.getBoolean("isRuleFree")) {
                //保存免邮规则
                List<ModelObject> freeRuleValueList = object.getArray("freeCondition");
                if (freeRuleValueList != null && freeRuleValueList.size() > 0) {
                    for (ModelObject freeRuleValueItem : freeRuleValueList) {
                        freeRuleValueItem.setObjectClass(TableLogisticsFreeRule.class);
                        freeRuleObjects.add(freeRuleValueItem);
                        String codes = freeRuleValueItem.getString("code");
                        if (StringUtils.isBlank(codes)) {
                            throw new ModuleException(StockCode.ARG_NULL, "免邮规则缺少地区信息");
                        }
                        if (StringUtils.isNotBlank(codes)) {
                            List<ModelObject> areas = makeRuleAreas(codes);
                            freeRuleValueItem.remove("code");
                            freeRuleValueItem.put("TableLogisticsAddress", areas);
                        }
                    }
                }
            }

            if (moneyRuleObjects.size() == 0) {
                throw new ModuleException(StockCode.ARG_NULL, "缺少运费信息");
            }
        } else {
            List<ModelObject> deliveryValue = object.getArray("deliveryValue");
            moneyRuleObjects = new ArrayList<>();
            if (deliveryValue != null && deliveryValue.size() > 0) {
                Set<String> deliveryTypeSet = new TreeSet<>();
                for (ModelObject deliveryTypeValue : deliveryValue) {
                    int deliveryType = deliveryTypeValue.getIntValue(TableLogisticsMoneyRule.deliveryType);
                    deliveryTypeSet.add(String.valueOf(deliveryType).intern());
                    if (DeliveryType.getByType(deliveryType) == null) {
                        throw new ModuleException(StockCode.ARG_VALID, "运送方式类型[" + deliveryType + "]不存在");
                    }
                }
                if (deliveryTypeSet.size() == 0) {
                    throw new ModuleException(StockCode.ARG_NULL, "包邮时必须选择至少一个运送方式");
                }
                String deliveryTypeStr = String.join(",", deliveryTypeSet);
                object.put(TableLogisticsTemplate.deliveryType, deliveryTypeStr);
            }
        }

        List<ModelObject> finalMoneyRuleObjects = moneyRuleObjects;
        List<ModelObject> finalFreeRuleObjects = freeRuleObjects;
        sessionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object invoke(Transaction transaction) throws Exception {
                //保存模板信息
                if (id <= 0) {
                    object.put(TableLogisticsTemplate.createTime, new Date());
                    sessionTemplate.save(object);
                } else {
                    object.remove(TableLogisticsTemplate.createTime);
                    sessionTemplate.update(object);
                    sessionTemplate.delete(Criteria.delete(TableLogisticsMoneyRule.class).eq(TableLogisticsMoneyRule.templateId, id));
                    sessionTemplate.delete(Criteria.delete(TableLogisticsFreeRule.class).eq(TableLogisticsFreeRule.templateId, id));
                    sessionTemplate.delete(Criteria.delete(TableLogisticsAddress.class).eq(TableLogisticsAddress.templateId, id));
                }
                long templateId = object.getLongValue(TableLogisticsTemplate.id);
                if (isFree != 1 && finalMoneyRuleObjects != null) {
                    int index = 0;
                    for (ModelObject moneyRule : finalMoneyRuleObjects) {
                        moneyRule.put(TableLogisticsMoneyRule.templateId, templateId);
                        moneyRule.put(TableLogisticsMoneyRule.index, ++index);
                        List<ModelObject> areas = moneyRule.getArray("TableLogisticsAddress");
                        moneyRule.remove("TableLogisticsAddress");
                        moneyRule.checkAndThrowable();
                        sessionTemplate.save(moneyRule);
                        if (areas != null) {
                            for (ModelObject area : areas) {
                                area.put(TableLogisticsAddress.templateId, templateId);
                                area.put(TableLogisticsAddress.index, moneyRule.getIntValue(TableLogisticsMoneyRule.index));
                                area.put(TableLogisticsAddress.type, 1);
                            }
                            sessionTemplate.save(areas);
                        }
                    }
                }
                if (isFree != 1 && finalFreeRuleObjects != null) {
                    int index = 0;
                    for (ModelObject freeRule : finalFreeRuleObjects) {
                        freeRule.put(TableLogisticsFreeRule.templateId, templateId);
                        freeRule.put(TableLogisticsFreeRule.index, ++index);
                        List<ModelObject> areas = freeRule.getArray("TableLogisticsAddress");
                        freeRule.remove("TableLogisticsAddress");
                        freeRule.checkAndThrowable();
                        sessionTemplate.save(freeRule);
                        if (areas != null) {
                            for (ModelObject area : areas) {
                                area.put(TableLogisticsAddress.templateId, templateId);
                                area.put(TableLogisticsAddress.index, freeRule.getIntValue(TableLogisticsFreeRule.index));
                                area.put(TableLogisticsAddress.type, 2);
                            }
                            sessionTemplate.save(areas);
                        }
                    }
                }
                return null;
            }
        });
    }

    private List<ModelObject> makeRuleAreas(String codes) throws ModuleException {
        String[] codeArr = codes.split(",");
        List<ModelObject> objects = GlobalService.areaService.getLineAreaByCode(Arrays.asList(codeArr));
        if (objects == null || objects.size() == 0) {
            throw new ModuleException(StockCode.ARG_NULL, "地区编码不正确");
        }

        List<ModelObject> logisticsAddress = new ArrayList<>();
        for (ModelObject object : objects) {
            ModelObject la = new ModelObject(TableLogisticsAddress.class);
            la.put(TableLogisticsAddress.country, object.getString("countryName"));
            la.put(TableLogisticsAddress.countryCode, object.getString("countryCode"));
            la.put(TableLogisticsAddress.province, object.getString("provinceName"));
            la.put(TableLogisticsAddress.provinceCode, object.getString("provinceCode"));
            la.put(TableLogisticsAddress.city, object.getString("cityName"));
            la.put(TableLogisticsAddress.cityCode, object.getString("cityCode"));
            logisticsAddress.add(la);
        }
        return logisticsAddress;
    }

    @Override
    public void deleteTemplate(int id) {
        //保存模板信息
        sessionTemplate.delete(TableLogisticsTemplate.class, id);

        sessionTemplate.delete(TableLogisticsMoneyRule.class).eq(TableLogisticsMoneyRule.templateId, id);
        sessionTemplate.delete(TableLogisticsFreeRule.class).eq(TableLogisticsFreeRule.templateId, id);
    }

    @Override
    public List<ModelObject> getTemplate() {
        return sessionTemplate.list(
                Criteria.query(TableLogisticsTemplate.class)
                        .order(TableLogisticsTemplate.createTime, false)
        );
    }
}
