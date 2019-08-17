package com.jianzixing.webapp.service.coupon;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.discount.DiscountTypes;
import com.jianzixing.webapp.service.order.OrderDiscountModel;
import com.jianzixing.webapp.service.order.OrderDiscountModelWrapper;
import com.jianzixing.webapp.service.order.OrderGoodsModel;
import com.jianzixing.webapp.service.order.OrderModel;
import com.jianzixing.webapp.tables.coupon.TableCoupon;
import com.jianzixing.webapp.tables.coupon.TableCouponGoods;
import com.jianzixing.webapp.tables.coupon.TableCouponUser;
import com.jianzixing.webapp.tables.coupon.TableCouponUserLevel;
import com.jianzixing.webapp.tables.goods.TableGoods;
import com.jianzixing.webapp.tables.goods.TableGoodsSku;
import com.jianzixing.webapp.tables.order.TableOrder;
import com.jianzixing.webapp.tables.user.TableUser;
import com.jianzixing.webapp.tables.user.TableUserLevel;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Join;
import org.mimosaframework.orm.criteria.Keyword;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.orm.transaction.Transaction;
import org.mimosaframework.orm.transaction.TransactionCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DefaultCouponService implements CouponService {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void saveCoupon(ModelObject object) throws ModelCheckerException, ModuleException {
        object.setObjectClass(TableCoupon.class);
        object.checkAndThrowable();

        int id = object.getIntValue(TableCoupon.id);
        boolean isUpdate = id > 0 ? true : false;

        double orderPrice = object.getDoubleValue(TableCoupon.orderPrice);
        double couponPrice = object.getDoubleValue(TableCoupon.couponPrice);
        if (orderPrice <= couponPrice) {
            throw new ModuleException(StockCode.FAILURE, "优惠价格不能大于等于使用门槛金额");
        }

        String startTime = object.getString(TableCoupon.startTime);
        String finishTime = object.getString(TableCoupon.finishTime);
        if (startTime == null && finishTime == null) {
            throw new ModuleException(StockCode.ARG_NULL, "必须设置开始时间和结束时间");
        }
        try {
            format.parse(startTime);
            format.parse(finishTime);
        } catch (ParseException e) {
            throw new ModuleException(StockCode.FORMAT, "开始时间或者结束时间日期格式错误");
        }

        Object userLevelObj = object.get("userLevels");
        List userLevels = null;
        if (userLevelObj instanceof Integer) {
            userLevels = new ArrayList();
            userLevels.add(userLevelObj);
        } else {
            userLevels = object.getArray("userLevels");
        }
        List<ModelObject> userLevelObjs = new ArrayList<>();
        if (userLevels != null) {
            boolean isAllLevel = false;
            for (int i = 0; i < userLevels.size(); i++) {
                if (String.valueOf(userLevels.get(i)).equals("0")) {
                    isAllLevel = true;
                    break;
                }
                ModelObject userLevel = new ModelObject(TableCouponUserLevel.class);
                userLevel.put(TableCouponUserLevel.ulid, userLevels.get(i));
                userLevelObjs.add(userLevel);
            }
            if (isAllLevel) {
                userLevelObjs.clear();
                ModelObject userLevel = new ModelObject(TableCouponUserLevel.class);
                userLevel.put(TableCouponUserLevel.ulid, 0);
                userLevelObjs.add(userLevel);
            }
        } else {
            // 如果不设置就全等级
            ModelObject userLevel = new ModelObject(TableCouponUserLevel.class);
            userLevel.put(TableCouponUserLevel.ulid, 0);
            userLevelObjs.add(userLevel);
        }

        // 默认商品类型是商品
        object.put(TableCoupon.type, 1);
        // 默认不可叠加
        object.put(TableCoupon.overlay, 0);
        if (isUpdate) {
            sessionTemplate.delete(Criteria.delete(TableCouponUserLevel.class)
                    .eq(TableCouponUserLevel.cid, id));
            object.remove(TableCoupon.createTime);
            sessionTemplate.update(object);
        } else {
            object.put(TableCoupon.isDel, 0);
            object.put(TableCoupon.createTime, new Date());
            sessionTemplate.save(object);
        }

        if (userLevelObjs.size() > 0) {
            for (ModelObject level : userLevelObjs) {
                level.put(TableCouponUserLevel.cid, object.getIntValue(TableCoupon.id));
            }
            sessionTemplate.save(userLevelObjs);
        }
    }

    @Override
    public void delCoupon(long id) {
        ModelObject object = new ModelObject(TableCoupon.class);
        object.put(TableCoupon.id, id);
        object.put(TableCoupon.isDel, 1);
        sessionTemplate.update(object);
    }

    @Override
    public void updateCoupon(ModelObject object) throws ModuleException, ModelCheckerException {
        int id = object.getIntValue(TableCoupon.id);
        ModelObject coupon = sessionTemplate.get(TableCoupon.class, id);
        if (coupon != null) {
            if (isValidCoupon(coupon)) {
                object.setObjectClass(TableCoupon.class);
                object.retain(TableCoupon.id, TableCoupon.name, TableCoupon.detail);
                object.checkUpdateThrowable();
                sessionTemplate.update(object);
            } else {
                this.saveCoupon(object);
            }
        } else {
            throw new ModuleException(StockCode.NOT_EXIST, "优惠券不存在");
        }
    }

    private boolean isValidCoupon(ModelObject coupon) {
        int id = coupon.getIntValue(TableCoupon.id);
        int status = coupon.getIntValue(TableCoupon.status);
        int isDel = coupon.getIntValue(TableCoupon.isDel);
        if (isDel != 1) {
            Date startTime = coupon.getDate(TableCoupon.startTime);
            Date finishTime = coupon.getDate(TableCoupon.finishTime);
            if (status == CouponStatus.GETTING.getCode()) {
                if (startTime.getTime() <= System.currentTimeMillis()
                        && finishTime.getTime() >= System.currentTimeMillis()) {
                    return true;
                }
            }
            if (status == CouponStatus.BEFORE.getCode()) {
                // 如果时间已经开始，设置状态
                if (startTime.getTime() <= System.currentTimeMillis()
                        && finishTime.getTime() >= System.currentTimeMillis()) {
                    ModelObject object = new ModelObject(TableCoupon.class);
                    object.put(TableCoupon.id, id);
                    object.put(TableCoupon.status, CouponStatus.GETTING.getCode());
                    sessionTemplate.update(object);
                    return true;
                }
                // 如果时间已经结束，设置状态
                if (finishTime.getTime() < System.currentTimeMillis()) {
                    ModelObject object = new ModelObject(TableCoupon.class);
                    object.put(TableCoupon.id, id);
                    object.put(TableCoupon.status, CouponStatus.FINISH.getCode());
                    sessionTemplate.update(object);
                }
            }
        }
        return false;
    }

    @Override
    public Paging getCoupons(ModelObject search, int start, int limit) {
        Query query = Criteria.query(TableCoupon.class);
        Join join = query.subjoin(TableCouponUserLevel.class).eq(TableCouponUserLevel.cid, TableCoupon.id);
        join.childJoin(TableUserLevel.class).eq(TableUserLevel.id, TableCouponUserLevel.ulid).single();
        query.eq(TableCoupon.isDel, 0);
        query.order(TableCoupon.id, false);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public void enableCoupon(long id) {
        ModelObject object = new ModelObject(TableCoupon.class);
        object.put(TableCoupon.id, id);
        object.put(TableCoupon.status, CouponStatus.BEFORE.getCode());
        sessionTemplate.update(object);
        ModelObject coupon = sessionTemplate.get(TableCoupon.class, id);
        this.isValidCoupon(coupon);
    }

    @Override
    public void finishCoupon(long id) {
        ModelObject object = new ModelObject(TableCoupon.class);
        object.put(TableCoupon.id, id);
        object.put(TableCoupon.status, CouponStatus.FINISH.getCode());
        sessionTemplate.update(object);
    }

    @Override
    public void addCouponGoods(long id, List<ModelObject> gids) throws ModuleException {
        ModelObject coupon = sessionTemplate.get(TableCoupon.class, id);
        if (coupon != null) {
            int cid = coupon.getIntValue(TableCoupon.id);
            int type = coupon.getIntValue(TableCoupon.type);
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

                        ModelObject object = new ModelObject(TableCouponGoods.class);
                        String value = "G" + gid;
                        if (skuId > 0) {
                            value += "S" + skuId;
                            object.put(TableCouponGoods.type, "GS");
                        } else {
                            object.put(TableCouponGoods.type, "G");
                        }

                        sessionTemplate.delete(
                                Criteria.delete(TableCouponGoods.class)
                                        .eq(TableCouponGoods.tid, value)
                                        .eq(TableCouponGoods.cid, cid)
                        );
                        object.put(TableCouponGoods.tid, value);
                        object.put(TableCouponGoods.cid, cid);
                        sessionTemplate.saveAndUpdate(object);
                    }
                }
            } else {
                throw new ModuleException(StockCode.STATUS_ERROR, "优惠券类型为商品时才能动态添加");
            }
        } else {
            throw new ModuleException(StockCode.NOT_EXIST, "优惠券不存在");
        }
    }

    @Override
    public void removeCouponGoods(long id, long gid, long skuId) {
        List<String> ids = new ArrayList<>();
        String value = "G" + gid;
        ids.add(value);
        if (skuId > 0) value += "S" + skuId;
        ids.add(value);
        sessionTemplate.delete(
                Criteria.delete(TableCouponGoods.class)
                        .in(TableCouponGoods.tid, ids)
                        .eq(TableCouponGoods.cid, id)
        );

        ModelObject coupon = sessionTemplate.get(TableCoupon.class, id);
        ModelObject exclude = new ModelObject(TableCouponGoods.class);
        exclude.put(TableCouponGoods.cid, id);
        exclude.put(TableCouponGoods.tid, "E" + gid);
        exclude.put(TableCouponGoods.type, "E");
        exclude.put(TableCouponGoods.enable, coupon.getIntValue(TableCoupon.status) == CouponStatus.GETTING.getCode() ? 1 : 0);
        sessionTemplate.save(exclude);
    }

    @Override
    public Paging getCouponGoods(long cid, int start, int limit) {
        ModelObject coupon = sessionTemplate.get(Criteria.query(TableCoupon.class)
                .eq(TableCoupon.id, cid));

        if (coupon != null) {
            Paging paging = null;
            int type = coupon.getIntValue(TableCoupon.type);
            if (type == 0) { // 商品分类
                List<ModelObject> ids = sessionTemplate.list(
                        Criteria.query(TableCouponGoods.class)
                                .eq(TableCouponGoods.cid, cid)
                                .eq(TableCouponGoods.type, "C"));
                if (ids != null) {
                    List<Integer> goodsGroupIdList = new ArrayList<>();
                    for (ModelObject idObj : ids) {
                        String idStr = idObj.getString(TableCouponGoods.tid);
                        if (idStr.startsWith("C")) {
                            goodsGroupIdList.add(Integer.parseInt(idStr.substring(1)));
                        }
                    }

                    paging = GlobalService.goodsService.getGoodsInGroups(goodsGroupIdList, start, limit);
                }
            }
            if (type == 1) { // 商品
                Paging idsPaging = sessionTemplate.paging(
                        Criteria.query(TableCouponGoods.class)
                                .eq(TableCouponGoods.cid, cid)
                                .in(TableCouponGoods.type, "G", "GS")
                                .limit(start, limit));
                if (idsPaging != null && idsPaging.getObjects() != null) {
                    List<ModelObject> ids = idsPaging.getObjects();
                    Map<Long, Long> goodsIdList = new LinkedHashMap<>();
                    for (ModelObject idObj : ids) {
                        String idStr = idObj.getString(TableCouponGoods.tid);
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

                    List<ModelObject> goods = GlobalService.goodsService.getGoodsWithSku(goodsIdList, "couponSku");
                    paging = idsPaging;
                    idsPaging.setObjects(goods);
                }
            }
            if (type == 2) { // 品牌
                List<ModelObject> ids = sessionTemplate.list(
                        Criteria.query(TableCouponGoods.class)
                                .eq(TableCouponGoods.cid, cid)
                                .eq(TableCouponGoods.type, "B"));
                if (ids != null) {
                    List<Integer> goodsGroupIdList = new ArrayList<>();
                    for (ModelObject idObj : ids) {
                        String idStr = idObj.getString(TableCouponGoods.tid);
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

                List<ModelObject> relGoods = sessionTemplate.list(Criteria.query(TableCouponGoods.class)
                        .eq(TableCouponGoods.cid, cid)
                        .eq(TableCouponGoods.type, "E")
                        .in(TableCouponGoods.tid, excludes));

                if (relGoods != null) {
                    for (ModelObject relG : relGoods) {
                        for (ModelObject object : objects) {
                            if (("E" + object.getString(TableGoods.id)).equals(relG.getString(TableCouponGoods.tid))) {
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

    @Override
    public ModelObject userGetCoupon(long uid, long cid, CouponChannelType channelType) throws ModuleException, TransactionException {
        ModelObject coupon = sessionTemplate.get(
                Criteria.query(TableCoupon.class)
                        .eq(TableCoupon.isDel, 0)
                        .eq(TableCoupon.id, cid)
                        .eq(TableCoupon.status, CouponStatus.GETTING.getCode())
        );
        ModelObject user = GlobalService.userService.getUser(uid);
        this.userGetCoupon(user, coupon, channelType);
        return coupon;
    }

    @Override
    public void userGetCoupon(ModelObject user, ModelObject coupon, CouponChannelType channelType) throws ModuleException, TransactionException {
        long uid = user.getLongValue(TableUser.id);
        long cid = coupon.getLongValue(TableCoupon.id);
        int amount = coupon.getIntValue(TableCoupon.amount);
        int prepareAmount = coupon.getIntValue(TableCoupon.prepareAmount);
        int count = coupon.getIntValue(TableCoupon.count);
        int channel = coupon.getIntValue(TableCoupon.channel);
        Date startTime = coupon.getDate(TableCoupon.startTime);
        Date finishTime = coupon.getDate(TableCoupon.finishTime);


        if (channelType != CouponChannelType.IGNORE && channel != channelType.getCode()) {
            throw new ModuleException("channel_error", "当前渠道不允许获取该优惠券");
        }

        if (startTime.getTime() <= System.currentTimeMillis()
                && finishTime.getTime() >= System.currentTimeMillis()) {

            if (amount <= prepareAmount) {
                ModelObject update = new ModelObject(TableCoupon.class);
                update.put(TableCoupon.id, cid);
                update.put(TableCoupon.status, CouponStatus.FINISH.getCode());
                sessionTemplate.update(update);
                throw new ModuleException("coupon_empty", "该优惠券已领完");
            }

            long total = sessionTemplate.count(Criteria.query(TableCouponUser.class).eq(TableCouponUser.cid, cid)
                    .eq(TableCouponUser.uid, uid));
            if (total >= count) {
                throw new ModuleException("out_count", "每个用户限领" + count + "张");
            }

            List<ModelObject> userLevels = sessionTemplate.list(Criteria.query(TableCouponUserLevel.class).eq(TableCouponUserLevel.cid, cid));
            ModelObject userLevel = GlobalService.userLevelService.getUserLevel(user);
            if (userLevels != null && userLevels.size() > 0) {
                if (userLevel == null) {
                    throw new ModuleException("user_level_low", "用户等级无效");
                }
                boolean isContains = false;
                int ulid = userLevel.getIntValue(TableUserLevel.id);
                for (ModelObject ul : userLevels) {
                    int culid = ul.getIntValue(TableCouponUserLevel.ulid);
                    if (ulid == culid || culid == 0) {
                        isContains = true;
                        break;
                    }
                }
                if (isContains == false) {
                    throw new ModuleException("user_level_low", "用户等级无法领取优惠券");
                }
            }

            sessionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean invoke(Transaction transaction) throws Exception {
                    long r = sessionTemplate.update(
                            Criteria.update(TableCoupon.class)
                                    .addSelf(TableCoupon.prepareAmount)
                                    .lt(TableCoupon.prepareAmount, amount)
                    );

                    if (r > 0) {
                        ModelObject userCoupon = new ModelObject(TableCouponUser.class);
                        userCoupon.put(TableCouponUser.uid, uid);
                        userCoupon.put(TableCouponUser.cid, cid);
                        userCoupon.put(TableCouponUser.status, CouponUserStatus.NORMAL.getCode());
                        userCoupon.put(TableCouponUser.createTime, new Date());
                        sessionTemplate.saveAndUpdate(userCoupon);
                    } else {
                        throw new ModuleException(StockCode.STATUS_ERROR, "该优惠券领取失败");
                    }
                    return true;
                }
            });
        } else {
            throw new ModuleException("coupon_expire", "该优惠券已过期");
        }
    }

    @Override
    public Paging getUserCoupons(ModelObject search, long cid, int start, int limit) {
        Paging paging = sessionTemplate.paging(
                Criteria.query(TableCouponUser.class)
                        .subjoin(TableUser.class).eq(TableUser.id, TableCouponUser.uid).single().query()
                        .subjoin(TableCoupon.class).eq(TableCoupon.id, TableCouponUser.cid).single().query()
                        .eq(TableCoupon.id, cid)
                        .limit(start, limit)
                        .order(TableCouponUser.id, false)
        );
        return paging;
    }

    @Override
    public List<ModelObject> getUserValidCoupons(long uid) {
        return sessionTemplate.list(Criteria.query(TableCouponUser.class)
                .eq(TableCouponUser.uid, uid)
                .eq(TableCouponUser.status, CouponUserStatus.NORMAL.getCode()));
    }

    @Override
    public void declareUserCoupon(int id) {
        ModelObject old = sessionTemplate.get(TableCouponUser.class, id);
        if (old != null) {
            int status = old.getIntValue(TableCouponUser.status);
            if (status != CouponUserStatus.USED.getCode()
                    && status != CouponUserStatus.EXPIRED.getCode()
                    && status != CouponUserStatus.DECLARE.getCode()) {
                ModelObject object = new ModelObject(TableCouponUser.class);
                object.put(TableCouponUser.id, id);
                object.put(TableCouponUser.status, CouponUserStatus.DECLARE.getCode());
                sessionTemplate.update(object);
            }
        }
    }

    @Override
    public List<ModelObject> getCouponsByUser(long uid, int page, CouponUserStatus... status) {
        int limit = 50;
        int start = (page - 1) * limit;
        List<Integer> statusList = new ArrayList<>();
        for (CouponUserStatus s : status) {
            statusList.add(s.getCode());
        }
        return sessionTemplate.list(
                Criteria.query(TableCouponUser.class)
                        .subjoin(TableCoupon.class).eq(TableCoupon.id, TableCouponUser.cid).single().query()
                        .eq(TableCouponUser.uid, uid)
                        .in(TableCouponUser.status, statusList)
                        .limit(start, limit)
        );
    }

    @Override
    public long getCouponsCountByUser(long uid, CouponUserStatus... status) {
        List<Integer> statusList = new ArrayList<>();
        for (CouponUserStatus s : status) {
            statusList.add(s.getCode());
        }
        return sessionTemplate.count(
                Criteria.query(TableCouponUser.class)
                        .eq(TableCouponUser.uid, uid)
                        .in(TableCouponUser.status, statusList)
        );
    }

    private List<ModelObject> getCouponsByCG(ModelObject user, List<ModelObject> couponGoods) {
        Set<Long> couponIds = new LinkedHashSet<>();
        for (ModelObject cg : couponGoods) {
            couponIds.add(cg.getLongValue(TableCouponGoods.cid));
        }

        List<ModelObject> getedCoupons = null;
        if (user != null) {
            long uid = user.getLongValue(TableUser.id);
            int levelId = user.getIntValue(TableUser.levelId);
            List<ModelObject> levels = sessionTemplate.list(Criteria.query(TableCouponUserLevel.class)
                    .in(TableCouponUserLevel.cid, couponIds)
                    .in(TableCouponUserLevel.ulid, levelId, 0));
            if (levels != null) {
                couponIds.clear();
                for (ModelObject level : levels) {
                    couponIds.add(level.getLongValue(TableCouponUserLevel.cid));
                }
            } else {
                return null;
            }

            getedCoupons = sessionTemplate.list(Criteria.query(TableCouponUser.class)
                    .eq(TableCouponUser.uid, uid)
                    .in(TableCouponUser.cid, couponIds)
                    .eq(TableCouponUser.status, CouponUserStatus.NORMAL.getCode()));
        }
        List<ModelObject> coupons = sessionTemplate.list(Criteria.query(TableCoupon.class)
                .in(TableCoupon.id, couponIds)
                .eq(TableCoupon.status, CouponStatus.GETTING.getCode())
                .eq(TableCoupon.isDel, 0));

        if (getedCoupons != null) {
            for (ModelObject c : coupons) {
                c.put("take", 0);
            }
            for (ModelObject gc : getedCoupons) {
                for (ModelObject c : coupons) {
                    if (gc.getLongValue(TableCouponUser.cid) == c.getLongValue(TableCoupon.id)) {
                        c.put("take", 1);
                    }
                }
            }
        }

        this.checkCouponsValid(coupons);
        return coupons;
    }

    private void checkCouponsValid(List<ModelObject> coupons) {
        if (coupons != null) {
            List<ModelObject> rm = new ArrayList<>();
            for (ModelObject coupon : coupons) {
                long id = coupon.getLongValue(TableCoupon.id);
                Date startTime = coupon.getDate(TableCoupon.startTime);
                Date finishTime = coupon.getDate(TableCoupon.finishTime);
                if (startTime.getTime() > System.currentTimeMillis()) {
                    ModelObject update = new ModelObject(TableCoupon.class);
                    update.put(TableCoupon.id, id);
                    update.put(TableCoupon.status, CouponStatus.BEFORE.getCode());
                    sessionTemplate.update(update);
                    rm.add(coupon);
                }
                if (finishTime.getTime() < System.currentTimeMillis()) {
                    ModelObject update = new ModelObject(TableCoupon.class);
                    update.put(TableCoupon.id, id);
                    update.put(TableCoupon.status, CouponStatus.FINISH.getCode());
                    sessionTemplate.update(update);
                    rm.add(coupon);
                }
            }
            coupons.removeAll(rm);
        }
    }

    @Override
    public List<ModelObject> getCouponsByGid(long uid, long gid, long skuId) {
        List<String> ids = new ArrayList<>();
        ids.add("G" + gid);
        ids.add("G" + gid + "S" + skuId);
        Query queryCouponGoods = Criteria.query(TableCouponGoods.class)
                .in(TableCouponGoods.tid, ids);
        List<ModelObject> couponGoods = sessionTemplate.list(queryCouponGoods);
        if (couponGoods == null) {
            return null;
        }
        ModelObject user = null;
        if (uid > 0) {
            user = GlobalService.userService.getUser(uid);
        }
        return getCouponsByCG(user, couponGoods);
    }

    @Override
    public List<ModelObject> getUserCouponsByGoods(ModelObject user, List<ModelObject> goodsList) {
        if (user != null && goodsList != null) {
            Query queryCouponGoods = Criteria.query(TableCouponGoods.class);
            Set<String> goodsIds = GlobalService.discountService.getGoodsDiscountKeySet(goodsList);

            queryCouponGoods.in(TableCouponGoods.tid, goodsIds);
            List<ModelObject> couponGoods = sessionTemplate.list(queryCouponGoods);
            if (couponGoods == null) {
                return null;
            }

            List<ModelObject> retainCoupons = new ArrayList<>();
            for (ModelObject gl : goodsList) {
                long gid = gl.getLongValue(TableGoods.id);
                ModelObject sku = gl.getModelObject(TableGoodsSku.class.getSimpleName());
                long skuId = 0;
                if (sku != null) {
                    skuId = sku.getLongValue(TableGoodsSku.id);
                }
                for (ModelObject coupon : couponGoods) {
                    String tid = coupon.getString(TableCouponGoods.tid);
                    if (("G" + gid).equals(tid) || ("G" + gid + "S" + skuId).equals(tid)) {
                        retainCoupons.add(coupon);
                    }
                }
            }
            return getCouponsByCG(user, couponGoods);
        }
        return null;
    }

    @Override
    public List<ModelObject> getUserCouponsInGoods(long uid, List<ModelObject> goodsList) {
        List<ModelObject> userCoupons = this.getUserValidCoupons(uid);
        List<Long> cids = new ArrayList<>();
        if (userCoupons != null) {
            for (ModelObject coupon : userCoupons) {
                cids.add(coupon.getLongValue(TableCouponUser.cid));
            }
            Set<String> goodsIds = GlobalService.discountService.getGoodsDiscountKeySet(goodsList);
            Query query = Criteria.query(TableCouponGoods.class)
                    .in(TableCouponGoods.cid, cids)
                    .in(TableCouponGoods.tid, goodsIds)
                    .in(TableCouponGoods.enable, 1);
            List<ModelObject> couponGoods = sessionTemplate.list(query);

            if (couponGoods != null) {
                Set<Long> cid = new LinkedHashSet<>();
                for (ModelObject userCoupon : userCoupons) {
                    cid.add(userCoupon.getLongValue(TableCouponUser.cid));
                }

                List<ModelObject> coupons = sessionTemplate.list(Criteria.query(TableCoupon.class).in(TableCoupon.id, cid));
                if (coupons != null) {
                    for (ModelObject coupon : coupons) {
                        coupon.put("enable", 0);
                    }
                }
                if (couponGoods != null && coupons != null) {
                    for (ModelObject cg : couponGoods) {
                        for (ModelObject coupon : coupons) {
                            if (cg.getLongValue(TableCouponGoods.cid) == coupon.getLongValue(TableCoupon.id)) {
                                coupon.put("enable", 1);
                            }
                        }
                    }
                }

                for (ModelObject userCoupon : userCoupons) {
                    for (ModelObject coupon : coupons) {
                        if (userCoupon.getLongValue(TableCouponUser.cid) == coupon.getLongValue(TableCoupon.id)) {
                            userCoupon.put(TableCoupon.class.getSimpleName(), coupon);
                        }
                    }
                }

                return userCoupons;
            }
        }
        return null;
    }

    @Override
    public List<OrderDiscountModel> calCoupon(OrderModel orderModel) {

        /**
         * step1:先获取用户选择使用的优惠券
         */
        long uid = orderModel.getUid();
        Set<Long> couponIds = new LinkedHashSet<>();
        couponIds.add(orderModel.getCouponId());
        List<ModelObject> couponUsers = sessionTemplate.list(Criteria.query(TableCouponUser.class)
                .subjoin(TableCoupon.class).eq(TableCoupon.id, TableCouponUser.cid).single().query()
                .in(TableCouponUser.id, couponIds)
                .eq(TableCouponUser.uid, uid)
                .eq(TableCouponUser.status, CouponUserStatus.NORMAL.getCode()));


        if (couponUsers != null && couponUsers.size() > 0) {

            /**
             * step2:根据用户优惠券查询优惠券活动信息,然后判断优惠券活动是否有效
             */
            List<Long> cids = new ArrayList<>();
            for (ModelObject coupon : couponUsers) {
                cids.add(coupon.getLongValue(TableCouponUser.cid));
            }

            boolean isUserCanUse = true;
            for (ModelObject couponUser : couponUsers) {
                ModelObject coupon = couponUser.getModelObject(TableCoupon.class);
                if (!this.userCanUse(orderModel.getUser(), coupon)) {
                    isUserCanUse = false;
                    break;
                }
            }

            if (isUserCanUse) {

                /**
                 * step3:
                 * 先获得所有商品列表，然后根据商品的各个主键信息查询出TableCouponGoods表
                 * 中是否存在当前商品的优惠活动
                 * 然后得到一个优惠活动和商品的对应表
                 */
                List<OrderGoodsModel> goodsModels = orderModel.getProducts();
                List<ModelObject> goodsList = new ArrayList<>();
                for (OrderGoodsModel model : goodsModels) {
                    goodsList.add(model.getGoods());
                }

                List<String> idsList = new ArrayList<>();
                Map<Long, Set<String>> idsMap = new HashMap<>();
                for (ModelObject goods : goodsList) {
                    long gid = goods.getLongValue(TableGoods.id);
                    Set<String> ids = GlobalService.discountService.getGoodsDiscountKeySet(goods);
                    idsList.addAll(ids);
                    idsMap.put(gid, ids);
                }
                Query query = Criteria.query(TableCouponGoods.class)
                        .in(TableCouponGoods.cid, cids)
                        .in(TableCouponGoods.tid, idsList)
                        .eq(TableCouponGoods.enable, 1);
                List<ModelObject> couponGoods = sessionTemplate.list(query);

                if (couponGoods != null) {
                    Set<Long> tidSet = new LinkedHashSet<>();
                    Map<Long, Set<Long>> tidMaps = new LinkedHashMap<>();
                    for (Map.Entry<Long, Set<String>> entry : idsMap.entrySet()) {
                        Set<String> ids = entry.getValue();
                        Set<Long> tidMapSet = new LinkedHashSet<>();
                        for (ModelObject cg : couponGoods) {
                            long ncid = cg.getLongValue(TableCouponGoods.cid);
                            String tid = cg.getString(TableCouponGoods.tid);

                            if (ids.contains(tid)) {
                                tidMapSet.add(ncid);
                            }
                            if (tid.startsWith("E") && ids.contains(tid)) {
                                tidMapSet.remove(ncid);
                            }
                        }
                        tidMaps.put(entry.getKey(), tidMapSet);
                        tidSet.addAll(tidMapSet);
                    }

                    List<ModelObject> sourceCoupons = sessionTemplate.list(Criteria.query(TableCoupon.class)
                            .in(TableCoupon.id, tidSet)
                            .eq(TableCoupon.isDel, 0)
                            .eq(TableCoupon.status, CouponStatus.GETTING.getCode()));
                    this.checkCouponsValid(sourceCoupons);

                    // 保存有效TableCouponUser表
                    Set<ModelObject> validCouponUser = new LinkedHashSet<>();
                    if (sourceCoupons != null && sourceCoupons.size() > 0) {
                        for (ModelObject sourceCoupon : sourceCoupons) {
                            long cid = sourceCoupon.getLongValue(TableCoupon.id);
                            for (ModelObject couponUser : couponUsers) {
                                if (cid == couponUser.getLongValue(TableCouponUser.cid)) {
                                    validCouponUser.add(couponUser);
                                }
                            }
                        }
                    }

                    // 将每个活动对应的商品列表缓存起来
                    Map<ModelObject, List<ModelObject>> couponMap = new LinkedHashMap<>();
                    for (Map.Entry<Long, Set<Long>> entry : tidMaps.entrySet()) {
                        ModelObject goods = null;
                        for (ModelObject g : goodsList) {
                            if (g.getLongValue(TableGoods.id) == entry.getKey()) {
                                goods = g;
                            }
                        }
                        if (goods != null) {
                            Set<Long> values = entry.getValue();
                            for (long value : values) {
                                for (ModelObject d : validCouponUser) {
                                    if (d.getLongValue(TableCouponUser.cid) == value) {
                                        List<ModelObject> mapGoods = couponMap.get(d);
                                        if (mapGoods == null) mapGoods = new ArrayList<>();
                                        mapGoods.add(goods);
                                        couponMap.put(d, mapGoods);
                                    }
                                }
                            }
                        }
                    }

                    /**
                     * step4:将优惠券和商品的对应表 key=coupon , value = List(Goods)
                     * 转换为 key=coupon , value = OrderDiscountModel
                     */
                    Map<ModelObject, OrderDiscountModel> couponModelMap = new LinkedHashMap<>();
                    if (couponMap != null && couponMap.size() > 0) {
                        Set<Map.Entry<ModelObject, List<ModelObject>>> entries = couponMap.entrySet();
                        for (Map.Entry<ModelObject, List<ModelObject>> entry : entries) {
                            ModelObject couponUser = entry.getKey();
                            OrderDiscountModel orderDiscountModel = new OrderDiscountModel();
                            List<ModelObject> values = entry.getValue();
                            if (values != null) {
                                List<OrderGoodsModel> orderCouponModelGoods = new ArrayList<>();
                                for (ModelObject goods : values) {
                                    for (OrderGoodsModel model : goodsModels) {
                                        if (goods.getLongValue(TableGoods.id) == model.getGid()) {
                                            orderCouponModelGoods.add(model);
                                        }
                                    }
                                }
                                orderDiscountModel.setGoodsModels(orderCouponModelGoods);
                            }
                            couponModelMap.put(couponUser, orderDiscountModel);
                        }
                    }


                    /**
                     * step5:然后计算商品优惠券的优惠金额然后分摊到各个商品中去
                     */
                    if (couponModelMap != null && couponModelMap.size() > 0) {
                        List<OrderDiscountModel> orderDiscountModels = new ArrayList<>();
                        Set<Map.Entry<ModelObject, OrderDiscountModel>> entries = couponModelMap.entrySet();
                        for (Map.Entry<ModelObject, OrderDiscountModel> entry : entries) {
                            ModelObject couponUser = entry.getKey();
                            ModelObject coupon = couponUser.getModelObject(TableCoupon.class);
                            OrderDiscountModel couponGoodsModel = entry.getValue();
                            orderDiscountModels.add(couponGoodsModel);
                            OrderDiscountModelWrapper discountGoodsModelWrapper = new OrderDiscountModelWrapper(coupon, couponGoodsModel);
                            BigDecimal totalPrice = discountGoodsModelWrapper.getTotalPrice();
                            BigDecimal takePrice = coupon.getBigDecimal(TableCoupon.orderPrice);
                            BigDecimal couponPrice = coupon.getBigDecimal(TableCoupon.couponPrice);
                            if (totalPrice.max(takePrice) == totalPrice) {
                                discountGoodsModelWrapper.setGoodsDiscountPrice(couponPrice);

                                if (orderModel.isOrderCreate()) {
                                    ModelObject update = new ModelObject(TableCouponUser.class);
                                    update.put(TableCouponUser.id, couponUser.getLongValue(TableCouponUser.id));
                                    update.put(TableCouponUser.status, CouponUserStatus.USED.getCode());
                                    update.put(TableCouponUser.useTime, new Date());
                                    update.put(TableCouponUser.orderNumber, orderModel.getOrderNumber());
                                    sessionTemplate.update(update);
                                }
                            }
                        }
                        return orderDiscountModels;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void handBack(ModelObject order) {
        String orderNumber = order.getString(TableOrder.number);
        long uid = order.getLongValue(TableOrder.userId);
        List<ModelObject> objects = sessionTemplate.list(Criteria.query(TableCouponUser.class)
                .eq(TableCouponUser.uid, uid)
                .eq(TableCouponUser.status, CouponUserStatus.USED.getCode())
                .eq(TableCouponUser.orderNumber, orderNumber));

        if (objects != null) {
            for (ModelObject object : objects) {
                ModelObject update = new ModelObject(TableCouponUser.class);
                update.put(TableCouponUser.id, object.getLongValue(TableCouponUser.id));
                update.put(TableCouponUser.status, CouponUserStatus.NORMAL.getCode());
                update.put(TableCouponUser.useTime, Keyword.NULL);
                update.put(TableCouponUser.orderNumber, Keyword.NULL);
                sessionTemplate.update(update);
            }
        }
    }

    @Override
    public long getUserValidCouponCount(long uid) {
        return sessionTemplate.count(Criteria.query(TableCouponUser.class)
                .eq(TableCouponUser.uid, uid)
                .eq(TableCouponUser.status, CouponUserStatus.NORMAL.getCode()));
    }


    private boolean userCanUse(ModelObject user, ModelObject coupon) {
        long did = coupon.getLongValue(TableCoupon.id);
        int count = coupon.getIntValue(TableCoupon.count);
        long uid = user.getLongValue(TableUser.id);

        if (!this.isValidCoupon(coupon)) {
            return false;
        }

        // 判断等级是否符合
        List<ModelObject> userLevels = sessionTemplate.list(Criteria.query(TableCouponUserLevel.class).eq(TableCouponUserLevel.cid, did));
        if (userLevels != null) {
            long levelAmount = user.getLongValue(TableUser.levelAmount);
            ModelObject level = GlobalService.userLevelService.getLevelByAmount(levelAmount);
            if (level == null) {
                return false;
            }
            boolean hasLevel = false;
            for (ModelObject ul : userLevels) {
                if (ul.getIntValue(TableCouponUserLevel.ulid) == level.getIntValue(TableUserLevel.id)
                        || ul.getIntValue(TableCouponUserLevel.ulid) == 0) {
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
                Criteria.query(TableCouponUser.class)
                        .eq(TableCouponUser.uid, uid)
                        .eq(TableCouponUser.cid, did));
        if (count >= 0 && c > count) {
            return false;
        }
        return true;
    }
}
