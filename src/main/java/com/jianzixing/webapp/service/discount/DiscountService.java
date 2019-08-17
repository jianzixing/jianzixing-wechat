package com.jianzixing.webapp.service.discount;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.order.*;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.exception.TransactionException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 活动有：
 * 1.满减
 * 2.满折
 * 3.优惠券
 * 4.X件Y折
 * 5.满选促销  ps:满99任选3件
 * 6.组合促销 ps:手机+手机壳优惠多少钱
 * 7.赠品促销 ps:满多少件赠送商品
 * 8.X件免Y件 ps:满3件免1件
 * 9.特价商品 ps:特价商品不参与任何活动计算
 *
 *
 * <p>
 * <p>
 * 1.满减区间 如：满10减1 满20减3
 * 2.满减递增 10减2 20减4 以此类推
 * 3.最大满减额
 * 4.可参与次数
 * 5.使用平台 pc app wap 全平台 微信
 * 6.适用会员等级  黄铜 白银 黄金
 */
public interface DiscountService {

    void addDiscount(ModelObject object) throws ModelCheckerException, ModuleException, TransactionException;

    void deleteDiscount(long id);

    void updateDiscount(ModelObject object) throws ModelCheckerException;

    Paging getDiscounts(ModelObject search, long start, long limit);

    void checkDiscountValid();

    List<ModelObject> getImpls();

    void enableDiscount(long id) throws ModuleException, TransactionException;

    void disableDiscount(long id) throws ModuleException, TransactionException;

    /**
     * 获取活动关联的商品列表
     *
     * @param did
     * @param start
     * @param limit
     * @return
     */
    Paging getDiscountGoods(long did, int start, int limit);

    /**
     * 用来获取一个商品包含的所有可能配置在活动列表中的值
     * 比如商品ID的值：G..
     * 商品品牌的值：B..
     * ...
     * 活动配置列表中配置的是商品分类还是商品品牌等等
     *
     * @param goods TableGoods表或者内含TableGoodsSku表
     * @return
     */
    Set<String> getGoodsDiscountKeySet(ModelObject goods);

    /**
     * 同上
     *
     * @param goods
     * @return
     */
    Set<String> getGoodsDiscountKeySet(List<ModelObject> goods);

    /**
     * 获取用户或者非用户的商品活动列表
     * 返回值map对应商品和活动或者反转为活动和商品
     *
     * @param user
     * @param goodsAndSku  TableGoods表或者内含TableGoodsSku表,goods = {... ,TableGoodsSku:{}}
     * @param platformType 获取特定平台的优惠活动
     * @param restrictDid  限制只读取restrictDid活动的商品和活动关联信息
     * @param reverse      Map的key和value对应的是goods和discount，是否反转key和value对应值
     * @return
     */
    Map<ModelObject, List<ModelObject>> getUserDiscountByGoods(ModelObject user,
                                                               List<ModelObject> goodsAndSku,
                                                               PlatformType platformType,
                                                               List<Long> restrictDid,
                                                               boolean reverse);

    /**
     * 属于
     * {@link DiscountService#getUserDiscountByGoods(ModelObject, List, PlatformType, boolean)}
     * 方法的包装
     *
     * @param user
     * @param goodsAndSku
     * @param platformType
     * @param reverse
     * @return
     */
    Map<ModelObject, List<ModelObject>> getUserDiscountByGoods(ModelObject user,
                                                               List<ModelObject> goodsAndSku,
                                                               PlatformType platformType,
                                                               boolean reverse);

    /**
     * 属于
     * {@link DiscountService#getUserDiscountByGoods(ModelObject, List, PlatformType, boolean)}
     * 方法的包装
     *
     * @param user
     * @param goodsAndSku
     * @param platformType
     * @return
     */
    List<ModelObject> getUserDiscountListByGoods(ModelObject user, List<ModelObject> goodsAndSku, PlatformType platformType);

    /**
     * 属于
     * {@link DiscountService#getUserDiscountByGoods(ModelObject, List, PlatformType, boolean)}
     * 方法的包装
     *
     * @param uid
     * @param gid
     * @param skuId
     * @param platformType
     * @return
     */
    List<ModelObject> getUserDiscountByGoods(long uid, long gid, long skuId, PlatformType platformType);

    /**
     * 获取一个活动的详细信息
     * [后台使用的方法]
     *
     * @param id
     * @return
     */
    ModelObject getDiscountById(long id);

    Paging getSimpleDiscountGoods(long did, long start, long limit);

    /**
     * 提供给下单时使用，用来计算当前订单商品中可以优惠的价格
     *
     * @param orderModel
     */
    List<OrderDiscountModel> calDiscount(OrderModel orderModel) throws ModuleException;

    /**
     * 提供给购物车使用，用来计算购物车的商品优惠价格
     * 计算好的优惠价格会存储在OrderDiscountModel中
     */
    void calDiscountByGoods(OrderModel orderModel, PlatformType platformType) throws ModuleException;

    List<ModelObject> getPlatforms();

    /**
     * 订单取消时退还优惠活动相关资源
     *
     * @param order
     */
    void handBack(ModelObject order);

    /**
     * 添加商品到促销活动中
     *
     * @param did
     * @param gids
     */
    void addDiscountGoods(long did, List<ModelObject> gids) throws ModuleException;

    /**
     * 从促销活动中移除一个商品
     */
    void removeDiscountGoods(long did, long gid, long skuId);
}
