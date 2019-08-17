package com.jianzixing.webapp.service.discount;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.order.*;
import com.jianzixing.webapp.tables.discount.*;
import com.jianzixing.webapp.tables.goods.TableGoods;
import com.jianzixing.webapp.tables.goods.TableGoodsGroup;
import com.jianzixing.webapp.tables.goods.TableGoodsSku;
import com.jianzixing.webapp.tables.user.TableUser;
import com.jianzixing.webapp.tables.user.TableUserLevel;
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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DefaultDiscountService implements DiscountService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Autowired
    ApplicationContext context;

    @Override
    public void addDiscount(ModelObject object) throws ModelCheckerException, ModuleException, TransactionException {
        String startTime = object.getString(TableDiscount.startTime);
        String finishTime = object.getString(TableDiscount.finishTime);
        String impl = object.getString(TableDiscount.impl);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (startTime == null && finishTime == null) {
            throw new ModuleException(StockCode.ARG_NULL, "必须设置开始时间和结束时间");
        }
        if (StringUtils.isBlank(impl)) {
            throw new ModuleException(StockCode.ARG_NULL, "缺少优惠活动实现");
        }

        try {
            format.parse(startTime);
            format.parse(finishTime);
        } catch (ParseException e) {
            throw new ModuleException(StockCode.FORMAT, "开始时间或者结束时间日期格式错误");
        }

        List gids = object.getArray("gids");
        List<ModelObject> gidAdds = object.getArray("gidIncludes");
        List<Integer> bids = object.getArray("bids");
        List<ModelObject> bidExcludes = object.getArray("bidExcludes");
        List<Integer> cids = object.getArray("cids");
        List<ModelObject> cidExcludes = object.getArray("cidExcludes");
        ModelObject params = object.getModelObject("params");

        List userLevels = object.getArray("userLevels");
        List platforms = object.getArray("platforms");
        List<ModelObject> userLevelObjs = new ArrayList<>();
        List<ModelObject> platformsObjs = new ArrayList<>();
        if (userLevels != null && userLevels.size() > 0) {
            boolean isAllLevel = false;
            for (int i = 0; i < userLevels.size(); i++) {
                if (String.valueOf(userLevels.get(i)).equals("0")) {
                    isAllLevel = true;
                    break;
                }
                ModelObject userLevel = new ModelObject(TableDiscountUserLevel.class);
                userLevel.put(TableDiscountUserLevel.ulid, userLevels.get(i));
                userLevelObjs.add(userLevel);
            }
            if (isAllLevel) {
                userLevelObjs.clear();
                ModelObject userLevel = new ModelObject(TableDiscountUserLevel.class);
                userLevel.put(TableDiscountUserLevel.ulid, 0);
                userLevelObjs.add(userLevel);
            }
        } else {
            // 如果没有设置所有等级,则属于所有用户等级都可以用
            ModelObject userLevel = new ModelObject(TableDiscountUserLevel.class);
            userLevel.put(TableDiscountUserLevel.ulid, 0);
            userLevelObjs.add(userLevel);
        }

        if (platforms != null && platforms.size() > 0) {
            boolean isAllPlatform = false;
            for (int i = 0; i < platforms.size(); i++) {
                if (String.valueOf(platforms.get(i)).equals("0")) {
                    isAllPlatform = true;
                    break;
                }
                ModelObject platform = new ModelObject(TableDiscountPlatform.class);
                platform.put(TableDiscountPlatform.ptype, platforms.get(i));
                platformsObjs.add(platform);
            }
            if (isAllPlatform) {
                platformsObjs.clear();
                ModelObject platform = new ModelObject(TableDiscountPlatform.class);
                platform.put(TableDiscountPlatform.ptype, 0);
                platformsObjs.add(platform);
            }
        } else {
            // 如果没有设置平台，则所有平台都可以使用
            ModelObject platform = new ModelObject(TableDiscountPlatform.class);
            platform.put(TableDiscountPlatform.ptype, 0);
            platformsObjs.add(platform);
        }

        int type = object.getIntValue(TableDiscount.type);
        if (type != DiscountTypes.GOODS_CLASSIFY.getCode()
                && type != DiscountTypes.GOODS.getCode()
                && type != DiscountTypes.BRAND.getCode()) {
            throw new ModuleException(StockCode.NOT_SUPPORT, "不支持的活动类型");
        }

        if (type == DiscountTypes.GOODS_CLASSIFY.getCode()) {
            if (cids == null || cids.size() == 0) {
                throw new ModuleException(StockCode.ARG_NULL, "商品分类不能为空");
            }
        }

        if (type == DiscountTypes.GOODS.getCode()) {
            if ((gids == null || gids.size() == 0) && (gidAdds == null || gidAdds.size() == 0)) {
                throw new ModuleException(StockCode.ARG_NULL, "活动商品不能为空");
            }
        }

        if (type == DiscountTypes.BRAND.getCode()) {
            if (bids == null || bids.size() == 0) {
                throw new ModuleException(StockCode.ARG_NULL, "活动品牌不能为空");
            }
        }

        DiscountCalculateInterface calculateInterface = this.getImpl(impl);
        if (calculateInterface == null) {
            throw new ModuleException(StockCode.ARG_NULL, "不支持的优惠活动实现");
        }
        calculateInterface.checkSaveParams(params);
        object.put(TableDiscount.view, calculateInterface.getView());
        object.put(TableDiscount.params, params.toJSONString());
        object.put(TableDiscount.implName, calculateInterface.getName());

        object.setObjectClass(TableDiscount.class);
        object.checkAndThrowable();
        object.put(TableDiscount.createTime, new Date());

        sessionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean invoke(Transaction transaction) throws Exception {
                sessionTemplate.save(object);
                int enable = object.getIntValue(TableDiscount.enable);
                long did = object.getLongValue(TableDiscount.id);
                if (type == DiscountTypes.GOODS_CLASSIFY.getCode()) {
                    List<ModelObject> records = new ArrayList<>();
                    for (Integer i : cids) {
                        ModelObject r = new ModelObject(TableDiscountGoods.class);
                        r.put(TableDiscountGoods.did, did);
                        r.put(TableDiscountGoods.tid, "C" + i);
                        r.put(TableDiscountGoods.type, "C");
                        r.put(TableDiscountGoods.enable, enable);
                        records.add(r);
                    }

                    if (cidExcludes != null) {
                        for (ModelObject goods : cidExcludes) {
                            long gid = 0;
                            long skuId = 0;
                            if (goods.getObjectClass().equals(TableGoods.class)) {
                                gid = goods.getLongValue(TableGoods.id);
                            }
                            ModelObject r = new ModelObject(TableDiscountGoods.class);
                            r.put(TableDiscountGoods.did, did);
                            r.put(TableDiscountGoods.tid, "E" + gid);
                            r.put(TableDiscountGoods.type, "E");
                            r.put(TableDiscountGoods.enable, enable);
                            records.add(r);
                        }
                    }

                    sessionTemplate.save(records);
                }
                if (type == DiscountTypes.GOODS.getCode()) {
                    List<ModelObject> records = new ArrayList<>();
                    for (Object i : gids) {
                        ModelObject r = new ModelObject(TableDiscountGoods.class);
                        r.put(TableDiscountGoods.did, did);
                        r.put(TableDiscountGoods.tid, "G" + i);
                        r.put(TableDiscountGoods.type, "G");
                        r.put(TableDiscountGoods.enable, enable);
                        records.add(r);
                    }

                    if (gidAdds != null) {
                        for (ModelObject goods : gidAdds) {
                            long gid = 0;
                            long skuId = 0;
                            if (goods.getObjectClass().equals(TableGoods.class)) {
                                gid = goods.getLongValue(TableGoods.id);
                            }
                            if (goods.getObjectClass().equals(TableGoodsSku.class)) {
                                gid = goods.getLongValue(TableGoodsSku.goodsId);
                                skuId = goods.getLongValue(TableGoodsSku.id);
                            }
                            ModelObject r = new ModelObject(TableDiscountGoods.class);
                            r.put(TableDiscountGoods.did, did);
                            if (skuId == 0) {
                                r.put(TableDiscountGoods.tid, "G" + gid);
                                r.put(TableDiscountGoods.type, "G");
                            } else {
                                r.put(TableDiscountGoods.tid, "G" + gid + "S" + skuId);
                                r.put(TableDiscountGoods.type, "GS");
                            }
                            r.put(TableDiscountGoods.enable, enable);
                            records.add(r);
                        }
                    }

                    sessionTemplate.save(records);
                }
                if (type == DiscountTypes.BRAND.getCode()) {
                    List<ModelObject> records = new ArrayList<>();
                    for (Integer i : bids) {
                        ModelObject r = new ModelObject(TableDiscountGoods.class);
                        r.put(TableDiscountGoods.did, did);
                        r.put(TableDiscountGoods.tid, "B" + i);
                        r.put(TableDiscountGoods.type, "B");
                        r.put(TableDiscountGoods.enable, enable);
                        records.add(r);
                    }

                    if (bidExcludes != null) {
                        for (ModelObject goods : bidExcludes) {
                            long gid = 0;
                            long skuId = 0;
                            if (goods.getObjectClass().equals(TableGoods.class)) {
                                gid = goods.getLongValue(TableGoods.id);
                            }
                            ModelObject r = new ModelObject(TableDiscountGoods.class);
                            r.put(TableDiscountGoods.did, did);
                            r.put(TableDiscountGoods.tid, "E" + gid);
                            r.put(TableDiscountGoods.type, "E");
                            r.put(TableDiscountGoods.enable, enable);
                            records.add(r);
                        }
                    }

                    sessionTemplate.save(records);
                }

                if (userLevelObjs.size() > 0) {
                    for (ModelObject userLevel : userLevelObjs) userLevel.put(TableDiscountUserLevel.did, did);
                    sessionTemplate.save(userLevelObjs);
                }
                if (platformsObjs.size() > 0) {
                    for (ModelObject platform : platformsObjs) platform.put(TableDiscountUserLevel.did, did);
                    sessionTemplate.save(platformsObjs);
                }
                return true;
            }
        });
    }

    @Override
    public void deleteDiscount(long id) {
        ModelObject object = new ModelObject(TableDiscount.class);
        object.put(TableDiscount.id, id);
        object.put(TableDiscount.isDel, 1);
        sessionTemplate.update(object);
    }

    /**
     * 一旦活动创建后审核后(暂时没有)不允许在修改关键信息
     * 只允许修改名称活动描述和开始结束时间
     *
     * @param object
     * @throws ModelCheckerException
     */
    @Override
    public void updateDiscount(ModelObject object) throws ModelCheckerException {
        List<ModelObject> params = object.getArray("params");

        object.setObjectClass(TableDiscount.class);
        object.checkUpdateThrowable();
        object.remove(TableDiscount.isDel);
        object.remove(TableDiscount.type);
        object.remove(TableDiscount.impl);
        object.remove(TableDiscount.createTime);
        sessionTemplate.update(object);
    }

    @Override
    public Paging getDiscounts(ModelObject search, long start, long limit) {
        Query query = Criteria.query(TableDiscount.class);
        query.limit(start, limit);
        query.order(TableDiscount.id, false);

        if (search != null) {
            search.clearEmpty();
            if (search.isNotEmpty("name"))
                query.like(TableDiscount.name, "%" + search.getString("name") + "%");
            if (search.isNotEmpty("type"))
                query.eq(TableDiscount.type, search.getIntValue("type"));
            if (search.isNotEmpty("detail"))
                query.like(TableDiscount.detail, "%" + search.getString("detail") + "%");
            if (search.isNotEmpty("sTimeStart"))
                query.gte(TableDiscount.startTime, search.getString("sTimeStart"));
            if (search.isNotEmpty("eTimeEnd"))
                query.lte(TableDiscount.finishTime, search.getString("eTimeEnd"));
            if (search.isNotEmpty("createTimeStart"))
                query.gte(TableDiscount.createTime, search.getString("createTimeStart"));
            if (search.isNotEmpty("createTimeEnd"))
                query.lte(TableDiscount.createTime, search.getString("createTimeEnd"));
        }

        Paging paging = sessionTemplate.paging(query);
        List<ModelObject> impls = this.getImpls();
        if (paging != null && paging.getObjects() != null) {
            List<ModelObject> discounts = paging.getObjects();
            if (discounts != null) {
                for (ModelObject discount : discounts) {
                    String impl = discount.getString(TableDiscount.impl);
                    for (ModelObject ipo : impls) {
                        if (impl.equals(ipo.getString("impl"))) {
                            discount.put("implName", ipo.getString("name"));
                        }
                    }
                }
            }
        }

        return paging;
    }

    private boolean getTimeValid(Date start, Date finish) {
        if (start != null && start.getTime() >= System.currentTimeMillis()) {
            return false;
        }
        if (finish != null && finish.getTime() <= System.currentTimeMillis()) {
            return false;
        }
        return true;
    }

    /**
     * 检查关联的商品，如果活动失效则关联的商品活动也失效
     */
    @Override
    public void checkDiscountValid() {
        long start = 0;
        long limit = 500;
        while (true) {
            List<ModelObject> valids = sessionTemplate.list(Criteria.query(TableDiscount.class)
                    .eq(TableDiscount.isDel, 0)
                    .eq(TableDiscount.type, 1)
                    // .eq(TableDiscount.enable, 1)
                    .limit(start, limit));
            if (valids == null) {
                break;
            } else {
                for (ModelObject v : valids) {
                    long id = v.getIntValue(TableDiscount.id);
                    int enable = v.getIntValue(TableDiscount.enable);
                    if (enable == 0 || enable == 3) {
                        sessionTemplate.update(Criteria.update(TableDiscountGoods.class)
                                .eq(TableDiscountGoods.did, id).value(TableDiscountGoods.enable, 0));
                        continue;
                    }
                    Date startTime = v.getDate(TableDiscount.startTime);
                    Date finishTime = v.getDate(TableDiscount.finishTime);
                    if (!this.getTimeValid(startTime, finishTime)) {
                        // 过期后就设置过期
                        sessionTemplate.update(Criteria.update(TableDiscount.class)
                                .eq(TableDiscount.id, id).value(TableDiscount.enable, 3));
                    } else {
                        // 如果有效就设置有效
                    }
                }
            }
            start += limit;
        }
    }

    @Override
    public List<ModelObject> getImpls() {
        Map<String, DiscountCalculateInterface> beans = context.getBeansOfType(DiscountCalculateInterface.class);
        if (beans != null) {
            List<ModelObject> objects = new ArrayList<>();
            for (Map.Entry<String, DiscountCalculateInterface> entry : beans.entrySet()) {
                ModelObject object = new ModelObject();
                object.put("name", entry.getValue().getName());
                object.put("impl", entry.getValue().getClass().getName());
                object.put("view", entry.getValue().getView());
                objects.add(object);
            }
            return objects;
        }
        return null;
    }

    public DiscountCalculateInterface getImpl(String impl) {
        Map<String, DiscountCalculateInterface> beans = context.getBeansOfType(DiscountCalculateInterface.class);
        if (beans != null) {
            for (Map.Entry<String, DiscountCalculateInterface> entry : beans.entrySet()) {
                if (entry.getValue().getClass().getName().equals(impl)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void enableDiscount(long id) throws ModuleException, TransactionException {
        ModelObject old = sessionTemplate.get(TableDiscount.class, id);
        if (old != null && old.getIntValue(TableDiscount.enable) == 3) {
            throw new ModuleException(StockCode.STATUS_ERROR, "已过期活动" + old.getString(TableDiscount.name) + "不允许启用");
        }
        ModelObject object = new ModelObject(TableDiscount.class);
        object.put(TableDiscount.id, id);
        object.put(TableDiscount.enable, 1);
        sessionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean invoke(Transaction transaction) throws Exception {
                sessionTemplate.update(object);
                sessionTemplate.update(Criteria.update(TableDiscountGoods.class)
                        .eq(TableDiscountGoods.did, id).value(TableDiscountGoods.enable, 1));
                return null;
            }
        });
    }

    @Override
    public void disableDiscount(long id) throws ModuleException, TransactionException {
        ModelObject old = sessionTemplate.get(TableDiscount.class, id);
        if (old != null && old.getIntValue(TableDiscount.enable) == 3) {
            throw new ModuleException(StockCode.STATUS_ERROR, "已过期活动" + old.getString(TableDiscount.name) + "不允许禁用");
        }
        ModelObject object = new ModelObject(TableDiscount.class);
        object.put(TableDiscount.id, id);
        object.put(TableDiscount.enable, 0);
        sessionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean invoke(Transaction transaction) throws Exception {

                sessionTemplate.update(object);
                sessionTemplate.update(Criteria.update(TableDiscountGoods.class)
                        .eq(TableDiscountGoods.did, id).value(TableDiscountGoods.enable, 0));
                return null;
            }
        });
    }

    @Override
    public Paging getDiscountGoods(long did, int start, int limit) {
        ModelObject coupon = sessionTemplate.get(Criteria.query(TableDiscount.class)
                .eq(TableDiscount.id, did));

        if (coupon != null) {
            Paging paging = null;
            int type = coupon.getIntValue(TableDiscount.type);
            if (type == 0) { // 商品分类
                List<ModelObject> ids = sessionTemplate.list(
                        Criteria.query(TableDiscountGoods.class)
                                .eq(TableDiscountGoods.did, did)
                                .eq(TableDiscountGoods.type, "C"));
                if (ids != null) {
                    List<Integer> goodsGroupIdList = new ArrayList<>();
                    for (ModelObject idObj : ids) {
                        String idStr = idObj.getString(TableDiscountGoods.tid);
                        if (idStr.startsWith("C")) {
                            goodsGroupIdList.add(Integer.parseInt(idStr.substring(1)));
                        }
                    }

                    paging = GlobalService.goodsService.getGoodsInGroups(goodsGroupIdList, start, limit);
                }
            }
            if (type == 1) { // 商品
                Paging idsPaging = sessionTemplate.paging(
                        Criteria.query(TableDiscountGoods.class)
                                .eq(TableDiscountGoods.did, did)
                                .in(TableDiscountGoods.type, "G", "GS")
                                .limit(start, limit));
                if (idsPaging != null && idsPaging.getObjects() != null) {
                    List<ModelObject> ids = idsPaging.getObjects();
                    Map<Long, Long> goodsIdList = new LinkedHashMap<>();
                    for (ModelObject idObj : ids) {
                        String idStr = idObj.getString(TableDiscountGoods.tid);
                        if (idStr.startsWith("G")) {
                            idStr = idStr.substring(1);
                            if (idStr.indexOf("S") > 0) {
                                String[] s = idStr.split("S");
                                goodsIdList.put(Long.parseLong(s[0]), Long.parseLong(s[1]));
                            } else {
                                goodsIdList.put(Long.parseLong(idStr), 0l);
                            }
                        }
                    }

                    List<ModelObject> goods = GlobalService.goodsService.getGoodsWithSku(goodsIdList, "discountSku");
                    paging = idsPaging;
                    idsPaging.setObjects(goods);
                }
            }
            if (type == 2) { // 品牌
                List<ModelObject> ids = sessionTemplate.list(
                        Criteria.query(TableDiscountGoods.class)
                                .eq(TableDiscountGoods.did, did)
                                .eq(TableDiscountGoods.type, "B"));
                if (ids != null) {
                    List<Integer> goodsGroupIdList = new ArrayList<>();
                    for (ModelObject idObj : ids) {
                        String idStr = idObj.getString(TableDiscountGoods.tid);
                        if (idStr.startsWith("B")) {
                            goodsGroupIdList.add(Integer.parseInt(idStr.substring(1)));
                        }
                    }

                    paging = GlobalService.goodsService.getGoodsInGroups(goodsGroupIdList, start, limit);
                }
            }

            if (paging != null) {
                List<String> excludes = new ArrayList<>();
                List<ModelObject> objects = paging.getObjects();
                if (objects != null) {
                    for (ModelObject object : objects) {
                        excludes.add("E" + object.getString(TableGoods.id));
                        object.put("exclude", 0);
                    }
                }

                List<ModelObject> relGoods = sessionTemplate.list(Criteria.query(TableDiscountGoods.class)
                        .eq(TableDiscountGoods.did, did)
                        .eq(TableDiscountGoods.type, "E")
                        .in(TableDiscountGoods.tid, excludes));

                if (relGoods != null) {
                    for (ModelObject relG : relGoods) {
                        for (ModelObject object : objects) {
                            if (("E" + object.getString(TableGoods.id)).equals(relG.getString(TableDiscountGoods.tid))) {
                                object.put("exclude", 1);
                            }
                        }
                    }
                }
            }
            return paging;
        }

        return null;
    }

    public Set<String> getGoodsDiscountKeySet(ModelObject goods) {
        long gid = goods.getLongValue(TableGoods.id);
        long groupId = goods.getLongValue(TableGoods.gid);
        ModelObject group = goods.getModelObject(TableGoodsGroup.class.getSimpleName());
        if (group == null) {
            group = GlobalService.goodsGroupService.getGroupById(groupId);
        }
        Set<String> ids = new LinkedHashSet<>();
        if (group != null) {
            String idStr = group.getString(TableGoodsGroup.list);
            if (StringUtils.isNotBlank(idStr)) {
                String[] idStrs = idStr.split(",");
                for (String gidStr : idStrs) {
                    ids.add("C" + gidStr.trim());
                }
            }
            ids.add("C" + group.getIntValue(TableGoodsGroup.id));
        }
        ids.add("C0");  // C0表示商品分类的根目录，如果C0有活动就代表所有商品都有活动
        ids.add("G" + gid); // 商品ID
        ids.add("E" + gid); // 排除的商品ID

        // 获取商品品牌
        int bid = goods.getIntValue(TableGoods.bid);
        ids.add("B" + bid);

        // SKU类型
        Object skuObj = goods.get(TableGoodsSku.class.getSimpleName());
        if (skuObj != null) {
            if (skuObj instanceof ModelObject) {
                long skuId = ((ModelObject) skuObj).getLongValue(TableGoodsSku.id);
                ids.add("G" + gid + "S" + skuId);
            }
            if (skuObj instanceof List) {
                List skus = (List) skuObj;
                for (int i = 0; i < skus.size(); i++) {
                    long skuId = ((ModelObject) ((List) skuObj).get(i)).getLongValue(TableGoodsSku.id);
                    ids.add("G" + gid + "S" + skuId);
                }
            }
        }
        return ids;
    }

    @Override
    public Set<String> getGoodsDiscountKeySet(List<ModelObject> goods) {
        Set<String> goodsIds = new LinkedHashSet<>();
        for (ModelObject gl : goods) {
            Set<String> sets = this.getGoodsDiscountKeySet(gl);
            if (sets != null) {
                goodsIds.addAll(sets);
            }
        }
        return goodsIds;
    }

    @Override
    public Map<ModelObject, List<ModelObject>> getUserDiscountByGoods(ModelObject user,
                                                                      List<ModelObject> goodsAndSku,
                                                                      PlatformType platformType,
                                                                      List<Long> restrictDid,
                                                                      boolean reverse) {
        Set<String> idsList = new LinkedHashSet<>();
        Map<Long, Set<String>> idsMap = new HashMap<>();
        for (ModelObject goods : goodsAndSku) {
            long gid = goods.getLongValue(TableGoods.id);
            Set<String> ids = this.getGoodsDiscountKeySet(goods);
            idsList.addAll(ids);
            idsMap.put(gid, ids);
        }

        // 获得当前商品和sku的所有活动配置
        Query query = Criteria.query(TableDiscountGoods.class)
                .in(TableDiscountGoods.tid, idsList)
                .eq(TableDiscountGoods.enable, 1);
        if (restrictDid != null && restrictDid.size() > 0) {
            query.in(TableDiscountGoods.did, restrictDid);
        }
        List<ModelObject> discountGoodsList = sessionTemplate.list(query);

        if (discountGoodsList != null) {
            Set<Long> didSet = new LinkedHashSet<>();
            Map<Long, Set<Long>> didMaps = new LinkedHashMap<>();

            for (Map.Entry<Long, Set<String>> entry : idsMap.entrySet()) {
                Set<String> ids = entry.getValue();
                Set<Long> didMapSet = new LinkedHashSet<>();
                for (ModelObject discountGoods : discountGoodsList) {
                    long did = discountGoods.getLongValue(TableDiscountGoods.did);
                    String tid = discountGoods.getString(TableDiscountGoods.tid);
                    boolean bool = false;
                    if (restrictDid != null && restrictDid.contains(did)) {
                        bool = true;
                    }
                    if (restrictDid == null || restrictDid.size() <= 0) {
                        bool = true;
                    }
                    if (bool) {
                        if (ids.contains(tid)) {
                            didMapSet.add(did);
                        }
                        if (tid.startsWith("E") && ids.contains(tid)) {
                            didMapSet.remove(did);
                        }
                    }
                }
                didMaps.put(entry.getKey(), didMapSet);
                didSet.addAll(didMapSet);
            }

            // 获得配置的活动信息
            List<ModelObject> sourceDiscounts = sessionTemplate.list(Criteria.query(TableDiscount.class)
                    .in(TableDiscount.id, didSet)
                    .eq(TableDiscount.isDel, 0)
                    .eq(TableDiscount.enable, 1));

            // 判断当前平台是否支持
            List<ModelObject> platforms = null;
            if (platformType != PlatformType.ALL) {
                platforms = sessionTemplate.list(
                        Criteria.query(TableDiscountPlatform.class)
                                .in(TableDiscountPlatform.did, didSet)
                                .in(TableDiscountPlatform.ptype, platformType.getCode(), 0));
            }

            // 判断当前用户等级是否支持
            List<ModelObject> userLevels = null;
            if (user != null) {
                ModelObject userLevel = GlobalService.userLevelService.getUserLevel(user);
                if (userLevel != null) {
                    userLevels = sessionTemplate.list(
                            Criteria.query(TableDiscountUserLevel.class)
                                    .in(TableDiscountUserLevel.did, didSet)
                                    .in(TableDiscountUserLevel.ulid, userLevel.getIntValue(TableUserLevel.id), 0)
                    );
                }
            }

            if (sourceDiscounts != null) {
                List<ModelObject> discounts = new ArrayList<>(sourceDiscounts);

                List<ModelObject> removeTimeValid = new ArrayList<>();
                for (ModelObject discount : discounts) {
                    long did = discount.getLongValue(TableDiscount.id);
                    Date start = discount.getDate(TableDiscount.startTime);
                    Date finish = discount.getDate(TableDiscount.finishTime);

                    if (platforms != null) {
                        boolean hasPlatform = false;
                        for (ModelObject platform : platforms) {
                            long pdid = platform.getLongValue(TableDiscountPlatform.did);
                            if (did == pdid) {
                                hasPlatform = true;
                            }
                        }
                        if (!hasPlatform) {
                            removeTimeValid.add(discount);
                            continue;
                        }
                    }

                    if (userLevels != null) {
                        boolean isCanUser = false;
                        for (ModelObject dul : userLevels) {
                            long userLevelDid = dul.getLongValue(TableDiscountUserLevel.did);
                            if (userLevelDid == did) {
                                isCanUser = true;
                                break;
                            }
                        }
                        if (!isCanUser) {
                            removeTimeValid.add(discount);
                            continue;
                        }
                    } else {
                        // 如果没有配置用户等级则直接判断失效
                        removeTimeValid.add(discount);
                        continue;
                    }

                    // 如果已经过期则排除
                    if (!this.getTimeValid(start, finish)) {
                        removeTimeValid.add(discount);
                    }
                }
                discounts.removeAll(removeTimeValid);

                // key = discount , values = goods
                Map<ModelObject, List<ModelObject>> discountMap = new LinkedHashMap<>();

                if (reverse) {
                    for (Map.Entry<Long, Set<Long>> entry : didMaps.entrySet()) {
                        ModelObject goods = null;
                        for (ModelObject g : goodsAndSku) {
                            if (g.getLongValue(TableGoods.id) == entry.getKey()) {
                                goods = g;
                            }
                        }
                        if (goods != null) {
                            Set<Long> values = entry.getValue();
                            for (long value : values) {
                                for (ModelObject d : discounts) {
                                    if (d.getLongValue(TableDiscount.id) == value) {
                                        List<ModelObject> mapGoods = discountMap.get(d);
                                        if (mapGoods == null) mapGoods = new ArrayList<>();
                                        mapGoods.add(goods);
                                        discountMap.put(d, mapGoods);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (Map.Entry<Long, Set<Long>> entry : didMaps.entrySet()) {
                        ModelObject goods = null;
                        for (ModelObject g : goodsAndSku) {
                            if (g.getLongValue(TableGoods.id) == entry.getKey()) {
                                goods = g;
                            }
                        }
                        if (goods != null) {
                            Set<Long> values = entry.getValue();
                            List<ModelObject> discountInGoods = new ArrayList<>();
                            for (long value : values) {
                                for (ModelObject d : discounts) {
                                    if (d.getLongValue(TableDiscount.id) == value) {
                                        discountInGoods.add(d);
                                    }
                                }
                            }
                            discountMap.put(goods, discountInGoods);
                        }
                    }
                }
                return discountMap;
            }
        }
        return null;
    }

    @Override
    public Map<ModelObject, List<ModelObject>> getUserDiscountByGoods(ModelObject user,
                                                                      List<ModelObject> goodsAndSku,
                                                                      PlatformType platformType,
                                                                      boolean reverse) {
        return getUserDiscountByGoods(user, goodsAndSku, platformType, null, reverse);
    }

    @Override
    public List<ModelObject> getUserDiscountListByGoods(ModelObject user, List<ModelObject> goodsAndSku, PlatformType platformType) {
        Map<ModelObject, List<ModelObject>> map = this.getUserDiscountByGoods(user, goodsAndSku, platformType, true);
        return getUserDiscountMap2List(map);
    }

    @Override
    public List<ModelObject> getUserDiscountByGoods(long uid, long gid, long skuId, PlatformType platformType) {
        ModelObject user = GlobalService.userService.getUser(uid);
        ModelObject goods = GlobalService.goodsService.getSimpleGoodsById(gid);
        ModelObject sku = GlobalService.goodsService.getSimpleSkuById(skuId);
        goods.put(TableGoodsSku.class.getSimpleName(), sku);

        Map<ModelObject, List<ModelObject>> map = this.getUserDiscountByGoods(user, Arrays.asList(goods), platformType, true);
        return getUserDiscountMap2List(map);
    }

    private List<ModelObject> getUserDiscountMap2List(Map<ModelObject, List<ModelObject>> map) {
        if (map != null) {
            Set<ModelObject> set = new LinkedHashSet<>();
            for (Map.Entry<ModelObject, List<ModelObject>> entry : map.entrySet()) {
                set.add(entry.getKey());
            }
            return new ArrayList<>(set);
        }
        return null;
    }

    @Override
    public ModelObject getDiscountById(long id) {
        Query query = Criteria.query(TableDiscount.class);
        query.eq(TableDiscount.id, id);
        query.subjoin(TableDiscountUserLevel.class).eq(TableDiscountUserLevel.did, TableDiscount.id);
        query.subjoin(TableDiscountPlatform.class).eq(TableDiscountPlatform.did, TableDiscount.id);
        ModelObject modelObject = sessionTemplate.get(query);
        List<ModelObject> impls = this.getImpls();
        if (modelObject != null) {
            String impl = modelObject.getString(TableDiscount.impl);
            for (ModelObject ipo : impls) {
                if (impl.equals(ipo.getString("impl"))) {
                    modelObject.put("implName", ipo.getString("name"));
                }
            }
        }

        int type = modelObject.getIntValue(TableDiscount.type);
        if (type == DiscountTypes.GOODS_CLASSIFY.getCode()) {
            List<ModelObject> cids = sessionTemplate.list(Criteria.query(TableDiscountGoods.class).eq(TableDiscountGoods.did, id));
            if (cids != null) {
                List<Integer> cidList = new ArrayList<>();
                for (ModelObject o : cids) {
                    String tid = o.getString(TableDiscountGoods.tid);
                    cidList.add(Integer.parseInt(tid.substring(1)));
                }
                List<ModelObject> groups = GlobalService.goodsGroupService.getGroups(cidList);
                if (groups != null) {
                    modelObject.put("groups", groups);
                }
            }
        }
        if (type == DiscountTypes.BRAND.getCode()) {
            List<ModelObject> bids = sessionTemplate.list(Criteria.query(TableDiscountGoods.class).eq(TableDiscountGoods.did, id));
            if (bids != null) {
                List<Integer> bidList = new ArrayList<>();
                for (ModelObject o : bids) {
                    String tid = o.getString(TableDiscountGoods.tid);
                    bidList.add(Integer.parseInt(tid.substring(1)));
                }
                List<ModelObject> brands = GlobalService.goodsBrandService.getBrands(bidList);
                if (brands != null) {
                    modelObject.put("brands", brands);
                }
            }
        }
        if (type == DiscountTypes.GOODS.getCode()) {
            List<ModelObject> gids = sessionTemplate.list(
                    Criteria.query(TableDiscountGoods.class)
                            .eq(TableDiscountGoods.did, id)
                            .limit(0, 10));
            if (gids != null) {
                List<Long> gidList = new ArrayList<>();
                for (ModelObject o : gids) {
                    String tid = o.getString(TableDiscountGoods.tid);
                    if (tid.indexOf("S") > 0) {
                        tid = tid.substring(0, tid.indexOf("S"));
                    }
                    gidList.add(Long.parseLong(tid.substring(1)));
                }
                List<ModelObject> goods = GlobalService.goodsService.getSimpleGoods(gidList);
                if (goods != null) {
                    modelObject.put("goods", goods);
                }
            }
        }

        List<ModelObject> leveldids = modelObject.getArray(TableDiscountUserLevel.class.getSimpleName());
        if (leveldids != null && leveldids.size() > 0) {
            List<Integer> levelIds = new ArrayList<>();
            for (ModelObject o : leveldids) {
                levelIds.add(o.getIntValue(TableDiscountUserLevel.ulid));
            }
            List<ModelObject> levels = GlobalService.userLevelService.getLevels(levelIds);
            modelObject.put("userLevels", levels);
        }

        List<ModelObject> platformObjs = modelObject.getArray(TableDiscountPlatform.class.getSimpleName());
        if (platformObjs != null && platformObjs.size() > 0) {
            List<ModelObject> platforms = new ArrayList<>();
            for (ModelObject p : platformObjs) {
                int platformId = p.getIntValue(TableDiscountPlatform.ptype);
                PlatformType platformType = PlatformType.get(platformId);
                ModelObject platform = new ModelObject();
                platform.put("id", platformType.getCode());
                platform.put("name", platformType.getName());
                platforms.add(platform);
            }
            modelObject.put("platforms", platforms);
        }

        return modelObject;
    }

    @Override
    public Paging getSimpleDiscountGoods(long did, long start, long limit) {
        Paging paging = sessionTemplate.paging(
                Criteria.query(TableDiscountGoods.class)
                        .eq(TableDiscountGoods.did, did)
                        .limit(start, limit)
        );
        if (paging != null && paging.getObjects() != null) {
            List<ModelObject> objects = paging.getObjects();
            List<Long> gidList = new ArrayList<>();
            for (ModelObject o : objects) {
                String tid = o.getString(TableDiscountGoods.tid);
                gidList.add(Long.parseLong(tid.substring(1)));
            }
            List<ModelObject> goods = GlobalService.goodsService.getSimpleGoods(gidList);
            if (goods != null) {
                paging.setObjects(goods);
            }
        }
        return paging;
    }

    @Override
    public List<OrderDiscountModel> calDiscount(OrderModel orderModel) throws ModuleException {
        if (orderModel != null) {

            /**
             * step1: 首先获得前端传入的用户已选的优惠活动id
             * 并查询出所选的优惠活动ID
             */
            long uid = orderModel.getUid();
            String orderNumber = orderModel.getOrderNumber();
            /**
             * 如果A，B，C商品选择同一个活动则，只计算这个活动下 A，B，C三个商品价格总和的优惠
             * 所以需要优惠活动不能重复
             */
            Set<Long> discountIds = new LinkedHashSet<>();
            List<OrderGoodsModel> goodsModels = orderModel.getProducts();
            for (OrderGoodsModel model : goodsModels) {
                discountIds.add(model.getDiscountId());
            }

            if (discountIds.size() > 0) {
                List<ModelObject> selectDiscounts = sessionTemplate.list(
                        Criteria.query(TableDiscount.class).in(TableDiscount.id, discountIds));


                if (selectDiscounts != null && selectDiscounts.size() > 0) {

                    /**
                     * step2:判断所选的优惠活动是否对当前用户有效
                     */
                    List<Long> removeDiscounts = new ArrayList<>();
                    for (ModelObject discount : selectDiscounts) {
                        if (!this.userCanUse(orderModel.getUser(), discount)) {
                            removeDiscounts.add(discount.getLongValue(TableDiscount.id));
                        }
                    }
                    discountIds.removeAll(removeDiscounts);


                    /**
                     * step3:将所有购买的商品和用户选择的优惠活动ID传入函数计算
                     * 得出结果：key=discount , value = List(Goods)
                     * 得出每个优惠活动对应的商品列表，后续计算时平均优惠活动会用到
                     */
                    List<ModelObject> goodsList = new ArrayList<>();
                    for (OrderGoodsModel model : goodsModels) {
                        goodsList.add(model.getGoods());
                    }

                    if (discountIds != null && discountIds.size() > 0) {
                        Map<ModelObject, List<ModelObject>> discounts = this.getUserDiscountByGoods(
                                orderModel.getUser(),
                                goodsList,
                                orderModel.getPlatformType(),
                                new ArrayList<>(discountIds),
                                true);

                        /**
                         * step4:将key=discount , value = List(Goods)结构
                         * 转换成 key=discount , value = OrderDiscountModel 的结构
                         */
                        Map<ModelObject, OrderDiscountModel> discountModelMap = new LinkedHashMap<>();
                        if (discounts != null && discounts.size() > 0) {
                            Set<Map.Entry<ModelObject, List<ModelObject>>> entries = discounts.entrySet();
                            for (Map.Entry<ModelObject, List<ModelObject>> entry : entries) {
                                ModelObject discount = entry.getKey();
                                long id = discount.getLongValue(TableDiscount.id);
                                OrderDiscountModel orderDiscountModel = new OrderDiscountModel();
                                List<ModelObject> values = entry.getValue();
                                if (values != null) {
                                    List<OrderGoodsModel> orderDiscountModelGoods = new ArrayList<>();
                                    for (ModelObject goods : values) {
                                        for (OrderGoodsModel model : goodsModels) {
                                            if (goods.getLongValue(TableGoods.id) == model.getGid()) {
                                                orderDiscountModelGoods.add(model);
                                            }
                                        }
                                    }

                                    /**
                                     * 这里需要做一件事情
                                     * 就是如果三个商品 A，B，C 都拥有桶一个活动D1
                                     * 但是A还有D2活动
                                     * 如果A选择的是D2活动，那么久排除A，B，C三个商品价格总计计算D1活动
                                     * 这个时候A单独计算D2，B，C总和计算D1
                                     */
                                    List<OrderGoodsModel> removed = new ArrayList<>();
                                    for (OrderGoodsModel model : orderDiscountModelGoods) {
                                        if (id != model.getDiscountId()) {
                                            removed.add(model);
                                        }
                                    }
                                    orderDiscountModelGoods.removeAll(removed);

                                    orderDiscountModel.setGoodsModels(orderDiscountModelGoods);
                                }
                                discountModelMap.put(discount, orderDiscountModel);
                            }
                        }


                        /**
                         * step5:开始计算每一个优惠活动优惠的价格
                         */
                        if (discountModelMap != null && discountModelMap.size() > 0) {
                            List<OrderDiscountModel> orderDiscountModels = new ArrayList<>();
                            Set<Map.Entry<ModelObject, OrderDiscountModel>> entries = discountModelMap.entrySet();
                            for (Map.Entry<ModelObject, OrderDiscountModel> entry : entries) {
                                try {
                                    ModelObject discount = entry.getKey();
                                    OrderDiscountModel discountGoodsModel = entry.getValue();
                                    orderDiscountModels.add(discountGoodsModel);

                                    String impl = discount.getString(TableDiscount.impl);
                                    Class instance = Class.forName(impl);
                                    DiscountCalculateInterface calculate = (DiscountCalculateInterface) instance.newInstance();
                                    OrderDiscountModelWrapper wrapper = new OrderDiscountModelWrapper(discount, discountGoodsModel);
                                    boolean isSucc = calculate.calculate(wrapper);
                                    if (isSucc && orderModel.isOrderCreate()) {
                                        ModelObject object = new ModelObject(TableDiscountUser.class);
                                        object.put(TableDiscountUser.userId, uid);
                                        object.put(TableDiscountUser.did, wrapper.getDid());
                                        object.put(TableDiscountUser.orderNumber, orderNumber);
                                        object.put(TableDiscountUser.createTime, new Date());
                                        sessionTemplate.save(object);
                                    }
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                }
                            }
                            return orderDiscountModels;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void calDiscountByGoods(OrderModel orderModel, PlatformType platformType) throws ModuleException {
        orderModel.setOrderCreate(false);
        orderModel.setPlatformType(platformType);
        this.calDiscount(orderModel);
    }

    @Override
    public List<ModelObject> getPlatforms() {
        PlatformType[] platformTypes = PlatformType.values();
        List<ModelObject> p = new ArrayList<>();
        for (PlatformType platformType : platformTypes) {
            ModelObject object = new ModelObject();
            object.put("id", platformType.getCode());
            object.put("name", platformType.getName());
            p.add(object);
        }
        return p;
    }

    @Override
    public void handBack(ModelObject order) {
        // 活动相关暂时不退还相关资源
    }

    @Override
    public void addDiscountGoods(long did, List<ModelObject> gids) throws ModuleException {
        ModelObject discount = sessionTemplate.get(TableDiscount.class, did);
        if (discount != null) {
            int cid = discount.getIntValue(TableDiscount.id);
            int type = discount.getIntValue(TableDiscount.type);
            if (type == DiscountTypes.GOODS.getCode()) {
                if (gids != null) {
                    for (ModelObject gidObj : gids) {
                        long gid = gidObj.getLongValue("gid");
                        long skuId = gidObj.getLongValue("skuId");
                        if (skuId < 0) skuId = 0;

                        ModelObject simpleGoods = GlobalService.goodsService.getSimpleGoodsById(gid);
                        if (simpleGoods == null) {
                            throw new ModuleException(StockCode.NOT_EXIST, "商品ID" + gid + "的商品不存在");
                        }

                        if (skuId != 0) {
                            ModelObject simpleSku = GlobalService.goodsService.getSkuById(skuId);
                            if (simpleSku == null) {
                                throw new ModuleException(StockCode.NOT_EXIST, "商品SKU" + skuId + "的商品不存在");
                            }
                        }
                    }
                    for (ModelObject gidObj : gids) {
                        long gid = gidObj.getLongValue("gid");
                        long skuId = gidObj.getLongValue("skuId");
                        if (skuId < 0) skuId = 0;

                        ModelObject object = new ModelObject(TableDiscountGoods.class);
                        String value = "G" + gid;
                        if (skuId > 0) {
                            value += "S" + skuId;
                            object.put(TableDiscountGoods.type, "GS");
                        } else {
                            object.put(TableDiscountGoods.type, "G");
                        }

                        sessionTemplate.delete(
                                Criteria.delete(TableDiscountGoods.class)
                                        .eq(TableDiscountGoods.tid, value)
                                        .eq(TableDiscountGoods.did, cid)
                        );
                        object.put(TableDiscountGoods.tid, value);
                        object.put(TableDiscountGoods.did, cid);
                        sessionTemplate.saveAndUpdate(object);
                    }
                }
            } else {
                throw new ModuleException(StockCode.STATUS_ERROR, "促销活动类型为商品时才能动态添加");
            }
        } else {
            throw new ModuleException(StockCode.NOT_EXIST, "促销活动不存在");
        }
    }

    @Override
    public void removeDiscountGoods(long did, long gid, long skuId) {
        List<String> ids = new ArrayList<>();
        String value = "G" + gid;
        ids.add(value);
        if (skuId > 0) value += "S" + skuId;
        ids.add(value);
        sessionTemplate.delete(
                Criteria.delete(TableDiscountGoods.class)
                        .in(TableDiscountGoods.tid, ids)
                        .eq(TableDiscountGoods.did, did)
        );

        ModelObject coupon = sessionTemplate.get(TableDiscount.class, did);
        ModelObject exclude = new ModelObject(TableDiscountGoods.class);
        exclude.put(TableDiscountGoods.did, did);
        exclude.put(TableDiscountGoods.tid, "E" + gid);
        exclude.put(TableDiscountGoods.type, "E");
        exclude.put(TableDiscountGoods.enable, coupon.getIntValue(TableDiscount.enable) == 1 ? 1 : 0);
        sessionTemplate.save(exclude);
    }

    private boolean userCanUse(ModelObject user, ModelObject discount) {
        long did = discount.getLongValue(TableDiscount.id);
        int count = discount.getIntValue(TableDiscount.count);
        long uid = user.getLongValue(TableUser.id);

        // 判断等级是否符合
        List<ModelObject> userLevels = sessionTemplate.list(Criteria.query(TableDiscountUserLevel.class).eq(TableDiscountUserLevel.did, did));
        if (userLevels != null) {
            long levelAmount = user.getLongValue(TableUser.levelAmount);
            ModelObject level = GlobalService.userLevelService.getLevelByAmount(levelAmount);
            if (level == null) {
                return false;
            }
            boolean hasLevel = false;
            for (ModelObject ul : userLevels) {
                if (ul.getIntValue(TableDiscountUserLevel.ulid) == level.getIntValue(TableUserLevel.id)
                        || ul.getIntValue(TableDiscountUserLevel.ulid) == 0) {
                    hasLevel = true;
                }
            }
            if (!hasLevel) {
                return false;
            }
        } else {
            return false;
        }

        // 判断使用次数是否符合
        long c = sessionTemplate.count(
                Criteria.query(TableDiscountUser.class)
                        .eq(TableDiscountUser.userId, uid)
                        .eq(TableDiscountUser.did, did));
        if (count >= 0 && c > count) {
            return false;
        }
        return true;
    }
}
