package com.jianzixing.webapp.service.goods;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.exception.TransactionException;

import java.util.List;
import java.util.Map;

/**
 * @author yangankang
 */
public interface GoodsService {
    int GOODS_STATUS_CREATE = 0;   //刚刚创建(未审核)
    int GOODS_STATUS_AUDIT = 10;  //已审核
    int GOODS_STATUS_DOWN = 20;  //下架
    int GOODS_STATUS_UP = 30;  //上架
    int GOODS_STATUS_INVALID = 40;  //无效(审核无效)
    int GOODS_STATUS_PASTDUE = 50;  //已过期
    /**
     * 实体物品
     */
    int GOODS_TYPE_ENTITY = 10;
    /**
     * 虚拟物品
     */
    int GOODS_TYPE_VIRTUAL = 11;

    /**
     * 服务(比如代办服务等等)
     */
    int GOODS_TYPE_SERVE = 20;

    int DELETE_YES = 1;
    int DELETE_NO = 0;

    void addGoods(int uid, ModelObject object, boolean isAdmin) throws ModuleException, TransactionException;

    void updateGoods(int uid, ModelObject object, boolean isAdmin) throws ModuleException, TransactionException;

    void deleteGoods(long goodsId);

    /**
     * 物理删除数据库中的商品信息
     *
     * @param goodsId
     * @throws TransactionException
     */
    void deleteEntityGoods(long goodsId) throws TransactionException;

    Paging getGoods(ModelObject search, long start, long limit, int gid);

    /**
     * 商品列表页搜索
     */
    ModelObject searchGoods(ModelObject search, long start, long limit);

    Paging getRecycleGoods(long start, long limit, String name);

    ModelObject getSimpleGoodsById(long id);

    ModelObject getSimpleSkuById(long skuId);

    ModelObject getSimpleOnlineGoodsById(long gid);

    List<ModelObject> getSimpleGoods(List<Long> ids);

    List<ModelObject> getSimpleSkus(List<Long> skus);

    /**
     * 从回收站中恢复商品
     *
     * @param id
     */
    void recoverGoods(long id);

    /**
     * 设置商品上架或者下架
     *
     * @param goodsId
     * @param isPutaway
     * @throws ModuleException
     */
    void setGoodsSaleStatus(long goodsId, boolean isPutaway) throws ModuleException;

    /**
     * 更新商品价格信息
     * 如果有sku则包括sku的价格信息都可以修改
     *
     * @param object
     * @param sku
     * @throws ModuleException
     * @throws TransactionException
     */
    void updateGoodsSales(ModelObject object, List<ModelObject> sku) throws ModuleException, TransactionException;

    void updateGoodsTitle(ModelObject object) throws ModuleException;

    void updateGoodsBrand(List<Long> ids, int bid);

    void updateGoodsValidTime(List<Integer> goodIds, long time);

    ModelObject getGoodsById(long id);

    void checkGoodsValidTime();

    void checkGoodsGroupsCount();

    boolean hasSku(ModelObject goods);

    ModelObject getSkuById(long skuId);

    ModelObject getPropertyBySku(long gid, long skuId);

    void deductAmount(long gid, long skuId, int buyAmount) throws ModuleException;

    void increaseAmount(long gid, long skuId, int buyAmount) throws ModuleException;

    List<ModelObject> getCountSimpleGoods(short count);

    ModelObject getSingleGoodsByPrice(double start, double end, double minPrice);

    boolean isUsedGid(int id);

    boolean isUsedPwid(int id);

    boolean isUsedBid(int id);

    List<ModelObject> getGoodsBySerialNumber(List<String> serialNumbers);

    List<ModelObject> getBaseGoodsByList(List<Long> nlist);

    /**
     * 获取页面需要的商品数据
     * 只获取上架或者下架状态的商品
     *
     * @param id
     * @return
     */
    ModelObject getViewGoodsById(long id);

    /**
     * 从购物车中获取商品信息
     *
     * @param uid
     * @param cartIds
     * @return
     */
    List<ModelObject> getOrderGoodsByCart(long uid, List cartIds);

    List<ModelObject> getParameterByGroupId(long gid);

    long getGoodsCount(GoodsStatus... statuses);

    /**
     * 获得一组推荐商品
     * 可能是随机推荐也可能是其它
     *
     * @param count
     * @return
     */
    List<ModelObject> getRecommendGoods(int count);

    /**
     * 通过商品分组获取商品列表
     *
     * @param goodsGroupIdList
     * @param start
     * @param limit
     * @return
     */
    Paging getGoodsInGroups(List<Integer> goodsGroupIdList, int start, int limit);

    /**
     * 通过商品id或者sku获取商品列表
     * map的key是商品id，value是skuid
     *
     * @param goodsIdList
     * @return
     */
    List<ModelObject> getGoodsWithSku(Map<Long, Long> goodsIdList, String skuValueName);
}
