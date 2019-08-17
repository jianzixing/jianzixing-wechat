package com.jianzixing.webapp.service.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.order.PlatformType;
import com.jianzixing.webapp.tables.discount.TableDiscount;
import com.jianzixing.webapp.tables.goods.*;
import com.jianzixing.webapp.tables.logistics.TableLogisticsTemplate;
import com.jianzixing.webapp.tables.shopcart.TableShoppingCart;
import com.jianzixing.webapp.tables.support.TableSupport;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.RandomUtils;
import org.mimosaframework.core.utils.StringTools;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Keyword;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.orm.transaction.Transaction;
import org.mimosaframework.orm.transaction.TransactionCallback;
import org.mimosaframework.orm.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

/**
 * @author yangankang
 */
@Service
public class DefaultGoodsService implements GoodsService {
    private static final Log logger = LogFactory.getLog(DefaultGoodsService.class);

    @Autowired
    SessionTemplate sessionTemplate;

    private int getHasSkuByInfo(ModelObject info) {
        int hasSku = info.getIntValue(TableGoods.hasSku);
        if (hasSku == 1) {
            hasSku = 1;
        } else {
            hasSku = 0;
        }
        info.put(TableGoods.hasSku, hasSku);
        return hasSku;
    }

    @Override
    public void addGoods(int uid, ModelObject object, boolean isAdmin) throws ModuleException, TransactionException {
        GoodsModel gm = this.processSaveOrUpdate(uid, object, isAdmin);
        ModelObject info = gm.getInfo();
        List<ModelObject> property = gm.getProperty();
        List<ModelObject> skuList = gm.getSkuList();
        List<ModelObject> imageList = gm.getImageList();
        ModelObject descObject = gm.getDescObject();

        int hasSku = this.getHasSkuByInfo(info);
        if (imageList != null && imageList.size() > 0 && imageList.get(0).containsKey(TableGoodsImage.fileName)) {
            info.put(TableGoods.fileName, imageList.get(0).getString(TableGoodsImage.fileName));
        }

        // 判断商品编码是否唯一
        info.put(TableGoods.createTime, new Date());

        sessionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean invoke(Transaction transaction) throws Exception {

                sessionTemplate.save(info);
                int goodsId = info.getIntValue(TableGoods.id);

                for (ModelObject attrItem : property) {
                    attrItem.put(TableGoodsProperty.goodsId, goodsId);
                    sessionTemplate.save(attrItem);
                }

                if (hasSku == 1 && skuList != null && skuList.size() > 0) {
                    for (ModelObject skuItem : skuList) {
                        skuItem.put(TableGoodsSku.goodsId, goodsId);
                        List<ModelObject> attrList = (List<ModelObject>) skuItem.get("property");
                        skuItem.remove("property");
                        sessionTemplate.save(skuItem);

                        int skuId = skuItem.getIntValue(TableGoodsSku.id);
                        if (skuId > 0) {
                            if (attrList != null) {
                                for (ModelObject attrItem : attrList) {
                                    attrItem.put(TableGoodsProperty.goodsId, goodsId);
                                    attrItem.put(TableGoodsProperty.skuId, skuId);

                                    sessionTemplate.save(attrItem);
                                }
                            }
                        }

                        // 如果没有商品编号则自动生成
                        String serialNumber = skuItem.getString(TableGoodsSku.serialNumber);
                        if (StringUtils.isBlank(serialNumber)) {
                            serialNumber = "S" + buildSerialNumber(skuId + "");
                            ModelObject skuSerialNumber = new ModelObject(TableGoodsSku.class);
                            skuSerialNumber.put(TableGoodsSku.id, skuId);
                            skuSerialNumber.put(TableGoodsSku.serialNumber, serialNumber);
                            sessionTemplate.update(skuSerialNumber);
                        }
                    }
                }

                int index = 0;
                for (ModelObject imageItem : imageList) {
                    imageItem.put(TableGoodsImage.goodsId, goodsId);
                    imageItem.put(TableGoodsImage.index, ++index);
                    sessionTemplate.save(imageItem);
                }

                descObject.put(TableGoodsDescribe.goodsId, goodsId);
                sessionTemplate.save(descObject);

                // 如果没有商品编号则自动生成
                String serialNumber = info.getString(TableGoodsSku.serialNumber);
                if (StringUtils.isBlank(serialNumber)) {
                    serialNumber = "G" + buildSerialNumber(goodsId + "");
                    ModelObject goodsSerialNumber = new ModelObject(TableGoods.class);
                    goodsSerialNumber.put(TableGoods.id, goodsId);
                    goodsSerialNumber.put(TableGoods.serialNumber, serialNumber);
                    sessionTemplate.update(goodsSerialNumber);
                }

                // 保存商品服务
                List<ModelObject> supports = gm.getSupports();
                if (supports != null) {
                    List<ModelObject> supportList = new ArrayList<>();
                    for (ModelObject support : supports) {
                        ModelObject goodsSupport = new ModelObject(TableGoodsSupport.class);
                        goodsSupport.put(TableGoodsSupport.goodsId, goodsId);
                        goodsSupport.put(TableGoodsSupport.supportId, support.getIntValue(TableSupport.id));
                        goodsSupport.put(TableGoodsSupport.supportName, support.getString(TableSupport.name));
                        supportList.add(goodsSupport);
                    }
                    sessionTemplate.save(supportList);
                }
                return true;
            }
        });
    }

    private String buildSerialNumber(String id) {
        if (id.length() < 8) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8 - id.length(); i++) {
                sb.append("0");
            }
            sb.append(id);
            return sb.toString();
        }
        return id;
    }

    @Override
    public void updateGoods(int uid, ModelObject object, boolean isAdmin) throws ModuleException, TransactionException {
        int goodsId = object.getIntValue(TableGoods.id);
        if (goodsId == 0) {
            throw new ModuleException(StockCode.ARG_NULL, "商品ID不存在");
        }

        ModelObject goods = sessionTemplate.get(TableGoods.class, goodsId);
        if (goods == null) {
            throw new ModuleException(StockCode.NOT_EXIST, "商品不存在,ID=" + goodsId);
        }

        GoodsModel gm = this.processSaveOrUpdate(uid, object, isAdmin);
        ModelObject info = gm.getInfo();
        List<ModelObject> property = gm.getProperty();
        List<ModelObject> skuList = gm.getSkuList();
        List<ModelObject> imageList = gm.getImageList();
        ModelObject descObject = gm.getDescObject();

        int hasSku = this.getHasSkuByInfo(info);
        if (imageList != null && imageList.size() > 0 && imageList.get(0).containsKey(TableGoodsImage.fileName)) {
            info.put(TableGoods.fileName, imageList.get(0).getString(TableGoodsImage.fileName));
        }
        info.put(TableGoods.id, goodsId);

        sessionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean invoke(Transaction transaction) throws Exception {
                List<ModelObject> oldProperty = sessionTemplate.query(TableGoodsProperty.class).eq(TableGoodsProperty.goodsId, goodsId).queries();
                List<ModelObject> oldSku = sessionTemplate.query(TableGoodsSku.class).eq(TableGoodsSku.goodsId, goodsId).queries();
                List<ModelObject> oldImage = sessionTemplate.query(TableGoodsImage.class).eq(TableGoodsImage.goodsId, goodsId).queries();

                if (descObject != null) {
                    sessionTemplate.update(TableGoodsDescribe.class)
                            .value(TableGoodsDescribe.desc, descObject.getString(TableGoodsDescribe.desc))
                            .eq(TableGoodsDescribe.goodsId, goodsId).update();
                }

                info.put(TableGoods.editTime, new Date());
                info.remove(TableGoods.createTime);
                sessionTemplate.update(info);

                Map<Integer, List<ModelObject>> skuMapProperty = new HashMap();
                {
                    List<ModelObject> normalProperty = new ArrayList();
                    if (oldProperty != null) {
                        oldProperty.forEach(m -> {
                            int skuId = m.getIntValue(TableGoodsProperty.skuId);
                            if (skuId == 0) {
                                normalProperty.add(m);
                            } else {
                                List<ModelObject> obj = skuMapProperty.get(skuId);
                                if (obj == null) obj = new ArrayList();
                                obj.add(m);
                                skuMapProperty.put(skuId, obj);
                            }
                        });
                    }

                    List<Integer> normalPropertyIds = new ArrayList();
                    normalProperty.forEach(m -> normalPropertyIds.add(m.getIntValue(TableGoodsProperty.id)));
                    if (property != null) {
                        property.forEach(m -> {
                            m.put(TableGoodsProperty.goodsId, goodsId);
                            if (normalPropertyIds.size() > 0) {
                                int id = normalPropertyIds.get(0);
                                m.put(TableGoodsProperty.id, id);
                                normalPropertyIds.remove(0);
                            }
                        });
                        if (normalPropertyIds.size() > 0) {
                            sessionTemplate.delete(TableGoodsProperty.class).in(TableGoodsProperty.id, normalPropertyIds).delete();
                        }
                        property.forEach(m -> sessionTemplate.saveAndUpdate(m));
                    }
                }

                {
                    if (hasSku == 1) {
                        if (oldSku == null) oldSku = new ArrayList<>();
                        if (skuList != null && skuList.size() > 0) {
                            List<ModelObject> updateOldSkuList = new ArrayList<>();
                            List<ModelObject> finalOldSku = oldSku;
                            skuList.forEach(s -> {
                                List<ModelObject> skuProperty = new ArrayList<>();
                                List list = s.getModelArray("property");
                                list.forEach(item -> skuProperty.add((ModelObject) item));
                                s.put(TableGoodsSku.goodsId, goodsId);

                                finalOldSku.forEach(os -> {
                                    int skuId = os.getIntValue(TableGoodsSku.id);
                                    List<ModelObject> child = skuMapProperty.get(skuId);
                                    if (!isSkuDiff(skuProperty, child)) {
                                        s.put(TableGoodsSku.id, skuId);
                                        updateOldSkuList.add(os);
                                    }
                                });
                            });

                            /**
                             * 删除没有在更新列表里的SKU信息
                             */
                            oldSku.removeAll(updateOldSkuList);
                            oldSku.forEach(os -> {
                                int skuId = os.getIntValue(TableGoodsSku.id);
                                sessionTemplate.delete(TableGoodsProperty.class)
                                        .eq(TableGoodsProperty.skuId, skuId).delete();
                                sessionTemplate.delete(TableGoodsSku.class, skuId);
                            });

                            /**
                             * 添加或者更新已经组织好的SKU信息
                             */
                            for (ModelObject skuItem : skuList) {
                                skuItem.put(TableGoodsSku.goodsId, goodsId);
                                List<ModelObject> attrList = (List<ModelObject>) skuItem.get("property");
                                skuItem.remove("property");
                                sessionTemplate.saveAndUpdate(skuItem);

                                int skuId = skuItem.getIntValue(TableGoodsSku.id);
                                if (skuId > 0) {
                                    if (attrList != null) {
                                        for (ModelObject attrItem : attrList) {
                                            attrItem.put(TableGoodsProperty.goodsId, goodsId);
                                            attrItem.put(TableGoodsProperty.skuId, skuId);
                                            sessionTemplate.saveAndUpdate(attrItem);
                                        }
                                    }
                                }
                            }
                        } else {
                            oldSku.forEach(os -> {
                                int skuId = os.getIntValue(TableGoodsSku.id);
                                sessionTemplate.delete(TableGoodsProperty.class)
                                        .eq(TableGoodsProperty.skuId, skuId).delete();
                                sessionTemplate.delete(TableGoodsSku.class, skuId);
                            });
                        }
                    }
                }

                {
                    sessionTemplate.delete(TableGoodsImage.class).eq(TableGoodsImage.goodsId, goodsId).delete();
                    if (imageList != null && imageList.size() > 0) {
                        int index = 0;
                        for (ModelObject img : imageList) {
                            img.setObjectClass(TableGoodsImage.class);
                            img.put(TableGoodsImage.goodsId, goodsId);
                            img.put(TableGoodsImage.index, ++index);
                        }
                        sessionTemplate.save(imageList);
                    }
                }

                // 保存商品服务
                List<ModelObject> supports = gm.getSupports();
                sessionTemplate.delete(Criteria.delete(TableGoodsSupport.class).eq(TableGoodsSupport.goodsId, goodsId));
                if (supports != null) {
                    List<ModelObject> supportList = new ArrayList<>();
                    for (ModelObject support : supports) {
                        ModelObject goodsSupport = new ModelObject(TableGoodsSupport.class);
                        goodsSupport.put(TableGoodsSupport.goodsId, goodsId);
                        goodsSupport.put(TableGoodsSupport.supportId, support.getIntValue(TableSupport.id));
                        goodsSupport.put(TableGoodsSupport.supportName, support.getString(TableSupport.name));
                        supportList.add(goodsSupport);
                    }
                    sessionTemplate.save(supportList);
                }
                return true;
            }
        });
    }

    private boolean isSkuDiff(List<ModelObject> first, List<ModelObject> second) {
        if (first.size() != second.size()) {
            return true;
        }

        boolean isDiff = false;
        for (ModelObject f : first) {
            int attrId = f.getIntValue(TableGoodsProperty.attrId);
            int valueId = f.getIntValue(TableGoodsProperty.valueId);
            final boolean[] has = {false};
            for (ModelObject s : second) {
                if (s.getIntValue(TableGoodsProperty.attrId) == attrId
                        && s.getIntValue(TableGoodsProperty.valueId) == valueId) {
                    has[0] = true;
                }
            }
            if (has[0] == false) {
                isDiff = true;
                break;
            }
        }
        if (!isDiff) {
            for (ModelObject f : first) {
                int attrId = f.getIntValue(TableGoodsProperty.attrId);
                int valueId = f.getIntValue(TableGoodsProperty.valueId);
                for (ModelObject s : second) {
                    if (s.getIntValue(TableGoodsProperty.attrId) == attrId
                            && s.getIntValue(TableGoodsProperty.valueId) == valueId) {
                        f.put(TableGoodsProperty.id, s.getIntValue(TableGoodsProperty.id));
                    }
                }
            }
        }

        return isDiff;
    }

    private GoodsModel processSaveOrUpdate(int uid, ModelObject object, boolean isAdmin) throws ModuleException {
        ModelObject info = object.getModelObject("info");
        ModelArray attrs = object.getModelArray("attrs");
        ModelArray images = object.getModelArray("images");
        ModelArray sku = object.getModelArray("sku");
        String desc = object.getString("desc");
        String supports = object.getString("supports");

        if (uid <= 0) {
            throw new ModuleException(StockCode.MUST_USER, "用户ID必须存在");
        }

        try {
            info.setObjectClass(TableGoods.class);
            if (isAdmin) {
                info.put(TableGoods.adminId, uid);
            } else {
                info.put(TableGoods.userId, uid);
            }
            int type = info.getIntValue(TableGoods.type);
            if (type == 0 || (type != GoodsService.GOODS_TYPE_ENTITY && type != GoodsService.GOODS_TYPE_VIRTUAL)) {
                info.put(TableGoods.type, GoodsService.GOODS_TYPE_ENTITY);
            }
            if (isAdmin) {
                info.put(TableGoods.status, GOODS_STATUS_DOWN);
            } else {
                info.put(TableGoods.status, GOODS_STATUS_CREATE);
            }
            info.put(TableGoods.sellTotal, 0);
            info.put(TableGoods.isDelete, 0);
            info.remove(TableGoods.id);
            info.checkAndThrowable();
        } catch (ModelCheckerException e) {
            throw new ModuleException(e);
        }

        List<ModelObject> property = new ArrayList();
        List<ModelObject> skuList = new ArrayList();
        List<ModelObject> imageList = new ArrayList();
        ModelObject descObject = new ModelObject(TableGoodsDescribe.class);
        descObject.put(TableGoodsDescribe.desc, desc);

        int skuTotalAmount = 0;
        {
            //处理attrs普通属性的值
            if (attrs != null) {
                Iterator iterator = attrs.iterator();
                while (iterator.hasNext()) {
                    ModelObject attr = (ModelObject) iterator.next();
                    int attrId = attr.getIntValue("attrId");
                    String name = attr.getString("name");
                    int pos = attr.getIntValue("pos");

                    ModelArray values = attr.getModelArray("values");
                    if (values != null) {
                        Iterator vi = values.iterator();
                        while (vi.hasNext()) {
                            ModelObject value = (ModelObject) vi.next();
                            int valueId = value.getIntValue("id");
                            String valueName = value.getString("name");

                            if (StringUtils.isBlank(name) || StringUtils.isBlank(valueName)) {
                                throw new ModuleException(StockCode.ARG_NULL, "普通属性和属性值名称都不能为空");
                            }

                            ModelObject po = new ModelObject();
                            po.setObjectClass(TableGoodsProperty.class);
                            po.put(TableGoodsProperty.attrId, attrId);
                            po.put(TableGoodsProperty.attrName, name);
                            po.put(TableGoodsProperty.valueId, valueId);
                            po.put(TableGoodsProperty.valueName, valueName);
                            po.put(TableGoodsProperty.pos, pos);
                            property.add(po);
                        }
                    }
                }
            }
        }

        {
            //处理sku规格属性的值
            if (sku != null) {
                Iterator iterator = sku.iterator();
                while (iterator.hasNext()) {
                    ModelObject attr = (ModelObject) iterator.next();
                    double price = attr.getDoubleValue("price");
                    double vipPrice = attr.getDoubleValue("vipPrice");
                    double originalPrice = attr.getDoubleValue("originalPrice");
                    double costPrice = attr.getDoubleValue("costPrice");
                    int amount = attr.getIntValue("amount");
                    String serialNumber = attr.getString("serialNumber");

                    if (price <= 0) {
                        throw new ModuleException(StockCode.TOO_SMALL, "多SKU销售价格不能小于零(最低0.01元)");
                    }
                    if (amount <= 0) {
                        throw new ModuleException(StockCode.TOO_SMALL, "多SKU库存数量不能小于零(最低为1)");
                    }

                    skuTotalAmount += amount;

                    ModelObject skuObj = new ModelObject();
                    skuObj.setObjectClass(TableGoodsSku.class);
                    skuObj.put(TableGoodsSku.price, price);
                    skuObj.put(TableGoodsSku.vipPrice, vipPrice);
                    skuObj.put(TableGoodsSku.originalPrice, originalPrice);
                    skuObj.put(TableGoodsSku.costPrice, costPrice);
                    skuObj.put(TableGoodsSku.amount, amount);
                    skuObj.put(TableGoodsSku.serialNumber, serialNumber);
                    skuList.add(skuObj);

                    ModelArray values = attr.getModelArray("values");
                    if (values != null) {
                        List<ModelObject> objects = new ArrayList();
                        Iterator valueit = values.iterator();
                        while (valueit.hasNext()) {
                            ModelObject value = (ModelObject) valueit.next();
                            ModelObject valueAttr = value.getModelObject("attr");

                            int attrId = valueAttr.getIntValue("id");
                            String name = valueAttr.getString("name");
                            int valueId = value.getIntValue("id");
                            String valueName = value.getString("value");

                            if (StringUtils.isBlank(name) || StringUtils.isBlank(valueName)) {
                                throw new ModuleException(StockCode.ARG_NULL, "销售属性和属性值名称都不能为空");
                            }

                            ModelObject po = new ModelObject();
                            po.setObjectClass(TableGoodsProperty.class);
                            po.put(TableGoodsProperty.attrId, attrId);
                            po.put(TableGoodsProperty.attrName, name);
                            po.put(TableGoodsProperty.valueId, valueId);
                            po.put(TableGoodsProperty.valueName, valueName);
                            objects.add(po);
                        }
                        skuObj.put("property", objects);
                    }
                }
            }
        }

        {
            //处理图片信息
            if (images != null) {
                Iterator iterator = images.iterator();
                while (iterator.hasNext()) {
                    ModelObject img = (ModelObject) iterator.next();
                    ModelArray fileNames = img.getModelArray("images");
                    int attrId = img.getIntValue("attrId");
                    int valueId = img.getIntValue("valueId");
                    Iterator it = fileNames.iterator();
                    while (it.hasNext()) {
                        String fileName = (String) it.next();

                        ModelObject imgObj = new ModelObject();
                        imgObj.setObjectClass(TableGoodsImage.class);
                        imgObj.put(TableGoodsImage.attrId, attrId);
                        imgObj.put(TableGoodsImage.valueId, valueId);
                        imgObj.put(TableGoodsImage.fileName, fileName);
                        imageList.add(imgObj);
                    }
                }
            }
        }

        if (skuTotalAmount != 0 && skuList.size() > 0) {
            info.put(TableGoods.amount, skuTotalAmount);
            info.put(TableGoods.hasSku, 1);
        }

        if (skuList.size() <= 0) {
            double price = info.getDoubleValue(TableGoods.price);
            if (price <= 0) {
                throw new ModuleException(StockCode.TOO_SMALL, "单SKU销售价格不能小于零(最低0.01元)");
            }
            int amount = info.getIntValue(TableGoods.amount);
            if (amount <= 0) {
                throw new ModuleException(StockCode.TOO_SMALL, "单SKU库存数量不能小于零(最低为1)");
            }
        }

        GoodsModel goodsModel = new GoodsModel();

        {
            // 处理商品服务信息
            if (StringUtils.isNotBlank(supports)) {
                String[] s1 = supports.split(",");
                List<ModelObject> supportObj = GlobalService.supportService.getSupportByArray(Arrays.asList(s1));
                goodsModel.setSupports(supportObj);
            }
        }

        goodsModel.setInfo(info);
        goodsModel.setAttrs(attrs);
        goodsModel.setImages(images);
        goodsModel.setSku(sku);
        goodsModel.setDesc(desc);
        goodsModel.setProperty(property);
        goodsModel.setSkuList(skuList);
        goodsModel.setImageList(imageList);
        goodsModel.setDescObject(descObject);
        return goodsModel;
    }

    @Override
    public void deleteGoods(long goodsId) {
        ModelObject object = new ModelObject(TableGoods.class);
        object.put(TableGoods.id, goodsId);
        object.put(TableGoods.isDelete, 1);
        sessionTemplate.update(object);
    }

    @Override
    public void deleteEntityGoods(long goodsId) throws TransactionException {
        sessionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean invoke(Transaction transaction) throws Exception {
                sessionTemplate.delete(TableGoodsDescribe.class)
                        .eq(TableGoodsDescribe.goodsId, goodsId).delete();
                sessionTemplate.delete(TableGoodsImage.class)
                        .eq(TableGoodsImage.goodsId, goodsId).delete();
                sessionTemplate.delete(TableGoodsProperty.class)
                        .eq(TableGoodsProperty.goodsId, goodsId).delete();
                sessionTemplate.delete(TableGoodsSku.class)
                        .eq(TableGoodsSku.goodsId, goodsId).delete();
                sessionTemplate.delete(TableGoods.class)
                        .eq(TableGoods.id, goodsId).delete();
                return true;
            }
        });
    }

    private void setGoodsQuery(Query query) {
        query.addSubjoin(Criteria.join(TableGoodsSku.class).join(TableGoodsSku.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsProperty.class).join(TableGoodsProperty.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsImage.class).join(TableGoodsImage.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsDescribe.class).join(TableGoodsDescribe.goodsId, TableGoods.id).single())
                .addSubjoin(Criteria.join(TableGoodsGroup.class).join(TableGoodsGroup.id, TableGoods.gid).single())
                .addSubjoin(Criteria.join(TableGoodsBrand.class).join(TableGoodsBrand.id, TableGoods.bid).single())
                .addSubjoin(Criteria.join(TableLogisticsTemplate.class).join(TableLogisticsTemplate.id, TableGoods.pwid).single())
                .eq(TableGoods.isDelete, DELETE_NO);
    }

    @Override
    public Paging getGoods(ModelObject search, long start, long limit, int gid) {
        Query query = Criteria.query(TableGoods.class);
        this.setGoodsQuery(query);
        query.order(TableGoods.id, false)
                .limit(start, limit);

        if (gid != 0) {
            query.eq(TableGoods.gid, gid);
            if (search != null) {
                search.put(TableGoods.gid, gid);
            }
        }

        ModelUtils.setLikeSearch(search, "name");
        ModelUtils.setValue2Integer(search, "hasSku");
        Paging paging = ModelUtils.getSearch("goods", sessionTemplate, search, query, TableGoods.id);
        if (paging != null && paging.getObjects() != null) {
            List<ModelObject> goods = paging.getObjects();
            Map<ModelObject, List<ModelObject>> discounts = GlobalService.discountService.getUserDiscountByGoods(null, goods, PlatformType.ALL, false);
            for (ModelObject g : goods) {
                if (discounts != null) {
                    g.put(TableDiscount.class.getSimpleName(), discounts.get(g));
                }
            }
        }

        return paging;
    }

    @Override
    public ModelObject searchGoods(ModelObject search, long start, long limit) {
        Query query = Criteria.query(TableGoods.class)
                .eq(TableGoods.status, 30)
                .eq(TableGoods.isDelete, DELETE_NO)
                .limit(start, limit);

        if (search.isNotEmpty("name")) {
            query.like(TableGoods.name, "%" + search.getString("name") + "%");
        }

        if (search.isNotEmpty("gId")) {
            List<ModelObject> children = GlobalService.goodsGroupService.getChildrenGroups(search.getIntValue("gId"));
            if (children != null && children.size() > 0) {
                List<Integer> gIds = new ArrayList<>();
                gIds.add(search.getIntValue("gId"));
                children.forEach(item -> {
                    gIds.add(item.getIntValue("id"));
                });
                query.in(TableGoods.gid, gIds);
            } else {
                query.eq(TableGoods.gid, search.getIntValue("gId"));
            }
        }

        if (search.isNotEmpty("brandId")) {
            String[] brandIds = search.getString("brandId").split(",");
            ArrayList brandIdList = new ArrayList();
            brandIdList.addAll(Arrays.asList(brandIds));
            query.in(TableGoods.bid, brandIdList);
        }
        if (search.isNotEmpty("minPrice")) {
            query.gt(TableGoods.price, search.getFloatValue("minPrice"));
        }

        if (search.isNotEmpty("maxPrice")) {
            query.lt(TableGoods.price, search.getFloatValue("maxPrice"));
        }

        //属性过滤
        if (search.isNotEmpty("parameter")) { //p=1-1,3,4_2-5,6
            String[] ps = search.getString("parameter").split("[_]");
            int length = ps.length;
            Map<Integer, Integer> goodsIdMap = new HashMap<>();
            for (String p : ps) {
                ModelObject parameter = new ModelObject();
                if (p.split("-").length == 2) {
                    parameter.put("attrId", p.split("-")[0]);
                    parameter.put("valueIds", p.split("-")[1].split(","));
                    try {
                        List<ModelObject> goodsIdList = sessionTemplate.getAutonomously("goods.getGoodsIdByProperty", parameter).getObjects();
                        if (goodsIdList == null || goodsIdList.size() == 0) { //本次搜索无结果
                            goodsIdMap.clear();
                            break;
                        } else {
                            goodsIdList.forEach(goodsIdModelObject -> {
                                int goodsId = goodsIdModelObject.getInteger(TableGoodsProperty.goodsId);
                                if (goodsIdMap.containsKey(goodsId)) {
                                    goodsIdMap.put(goodsId, goodsIdMap.get(goodsId) + 1);
                                } else {
                                    goodsIdMap.put(goodsId, 1);
                                }
                            });
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (goodsIdMap.isEmpty()) {
                query.eq(TableGoods.id, -1);
            } else {
                List<Integer> goodsIdList = new ArrayList<>();
                for (Integer goodsId : goodsIdMap.keySet()) {
                    if (goodsIdMap.get(goodsId) == length) { //所有的属性条件都符合
                        goodsIdList.add(goodsId);
                    }
                }
                if (goodsIdList.size() == 0) {
                    query.eq(TableGoods.id, -1);
                } else {
                    query.in(TableGoods.id, goodsIdList);
                }
            }
        }

        //sort 1综合 2销量 3价格高往低 4价格低往高
        int sort = StringTools.toInt(search.getString("sort"), 1);
        if (sort == 1) {
            query.order(TableGoods.id, false);
        } else if (sort == 2) {
            query.order(TableGoods.sellTotal, false);
        } else if (sort == 3) {
            query.order(TableGoods.price, false);
        } else if (sort == 4) {
            query.order(TableGoods.price, true);
        }

        ModelObject result = new ModelObject();
        Paging paging = sessionTemplate.paging(query);
        if (paging != null && paging.getCount() > 0) {
            List<ModelObject> goods = paging.getObjects();
            Map<ModelObject, List<ModelObject>> discounts = GlobalService.discountService.getUserDiscountByGoods(search.getModelObject("user"), goods, PlatformType.ALL, false);
            for (ModelObject g : goods) {
                if (discounts != null) {
                    g.put(TableDiscount.class.getSimpleName(), discounts.get(g));
                }
            }
        }
        result.put("paging", paging);

        if (start == 0) { //第一页的时候查品牌列表
            try {
                List<ModelObject> bidModelObjectList = sessionTemplate.getAutonomously("goods.getBidBySearch", search).getObjects();
                if (bidModelObjectList != null && bidModelObjectList.size() > 0) {
                    List<Integer> bidList = new ArrayList<>();
                    bidModelObjectList.forEach(bidModelObject -> {
                        bidList.add(bidModelObject.getInteger(TableGoods.bid));
                    });
                    result.put("brandList", GlobalService.goodsBrandService.getBrands(bidList));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    @Override
    public Paging getRecycleGoods(long start, long limit, String name) {
        Query query = sessionTemplate.query(TableGoods.class)
                .addSubjoin(Criteria.join(TableGoodsSku.class).join(TableGoodsSku.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsProperty.class).join(TableGoodsProperty.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsImage.class).join(TableGoodsImage.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsDescribe.class).join(TableGoodsDescribe.goodsId, TableGoods.id).single())
                .addSubjoin(Criteria.join(TableGoodsGroup.class).join(TableGoodsGroup.id, TableGoods.gid).single())
                .addSubjoin(Criteria.join(TableGoodsBrand.class).join(TableGoodsBrand.id, TableGoods.bid).single())
                .eq(TableGoods.isDelete, DELETE_YES)
                .order(TableGoods.id, false)
                .limit(start, limit);

        if (StringUtils.isNotBlank(name)) {
            query.like(TableGoods.name, "%" + name + "%");
        }

        return query.paging();
    }

    @Override
    public ModelObject getSimpleGoodsById(long id) {
        return sessionTemplate.get(TableGoods.class, id);
    }

    @Override
    public ModelObject getSimpleSkuById(long skuId) {
        return sessionTemplate.get(TableGoodsSku.class, skuId);
    }

    @Override
    public ModelObject getSimpleOnlineGoodsById(long gid) {
        return sessionTemplate.get(
                Criteria.query(TableGoods.class)
                        .eq(TableGoods.id, gid)
                        .eq(TableGoods.status, GoodsStatus.UP.getCode())
                        .eq(TableGoods.isDelete, 0)
        );
    }

    @Override
    public List<ModelObject> getSimpleGoods(List<Long> ids) {
        return sessionTemplate.list(
                Criteria.query(TableGoods.class)
                        .in(TableGoods.id, ids)
        );
    }

    @Override
    public List<ModelObject> getSimpleSkus(List<Long> skus) {
        return sessionTemplate.list(
                Criteria.query(TableGoodsSku.class)
                        .subjoin(TableGoodsProperty.class)
                        .eq(TableGoodsProperty.skuId, TableGoodsSku.id)
                        .eq(TableGoodsProperty.goodsId, TableGoodsSku.goodsId).query()
                        .in(TableGoodsSku.id, skus)
        );
    }

    @Override
    public void recoverGoods(long id) {
        ModelObject object = new ModelObject(TableGoods.class);
        object.put(TableGoods.id, id);
        object.put(TableGoods.isDelete, DELETE_NO);
        sessionTemplate.update(object);
    }

    @Override
    public void setGoodsSaleStatus(long goodsId, boolean isPutaway) throws ModuleException {
        ModelObject goods = sessionTemplate.query(TableGoods.class)
                .eq(TableGoods.id, goodsId).query();
        if (goods != null) {
            int status = goods.getIntValue(TableGoods.status);
            int isDel = goods.getIntValue(TableGoods.isDelete);

            if (isDel == DELETE_YES) {
                throw new ModuleException(StockCode.STATUS_ERROR, "商品当前在回收站内无法上下架");
            }

            if (status != GOODS_STATUS_AUDIT
                    && status != GOODS_STATUS_DOWN
                    && status != GOODS_STATUS_UP) {
                if (status == GOODS_STATUS_PASTDUE && !isPutaway) {
                    // 允许将过期商品重新下架后再上架
                } else {
                    if (status == GOODS_STATUS_PASTDUE) {
                        throw new ModuleException(StockCode.STATUS_ERROR, "已过期商品如果重新上架需要先设置为下架状态，并且重置有效期时间！");
                    } else {
                        throw new ModuleException(StockCode.STATUS_ERROR, "商品当前状态不允许上下架");
                    }
                }
            }

            ModelObject object = new ModelObject(TableGoods.class);
            object.put(TableGoods.id, goodsId);
            if (isPutaway) {
                object.put(TableGoods.status, GOODS_STATUS_UP);
            } else {
                object.put(TableGoods.status, GOODS_STATUS_DOWN);
            }

            sessionTemplate.update(object);
        }
    }

    @Override
    public void updateGoodsSales(ModelObject object, List<ModelObject> sku) throws ModuleException, TransactionException {
        int goodsId = object.getIntValue(TableGoods.id);
        double price = object.getDoubleValue(TableGoods.price);
        double vipPrice = object.getDoubleValue(TableGoods.vipPrice);
        double originalPrice = object.getDoubleValue(TableGoods.originalPrice);
        double costPrice = object.getDoubleValue(TableGoods.costPrice);
        String serialNumber = object.getString(TableGoods.serialNumber);
        int amount = object.getIntValue(TableGoods.amount);

        if (goodsId == 0) {
            throw new ModuleException(StockCode.ARG_NULL, "更新售卖信息的商品ID必须存在");
        }

        ModelObject goods = new ModelObject(TableGoods.class);
        goods.put(TableGoods.id, goodsId);
        goods.put(TableGoods.price, price);
        goods.put(TableGoods.vipPrice, vipPrice);
        goods.put(TableGoods.originalPrice, originalPrice);
        goods.put(TableGoods.costPrice, costPrice);
        goods.put(TableGoods.serialNumber, serialNumber);

        List<ModelObject> skus = sessionTemplate.query(TableGoodsSku.class).eq(TableGoodsSku.goodsId, goodsId).queries();
        List<ModelObject> newSku = new ArrayList();
        if (sku != null && sku.size() > 0) {
            int skuAmountCount = 0;
            for (ModelObject o : sku) {
                int skuId = o.getIntValue(TableGoodsSku.id);
                int skuAmount = o.getIntValue(TableGoodsSku.amount);
                int skuPrice = o.getIntValue(TableGoodsSku.price);
                int skuVipPrice = o.getIntValue(TableGoodsSku.vipPrice);
                int skuOriginalPrice = o.getIntValue(TableGoodsSku.originalPrice);
                int skuCostPrice = o.getIntValue(TableGoodsSku.costPrice);
                String skuSerialNumber = o.getString(TableGoodsSku.serialNumber);
                skuAmountCount += skuAmount;
                boolean has = false;
                for (ModelObject skuObj : skus) {
                    if (skuObj.getIntValue(TableGoodsSku.id) == skuId) {
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    throw new ModuleException(StockCode.NOT_EXIST, "规格SKU修改中包含非当前商品的规格信息");
                }

                ModelObject si = new ModelObject(TableGoodsSku.class);
                si.put(TableGoodsSku.id, skuId);
                si.put(TableGoodsSku.amount, skuAmount);
                si.put(TableGoodsSku.price, skuPrice);
                si.put(TableGoodsSku.vipPrice, skuVipPrice);
                si.put(TableGoodsSku.originalPrice, skuOriginalPrice);
                si.put(TableGoodsSku.costPrice, skuCostPrice);
                si.put(TableGoodsSku.serialNumber, skuSerialNumber);
                newSku.add(si);
            }
            amount = skuAmountCount;
        }
        goods.put(TableGoods.amount, amount);
        goods.put(TableGoods.editTime, new Date());

        sessionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean invoke(Transaction transaction) throws Exception {
                for (ModelObject s : newSku) {
                    sessionTemplate.update(s);
                }
                sessionTemplate.update(goods);
                return true;
            }
        });
    }

    @Override
    public void updateGoodsTitle(ModelObject object) throws ModuleException {
        int goodsId = object.getIntValue(TableGoods.id);
        if (goodsId == 0) throw new ModuleException(StockCode.ARG_NULL, "更新商品标题ID不能为空");
        String name = object.getString(TableGoods.name);
        String subtitle = object.getString(TableGoods.subtitle);

        ModelObject p = new ModelObject(TableGoods.class);
        p.put(TableGoods.id, goodsId);
        p.put(TableGoods.name, name);
        p.put(TableGoods.subtitle, subtitle);
        p.put(TableGoods.editTime, new Date());
        sessionTemplate.update(p);
    }

    @Override
    public void updateGoodsBrand(List<Long> ids, int bid) {
        sessionTemplate.update(TableGoods.class)
                .value(TableGoods.bid, bid)
                .in(TableGoods.id, ids)
                .update();
    }

    @Override
    public void updateGoodsValidTime(List<Integer> goodIds, long time) {
        sessionTemplate.update(TableGoods.class)
                .value(TableGoods.validTime, time > 0 ? new Date(time) : Keyword.NULL)
                .in(TableGoods.id, goodIds)
                .update();
    }

    @Override
    public ModelObject getGoodsById(long id) {
        Query query = sessionTemplate.query(TableGoods.class)
                .addSubjoin(Criteria.join(TableGoodsSupport.class).join(TableGoodsSupport.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsSku.class).join(TableGoodsSku.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsProperty.class).join(TableGoodsProperty.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsImage.class).join(TableGoodsImage.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsDescribe.class).join(TableGoodsDescribe.goodsId, TableGoods.id).single())
                .addSubjoin(Criteria.join(TableGoodsGroup.class).join(TableGoodsGroup.id, TableGoods.gid).single())
                .addSubjoin(Criteria.join(TableGoodsBrand.class).join(TableGoodsBrand.id, TableGoods.bid).single())
                .eq(TableGoods.id, id);

        return query.query();
    }

    @Override
    public void checkGoodsValidTime() {
        List<ModelObject> objects = sessionTemplate.query(TableGoods.class)
                .eq(TableGoods.isDelete, 0)
                .lte(TableGoods.validTime, new Date())
                .limit(0, 500)
                .isNotNull(TableGoods.validTime)
                .eq(TableGoods.status, GOODS_STATUS_UP)
                .queries();

        // 开始计算有效期
        if (objects != null) {
            for (ModelObject m : objects) {
                int goodsId = m.getIntValue(TableGoods.id);
                ModelObject object = new ModelObject(TableGoods.class);
                object.put(TableGoods.id, goodsId);
                object.put(TableGoods.status, GOODS_STATUS_PASTDUE);
                // object.put(TableGoods.validTime, Keyword.NULL);
                sessionTemplate.update(object);
                logger.info("检测到商品[" + goodsId + "][" + m.getString(TableGoods.name) + "]已过期并下架");
            }
        }
    }

    @Override
    public void checkGoodsGroupsCount() {
        List<ModelObject> groups = sessionTemplate.list(
                Criteria.query(TableGoodsGroup.class)
        );
        if (groups != null) {
            // 开始计算每一个组的数据
            for (ModelObject group : groups) {
                int id = group.getIntValue(TableGoodsGroup.id);
                this.checkGoodsGroupCount(id);
            }

            // 开始计算父子组和值
            this.checkGoodsGroupParentCount(0);
        }
    }

    private long checkGoodsGroupParentCount(int pid) {
        List<ModelObject> objects = sessionTemplate.list(
                Criteria.query(TableGoodsGroup.class)
                        .eq(TableGoodsGroup.pid, pid)
        );
        long totalCount = 0;
        if (objects != null) {
            for (ModelObject object : objects) {
                int id = object.getIntValue(TableGoodsGroup.id);
                long count = this.checkGoodsGroupParentCount(id);
                long selfCount = object.getIntValue(TableGoodsGroup.count);
                totalCount += count + selfCount;
                ModelObject update = new ModelObject(TableGoodsGroup.class);
                update.put(TableGoodsGroup.id, id);
                update.put(TableGoodsGroup.count, selfCount + count);
                sessionTemplate.update(update);
            }
        }
        return totalCount;
    }

    private void checkGoodsGroupCount(int id) {
        long count = sessionTemplate.count(
                Criteria.query(TableGoods.class)
                        .eq(TableGoods.isDelete, 0)
                        .eq(TableGoods.gid, id)
        );
        ModelObject object = new ModelObject(TableGoodsGroup.class);
        object.put(TableGoodsGroup.id, id);
        object.put(TableGoodsGroup.count, count);
        sessionTemplate.update(object);
    }

    @Override
    public boolean hasSku(ModelObject goods) {
        return goods != null && goods.getIntValue(TableGoods.hasSku) == 1 ? true : false;
    }

    @Override
    public ModelObject getSkuById(long skuId) {
        return sessionTemplate.get(Criteria.query(TableGoodsSku.class).eq(TableGoodsSku.id, skuId));
    }

    @Override
    public ModelObject getPropertyBySku(long gid, long skuId) {
        return sessionTemplate.get(
                Criteria.query(TableGoodsProperty.class)
                        .eq(TableGoodsProperty.goodsId, gid)
                        .eq(TableGoodsProperty.skuId, skuId)
        );
    }

    @Override
    public void deductAmount(long gid, long skuId, int buyAmount) throws ModuleException {
        if (skuId != 0 && skuId > 0) {
            long count = sessionTemplate.update(
                    Criteria.update(TableGoodsSku.class)
                            .subSelf(TableGoodsSku.amount, buyAmount)
                            .eq(TableGoodsSku.id, skuId)
                            .gt(TableGoodsSku.amount, 0)
            );
            if (count == 0) {
                throw new ModuleException(StockCode.TOO_SMALL, "库存小于0更新失败");
            }
        }
        long count = sessionTemplate.update(
                Criteria.update(TableGoods.class)
                        .subSelf(TableGoods.amount, buyAmount)
                        .eq(TableGoods.id, gid)
                        .gt(TableGoods.amount, 0)
        );
        ModelObject goods = sessionTemplate.get(Criteria.query(TableGoods.class).eq(TableGoods.id, gid));
        if (goods != null) {
            long amount = goods.getLongValue(TableGoods.amount);
            if (amount <= 0 && goods.getIntValue(TableGoods.status) == GoodsStatus.UP.getCode()) {
                sessionTemplate.update(
                        Criteria.update(TableGoods.class)
                                .eq(TableGoods.id, gid)
                                .value(TableGoods.status, GoodsStatus.DOWN.getCode())
                );
            }
        }

        if (count == 0) {
            throw new ModuleException(StockCode.TOO_SMALL, "库存小于0更新失败");
        }
    }

    @Override
    public void increaseAmount(long gid, long skuId, int buyAmount) throws ModuleException {
        if (skuId != 0) {
            sessionTemplate.update(
                    Criteria.update(TableGoodsSku.class)
                            .addSelf(TableGoodsSku.amount, buyAmount)
                            .eq(TableGoodsSku.id, skuId)
            );
        }
        sessionTemplate.update(
                Criteria.update(TableGoods.class)
                        .addSelf(TableGoods.amount, buyAmount)
                        .eq(TableGoods.id, skuId)
        );
    }

    @Override
    public List<ModelObject> getCountSimpleGoods(short count) {
        Query query = sessionTemplate.query(TableGoods.class)
                .eq(TableGoods.status, GoodsStatus.UP.getCode())
                .limit(0, count)
                .order(TableGoods.id, false);

        List<ModelObject> objects = query.queries();
        return objects;
    }

    @Override
    public ModelObject getSingleGoodsByPrice(double start, double end, double minPrice) {
        Query query = Criteria.query(TableGoods.class)
                .eq(TableGoods.status, GoodsStatus.UP.getCode())
                .gt(TableGoods.amount, 0)
                .limit(0, 1);
        if (start < minPrice) {
            start = minPrice;
        }
        if (start > 0 && end > 0) query.between(TableGoods.price, start, end);
        if (start > 0 && end <= 0) query.gte(TableGoods.price, start);
        if (start <= 0 && end > 0) query.lte(TableGoods.price, end);
        query.order(TableGoods.price, false);

        return sessionTemplate.get(query);
    }

    @Override
    public boolean isUsedGid(int id) {
        List list = sessionTemplate.list(Criteria.query(TableGoods.class).eq(TableGoods.gid, id).limit(0, 1));
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isUsedPwid(int id) {
        // 运费模板
        List list = sessionTemplate.list(Criteria.query(TableGoods.class).eq(TableGoods.pwid, id).limit(0, 1));
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isUsedBid(int id) {
        // 品牌
        List list = sessionTemplate.list(Criteria.query(TableGoods.class).eq(TableGoods.bid, id).limit(0, 1));
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<ModelObject> getGoodsBySerialNumber(List<String> serialNumbers) {
        List<ModelObject> skus = sessionTemplate.list(Criteria.query(TableGoodsSku.class).in(TableGoodsSku.serialNumber, serialNumbers));

        if (skus != null) {
            for (ModelObject sku : skus) {
                sku.setObjectClass(TableGoodsSku.class);
            }
        }
        List<ModelObject> goods = sessionTemplate.list(Criteria.query(TableGoods.class).in(TableGoods.serialNumber, serialNumbers));
        if (goods != null) {
            for (ModelObject g : goods) {
                g.setObjectClass(TableGoods.class);
            }
        }
        goods.addAll(skus);
        return goods;
    }

    @Override
    public List<ModelObject> getBaseGoodsByList(List<Long> nlist) {
        Query query = Criteria.query(TableGoods.class)
                .addSubjoin(Criteria.join(TableGoodsSku.class).join(TableGoodsSku.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsSku.class, TableGoodsProperty.class).join(TableGoodsProperty.skuId, TableGoodsSku.id))
                .addSubjoin(Criteria.join(TableGoodsGroup.class).join(TableGoodsGroup.id, TableGoods.gid).single())
                .addSubjoin(Criteria.join(TableGoodsBrand.class).join(TableGoodsBrand.id, TableGoods.bid).single())
                .addSubjoin(Criteria.join(TableLogisticsTemplate.class).join(TableLogisticsTemplate.id, TableGoods.pwid).single())
                .eq(TableGoods.isDelete, DELETE_NO)
                .in(TableGoods.id, nlist);


        List<ModelObject> goods = sessionTemplate.list(query);
        if (goods != null) {
            Map<ModelObject, List<ModelObject>> discounts = GlobalService.discountService.getUserDiscountByGoods(null, goods, PlatformType.ALL, false);
            for (ModelObject g : goods) {
                if (discounts != null) {
                    g.put(TableDiscount.class.getSimpleName(), discounts.get(g));
                }
            }
        }

        return goods;
    }

    @Override
    public ModelObject getViewGoodsById(long id) {
        Query query = sessionTemplate.query(TableGoods.class)
                .addSubjoin(Criteria.join(TableGoodsSku.class).join(TableGoodsSku.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsProperty.class).join(TableGoodsProperty.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsImage.class).join(TableGoodsImage.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsDescribe.class).join(TableGoodsDescribe.goodsId, TableGoods.id).single())
                .addSubjoin(Criteria.join(TableGoodsGroup.class).join(TableGoodsGroup.id, TableGoods.gid).single())
                .addSubjoin(Criteria.join(TableGoodsBrand.class).join(TableGoodsBrand.id, TableGoods.bid).single())
                .in(TableGoods.status, GoodsStatus.UP.getCode(), GoodsStatus.DOWN.getCode())
                .eq(TableGoods.id, id);


        ModelObject goods = query.query();
        if (goods != null) {

            /**
             * 重新组织结构，包括属性，颜色属性
             * sku对应的属性id，方便前端展示
             */
            List<ModelObject> skus = goods.getArray(TableGoodsSku.class.getSimpleName());
            List<ModelObject> properties = goods.getArray(TableGoodsProperty.class.getSimpleName());
            List<ModelObject> images = goods.getArray(TableGoodsImage.class.getSimpleName());

            Map<Long, ModelObject> skups = new LinkedHashMap<>();
            List<ModelObject> attrs = new ArrayList<>();
            List<ModelObject> skuAttrs = new ArrayList<>();

            if (skus != null) {
                if (properties != null) {
                    for (ModelObject p : properties) {
                        long attrId = p.getLongValue(TableGoodsProperty.attrId);
                        String attrName = p.getString(TableGoodsProperty.attrName);
                        long valueId = p.getLongValue(TableGoodsProperty.valueId);
                        String valueName = p.getString(TableGoodsProperty.valueName);
                        long skuId = p.getLongValue(TableGoodsProperty.skuId);
                        ModelObject attr = skups.get(attrId);
                        if (attr == null) {
                            attr = new ModelObject();
                            attr.put(TableGoodsProperty.attrId, attrId);
                            attr.put(TableGoodsProperty.attrName, attrName);
                        }
                        if (skuId != 0) {
                            attr.put(TableGoodsProperty.skuId, skuId);
                        }

                        List<ModelObject> values = attr.getArray("values");
                        if (values == null) {
                            values = new ArrayList<>();
                            attr.put("values", values);
                        }

                        boolean hasValue = false;
                        for (ModelObject value : values) {
                            if (value.getLongValue(TableGoodsProperty.valueId) == valueId) hasValue = true;
                        }
                        if (!hasValue) {
                            ModelObject value = new ModelObject();
                            value.put(TableGoodsProperty.valueId, valueId);
                            value.put(TableGoodsProperty.valueName, valueName);

                            if (images != null) {
                                for (ModelObject img : images) {
                                    long imgAttrId = img.getLongValue(TableGoodsImage.attrId);
                                    long imgValueId = img.getLongValue(TableGoodsImage.valueId);
                                    if (attrId == imgAttrId && valueId == imgValueId) {
                                        value.put("img", img.getString(TableGoodsImage.fileName));
                                        attr.put("color", true);
                                    }
                                }
                            }
                            values.add(value);
                            attr.put("values", values);
                        }

                        skups.put(attrId, attr);
                    }
                }

                Iterator<Map.Entry<Long, ModelObject>> iterator = skups.entrySet().iterator();
                while (iterator.hasNext()) {
                    attrs.add(iterator.next().getValue());
                }
                Collections.reverse(attrs);

                for (ModelObject attr : attrs) {
                    if (attr.getBooleanValue("color")) {
                        skuAttrs.add(attr);
                    }
                }
                for (ModelObject attr : attrs) {
                    if (!attr.getBooleanValue("color")
                            && attr.isNotEmpty(TableGoodsProperty.skuId)
                            && attr.getLongValue(TableGoodsProperty.skuId) != 0) {
                        skuAttrs.add(attr);
                    }
                }
                goods.put("skuAttrs", skuAttrs);

                for (ModelObject sku : skus) {
                    long skuId = sku.getLongValue(TableGoodsSku.id);

                    if (properties != null) {
                        Set<String> skuAttrIds = new LinkedHashSet<>();
                        for (ModelObject p : properties) {
                            long psku = p.getLongValue(TableGoodsProperty.skuId);
                            long valueId = p.getLongValue(TableGoodsProperty.valueId);
                            if (psku == skuId) {
                                skuAttrIds.add(String.valueOf(valueId));
                            }
                        }
                        sku.put("attrIds", String.join(",", skuAttrIds));
                    }
                }
            }

            /**
             * 开始重新组织商品属性
             */

            if (properties != null) {
                goods.put("attrs", attrs);
            }
        }

        return goods;
    }

    @Override
    public List<ModelObject> getOrderGoodsByCart(long uid, List cartIds) {
        if (cartIds != null && cartIds.size() > 0) {
            List<ModelObject> carts = GlobalService.shoppingCartService.getUserCarts(uid, cartIds);

            if (carts != null && carts.size() > 0) {
                Set<Long> gids = new LinkedHashSet<>();
                Set<Long> skus = new LinkedHashSet<>();
                Map<String, Integer> nums = new HashMap<>();
                Map<String, Long> discountMap = new HashMap<>();
                for (int i = 0; i < carts.size(); i++) {
                    ModelObject item = carts.get(i);
                    long gid = item.getLongValue(TableShoppingCart.gid);
                    long skuId = item.getLongValue(TableShoppingCart.skuId);
                    int amount = item.getIntValue(TableShoppingCart.amount);
                    long discountId = item.getLongValue(TableShoppingCart.discountId);
                    gids.add(gid);
                    if (skuId != 0) {
                        skus.add(skuId);
                    }
                    if (amount != 0) {
                        nums.put("G" + gid + "S" + skuId, amount);
                    } else {
                        nums.put("G" + gid + "S" + skuId, 1);
                    }
                    if (discountId > 0) {
                        discountMap.put("G" + gid + "S" + skuId, discountId);
                    }
                }

                List<ModelObject> goodsList = sessionTemplate.list(Criteria.query(TableGoods.class)
                        .eq(TableGoods.status, GoodsStatus.UP.getCode())
                        .eq(TableGoods.isDelete, 0)
                        .in(TableGoods.id, new ArrayList<>(gids)));

                List<ModelObject> skuList = null;
                if (skus != null && skus.size() > 0) {
                    skuList = sessionTemplate.list(Criteria.query(TableGoodsSku.class)
                            .subjoin(TableGoodsProperty.class)
                            .eq(TableGoodsProperty.goodsId, TableGoodsSku.goodsId)
                            .eq(TableGoodsProperty.skuId, TableGoodsSku.id)
                            .query()
                            .in(TableGoodsSku.id, new ArrayList<>(skus)));
                }

                List<ModelObject> orderGoods = new ArrayList<>();
                if (goodsList != null) {
                    for (ModelObject gd : goodsList) {
                        int hasSku = gd.getIntValue(TableGoods.hasSku);
                        if (hasSku == 1) {
                            if (skuList != null) {
                                for (ModelObject sku : skuList) {
                                    long gid = sku.getLongValue(TableGoodsSku.goodsId);
                                    if (gid == gd.getLongValue(TableGoods.id)) {
                                        ModelObject newGoods = (ModelObject) gd.clone();
                                        newGoods.put(TableGoodsSku.class.getSimpleName(), sku);
                                        newGoods.put("buyAmount",
                                                nums.get("G" + gd.getLongValue(TableGoods.id) + "S" + sku.getLongValue(TableGoodsSku.id)));
                                        newGoods.put(TableGoods.price, sku.get(TableGoodsSku.price));
                                        newGoods.put(TableGoods.vipPrice, sku.get(TableGoodsSku.vipPrice));
                                        newGoods.put(TableGoods.originalPrice, sku.get(TableGoodsSku.originalPrice));
                                        newGoods.put(TableGoods.costPrice, sku.get(TableGoodsSku.costPrice));
                                        newGoods.put(TableGoods.amount, sku.get(TableGoodsSku.amount));
                                        newGoods.put("skuId", sku.getLongValue(TableGoodsSku.id));

                                        Long discountId = discountMap.get("G" + gd.getLongValue(TableGoods.id) + "S" + sku.getLongValue(TableGoodsSku.id));
                                        if (discountId != null) {
                                            newGoods.put("discountId", discountId);
                                        }
                                        orderGoods.add(newGoods);
                                    }
                                }
                            }
                        } else {
                            gd.put("buyAmount", nums.get("G" + gd.getLongValue(TableGoods.id) + "S0"));
                            Long discountId = discountMap.get("G" + gd.getLongValue(TableGoods.id) + "S0");
                            if (discountId != null) {
                                gd.put("discountId", discountId);
                            }
                            orderGoods.add(gd);
                        }
                    }

                    if (skuList != null) {
                        for (ModelObject sku : skuList) {
                            List<ModelObject> property = sku.getArray(TableGoodsProperty.class.getSimpleName());
                            List<String> skuGoodsName = new ArrayList<>();
                            if (property != null) {
                                for (ModelObject pp : property) {
                                    skuGoodsName.add(pp.getString(TableGoodsProperty.valueName));
                                }
                            }
                            sku.put("propertyNames", skuGoodsName);
                            sku.put("propertyNamesString", String.join(",", skuGoodsName));
                        }
                    }
                }
                return orderGoods;
            }
        }

        return null;
    }

    @Override
    public List<ModelObject> getParameterByGroupId(long gid) {
        List<ModelObject> relList = sessionTemplate.list(Criteria.query(TableGoodsParameterRel.class).eq(TableGoodsParameterRel.groupId, gid));
        if (relList != null && relList.size() > 0) {
            List<Integer> relIds = new ArrayList<>();
            relList.forEach(rel -> relIds.add(rel.getInteger(TableGoodsParameterRel.parameterId)));
            if (relIds.size() > 0) {
                return sessionTemplate.list(Criteria.query(TableGoodsParameter.class).in(TableGoodsParameter.id, relIds)
                        .subjoin(TableGoodsValue.class).eq(TableGoodsValue.parameterId, TableGoodsParameter.id).query());
            }
        }
        return null;
    }

    @Override
    public long getGoodsCount(GoodsStatus... statuses) {
        List<Integer> ins = new ArrayList<>();
        for (GoodsStatus status : statuses) {
            ins.add(status.getCode());
        }
        return sessionTemplate.count(Criteria.query(TableGoods.class)
                .in(TableGoods.status, ins));
    }

    @Override
    public List<ModelObject> getRecommendGoods(int count) {
        long totalUpGoods = sessionTemplate.count(
                Criteria.query(TableGoods.class)
                        .eq(TableGoods.isDelete, DELETE_NO)
                        .eq(TableGoods.status, GoodsStatus.UP.getCode())
        );
        List<ModelObject> goods = new ArrayList<>();
        if (totalUpGoods > 0) {
            for (int i = 0; i < count; i++) {
                long index = RandomUtils.randomNumber(0, totalUpGoods - 1);

                List<ModelObject> g = sessionTemplate.list(Criteria.query(TableGoods.class)
                        .eq(TableGoods.isDelete, DELETE_NO)
                        .eq(TableGoods.status, GoodsStatus.UP.getCode())
                        .limit(index, 1));
                if (g != null && g.size() > 0) {
                    goods.add(g.get(0));
                }
            }
        }
        return goods;
    }

    @Override
    public Paging getGoodsInGroups(List<Integer> goodsGroupIdList, int start, int limit) {
        Query query = Criteria.query(TableGoods.class);
        query.addSubjoin(Criteria.join(TableGoodsSku.class).join(TableGoodsSku.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsProperty.class).join(TableGoodsProperty.goodsId, TableGoods.id))
                .addSubjoin(Criteria.join(TableGoodsGroup.class).join(TableGoodsGroup.id, TableGoods.gid).single())
                .addSubjoin(Criteria.join(TableGoodsBrand.class).join(TableGoodsBrand.id, TableGoods.bid).single());
        query.in(TableGoods.gid, goodsGroupIdList);
        query.limit(start, limit);
        return sessionTemplate.paging(query);
    }

    @Override
    public List<ModelObject> getGoodsWithSku(Map<Long, Long> goodsIdList, String skuValueName) {
        if (goodsIdList != null) {
            List<Long> goodsIds = new ArrayList<>();
            List<Long> goodsSkuIds = new ArrayList<>();
            Iterator<Map.Entry<Long, Long>> iterator = goodsIdList.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, Long> entry = iterator.next();
                goodsIds.add(entry.getKey());
                if (entry.getValue() != 0l) {
                    goodsSkuIds.add(entry.getValue());
                }
            }

            Query query = Criteria.query(TableGoods.class);
            query.addSubjoin(Criteria.join(TableGoodsSku.class).join(TableGoodsSku.goodsId, TableGoods.id))
                    .addSubjoin(Criteria.join(TableGoodsGroup.class).join(TableGoodsGroup.id, TableGoods.gid).single())
                    .addSubjoin(Criteria.join(TableGoodsBrand.class).join(TableGoodsBrand.id, TableGoods.bid).single());
            query.in(TableGoods.id, goodsIds);
            List<ModelObject> goods = sessionTemplate.list(query);

            if (goodsSkuIds != null && goodsSkuIds.size() > 0) {
                List<ModelObject> skus = sessionTemplate.list(Criteria.query(TableGoodsSku.class)
                        .subjoin(TableGoodsProperty.class)
                        .eq(TableGoodsProperty.goodsId, TableGoodsSku.goodsId)
                        .eq(TableGoodsProperty.skuId, TableGoodsSku.id)
                        .query()
                        .in(TableGoodsSku.id, goodsSkuIds));

                if (goods != null) {
                    for (ModelObject g : goods) {
                        if (skus != null) {
                            long goodsId = g.getLongValue(TableGoods.id);
                            long relSkuId = goodsIdList.get(goodsId);
                            for (ModelObject sku : skus) {
                                if (sku.getLongValue(TableGoodsSku.id) == relSkuId
                                        && sku.getLongValue(TableGoodsSku.goodsId) == goodsId) {
                                    List<ModelObject> property = sku.getArray(TableGoodsProperty.class);
                                    List<String> skuGoodsName = new ArrayList<>();
                                    if (property != null) {
                                        for (ModelObject pp : property) {
                                            if (pp.getLongValue(TableGoodsProperty.skuId) == sku.getLongValue(TableGoodsSku.id)) {
                                                skuGoodsName.add(pp.getString(TableGoodsProperty.attrName) + ";" + pp.getString(TableGoodsProperty.valueName));
                                            }
                                        }
                                    }
                                    sku.put("propertyNames", skuGoodsName);
                                    g.put(skuValueName, sku);
                                }
                            }
                        }
                    }
                }
            }

            return goods;
        }
        return null;
    }
}
