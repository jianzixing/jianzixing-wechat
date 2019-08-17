package com.jianzixing.webapp.service.shopcart;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.order.OrderDiscountModel;
import com.jianzixing.webapp.service.order.OrderGoodsModel;
import com.jianzixing.webapp.service.order.OrderModel;
import com.jianzixing.webapp.service.order.PlatformType;
import com.jianzixing.webapp.tables.goods.TableGoods;
import com.jianzixing.webapp.tables.goods.TableGoodsProperty;
import com.jianzixing.webapp.tables.goods.TableGoodsSku;
import com.jianzixing.webapp.tables.shopcart.TableShoppingCart;
import com.jianzixing.webapp.tables.user.TableUser;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DefaultShoppingCartService implements ShoppingCartService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addCartGoods(ModelObject cart) throws Exception {

        long gid = cart.getLongValue(TableShoppingCart.gid);
        long skuId = cart.getLongValue(TableShoppingCart.skuId);
        long userId = cart.getLongValue(TableShoppingCart.userId);
        if (userId == 0) {
            throw new Exception("登录信息异常");
        }
        int amount = cart.getIntValue(TableShoppingCart.amount);
        if (amount == 0) {
            throw new Exception("参数异常");
        }
        if (gid != 0) {
            ModelObject goods = GlobalService.goodsService.getSimpleGoodsById(gid);
            if (goods != null) {
                int hasSku = goods.getIntValue(TableGoods.hasSku);
                ModelObject old = sessionTemplate.get(Criteria.query(TableShoppingCart.class)
                        .eq(TableShoppingCart.userId, userId)
                        .eq(TableShoppingCart.gid, gid)
                        .eq(TableShoppingCart.skuId, hasSku == 1 ? skuId : 0));
                if (old != null && !cart.containsKey("fast")) {
                    cart.put(TableShoppingCart.amount, amount + old.getIntValue(TableShoppingCart.amount));
                }

                ModelObject sku = hasSku == 1 ? GlobalService.goodsService.getSkuById(skuId) : null;
                if (hasSku == 0 || sku != null) {
                    cart.setObjectClass(TableShoppingCart.class);
                    if (old != null) {
                        cart.put(TableShoppingCart.id, old.getLongValue(TableShoppingCart.id));
                        sessionTemplate.update(cart);
                    } else {
                        cart.put(TableShoppingCart.createTime, new Date());
                        sessionTemplate.save(cart);
                    }
                }
            }
        } else {
            throw new Exception("商品异常");
        }
    }

    @Override
    public void modifyCartGoods(ModelObject cart) throws ModuleException {
        long userId = cart.getLongValue(TableShoppingCart.userId);
        if (userId == 0) {
            throw new ModuleException("not_login", "登录信息异常");
        }
        int amount = cart.getIntValue(TableShoppingCart.amount);
        if (cart.containsKey(TableShoppingCart.amount) && amount <= 0) {
            throw new ModuleException("empty_order_amount", "购买数量不能为0");
        }
        long gid = cart.getLongValue(TableShoppingCart.gid);
        if (gid == 0) {
            throw new ModuleException("empty_order_goods", "购买商品不能为空");
        }
        cart.setObjectClass(TableShoppingCart.class);

        ModelObject oldCart = sessionTemplate.get(Criteria.query(TableShoppingCart.class)
                .eq(TableShoppingCart.userId, userId)
                .eq(TableShoppingCart.gid, gid));
        if (oldCart != null) {
            cart.put(TableShoppingCart.id, oldCart.getLongValue(TableShoppingCart.id));
        }

        sessionTemplate.update(cart);
    }

    @Override
    public void modifyCartCheckState(ModelObject cart) throws ModuleException {
        if (!cart.containsKey(TableShoppingCart.id) || !cart.containsKey(TableShoppingCart.isChecked)) {
            throw new ModuleException("no_info", "信息不全");
        }
        cart.setObjectClass(TableShoppingCart.class);
        sessionTemplate.update(cart);
    }

    @Override
    public void deleteCartGoods(long userId, long goodsId, long skuId) {
        sessionTemplate.delete(Criteria.delete(TableShoppingCart.class)
                .eq(TableShoppingCart.userId, userId)
                .eq(TableShoppingCart.gid, goodsId)
                .eq(TableShoppingCart.skuId, skuId));
    }

    @Override
    public void deleteCartGoods(long userId, long goodsId) {
        deleteCartGoods(userId, goodsId, 0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ModelObject> getCarts(long userId) {
        List<ModelObject> carts = getCartModels(userId);
        if (carts != null && carts.size() > 0) {
            //先用ID查，后面再优化
            carts.forEach(item -> {
                List<ModelObject> discounts = GlobalService.discountService.getUserDiscountByGoods(userId, item.getInteger(TableShoppingCart.gid), item.getInteger(TableShoppingCart.skuId), PlatformType.WECHAT);
                if (discounts != null && discounts.size() > 0) {
                    discounts.forEach(discount -> {
                        discount.remove("params");
                    });
                }
                item.put("discount", discounts);
            });
        }
        return carts;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ModelObject> getCheckCarts(long userId) {
        List<ModelObject> carts = getCheckCartModels(userId);
        if (carts != null && carts.size() > 0) {
            //先用ID查，后面再优化
            carts.forEach(item -> {
                List<ModelObject> discounts = GlobalService.discountService.getUserDiscountByGoods(userId, item.getInteger(TableShoppingCart.gid), item.getInteger(TableShoppingCart.skuId), PlatformType.WECHAT);
                if (discounts != null && discounts.size() > 0) {
                    discounts.forEach(discount -> {
                        discount.remove("params");
                    });
                }
                item.put("discount", discounts);
            });
        }
        return carts;
    }

    @Override
    public List<OrderGoodsModel> getCartPriceTotal(ModelObject user) throws ModuleException {
        OrderModel orderModel = new OrderModel();
        orderModel.setUid(user.getIntValue(TableUser.id));
        orderModel.setUser(user);
        List<ModelObject> carts = getCheckCarts(user.getIntValue(TableUser.id));
        if (carts == null || carts.size() == 0) {
            return null;
        }
        //过滤未选中的商品

        List<Long> goodsIds = new ArrayList<>();
        List<Long> skuIds = new ArrayList<>();
        carts.forEach(cart -> {
            goodsIds.add(cart.getLongValue(TableShoppingCart.gid));
            skuIds.add(cart.getLongValue(TableShoppingCart.skuId));
        });

        List<ModelObject> goodsList = GlobalService.goodsService.getSimpleGoods(goodsIds);
        List<ModelObject> skuList = GlobalService.goodsService.getSimpleSkus(skuIds);

        List<OrderGoodsModel> orderGoodsModelList = new ArrayList<>();
        carts.forEach(cart -> {
            long gid = cart.getLongValue(TableShoppingCart.gid);
            long skuId = cart.getLongValue(TableShoppingCart.skuId);
            OrderGoodsModel orderGoods = new OrderGoodsModel();
            orderGoods.setGid(gid);
            orderGoods.setGoods(cart.getModelObject("TableGoods"));
            orderGoods.setSkuId(skuId);
            orderGoods.setSku(cart.getModelObject("TableGoodsSku"));
            orderGoods.setDiscountId(cart.getInteger(TableShoppingCart.discountId) == null ? -1 : cart.getIntValue(TableShoppingCart.discountId));

            ModelObject g = null;
            ModelObject s = null;
            for (ModelObject gi : goodsList) {
                if (gid == gi.getLongValue(TableGoods.id)) {
                    g = gi;
                    break;
                }
            }
            orderGoods.setGoods(g);

            if (skuList != null && skuList.size() > 0) {
                for (ModelObject si : skuList) {
                    if (gid == si.getLongValue(TableGoodsSku.id)) {
                        s = si;
                        break;
                    }
                }
                orderGoods.setSku(s);
            }

            orderGoods.setBuyAmount(cart.getIntValue(TableShoppingCart.amount));
            orderGoodsModelList.add(orderGoods);
        });
        orderModel.setProducts(orderGoodsModelList);
        GlobalService.discountService.calDiscountByGoods(orderModel, PlatformType.ALL);

        return orderGoodsModelList;
    }

    @Override
    public List<OrderGoodsModel> getCartPriceTotal(long userId) throws ModuleException {
        return getCartPriceTotal(GlobalService.userService.getUser(userId));
    }

    @Override
    public List<ModelObject> getUserCarts(long uid, List ids) {
        return sessionTemplate.list(Criteria.query(TableShoppingCart.class)
                .eq(TableShoppingCart.userId, uid)
                .in(TableShoppingCart.id, ids));
    }

    @Override
    public void deleteCarts(long uid, List<String> ids) {
        sessionTemplate.delete(Criteria.delete(TableShoppingCart.class)
                .eq(TableShoppingCart.userId, uid)
                .in(TableShoppingCart.id, ids));
    }

    @SuppressWarnings("unchecked")
    List<ModelObject> getCartModels(long userId) {
        return sessionTemplate.list(
                Criteria.query(TableShoppingCart.class)
                        .subjoin(TableGoods.class).eq(TableGoods.id, TableShoppingCart.gid).single().query()
                        .subjoin(TableGoodsSku.class).eq(TableGoodsSku.id, TableShoppingCart.skuId).single().query()
                        .subjoin(TableGoodsProperty.class).eq(TableGoodsSku.id, TableShoppingCart.skuId).query()
                        .eq(TableShoppingCart.userId, userId));
    }

    @SuppressWarnings("unchecked")
    List<ModelObject> getCheckCartModels(long userId) {
        return sessionTemplate.list(
                Criteria.query(TableShoppingCart.class)
                        .subjoin(TableGoods.class).eq(TableGoods.id, TableShoppingCart.gid).single().query()
                        .subjoin(TableGoodsSku.class).eq(TableGoodsSku.id, TableShoppingCart.skuId).single().query()
                        .subjoin(TableGoodsProperty.class).eq(TableGoodsSku.id, TableShoppingCart.skuId).query()
                        .eq(TableShoppingCart.userId, userId).eq(TableShoppingCart.isChecked, 1));
    }
}
