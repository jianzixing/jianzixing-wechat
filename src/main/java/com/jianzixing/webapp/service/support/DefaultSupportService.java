package com.jianzixing.webapp.service.support;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.goods.TableGoodsGroupSupport;
import com.jianzixing.webapp.tables.goods.TableGoodsSupport;
import com.jianzixing.webapp.tables.order.TableOrder;
import com.jianzixing.webapp.tables.support.TableSupport;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DefaultSupportService implements SupportService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addSupport(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableSupport.class);
        object.put(TableSupport.createTime, new Date());
        object.checkAndThrowable();

        object.put(TableSupport.fixed, 0);
        sessionTemplate.save(object);
    }

    @Override
    public void delSupport(int id) {
        sessionTemplate.delete(TableSupport.class, id);
        sessionTemplate.delete(Criteria.delete(TableGoodsGroupSupport.class)
                .eq(TableGoodsGroupSupport.supportId, id));
        sessionTemplate.delete(Criteria.delete(TableGoodsSupport.class)
                .eq(TableGoodsSupport.supportId, id));
    }

    @Override
    public void updateSupport(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableSupport.class);
        object.retain(TableSupport.id,
                TableSupport.name,
                TableSupport.serTime,
                TableSupport.price,
                TableSupport.detail);
        object.checkUpdateThrowable();

        int id = object.getIntValue(TableSupport.id);
        ModelObject old = sessionTemplate.get(TableSupport.class, id);
        if (old != null && object.isNotEmpty(TableSupport.name)
                && !old.getString(TableSupport.name).equals(object.getString(TableSupport.name))) {
            sessionTemplate.update(Criteria.update(TableGoodsGroupSupport.class)
                    .eq(TableGoodsGroupSupport.supportId, id)
                    .value(TableGoodsGroupSupport.supportName, object.getString(TableSupport.name)));

            sessionTemplate.update(Criteria.update(TableGoodsSupport.class)
                    .eq(TableGoodsSupport.supportId, id)
                    .value(TableGoodsSupport.supportName, object.getString(TableSupport.name)));
        }

        sessionTemplate.update(object);
    }

    @Override
    public List<ModelObject> getSupports() {
        return sessionTemplate.list(Criteria.query(TableSupport.class).order(TableSupport.id, false));
    }

    @Override
    public List<ModelObject> getSupportByArray(List<String> ids) {
        return sessionTemplate.list(Criteria.query(TableSupport.class)
                .in(TableSupport.id, ids));
    }

    @Override
    public List<ModelObject> getSupportsByGroup(long groupId) {
        return sessionTemplate.list(Criteria.query(TableGoodsGroupSupport.class)
                .eq(TableGoodsGroupSupport.groupId, groupId));
    }

    @Override
    public List<ModelObject> getSupportsByGoods(long goodsId) {
        return sessionTemplate.list(Criteria.query(TableGoodsSupport.class)
                .eq(TableGoodsSupport.goodsId, goodsId));
    }

    @Override
    public ModelObject getAfterSalesByOrder(ModelObject order, long goodsId) {
        ModelObject object = new ModelObject();
        object.put(SupportServiceType.SALES_RETURN.name(), 0);
        object.put(SupportServiceType.EXCHANGE_GOODS.name(), 0);
        object.put(SupportServiceType.MAINTAIN.name(), 0);
        if (order != null) {
            Date payTime = order.getDate(TableOrder.payTime);
            List<ModelObject> supports = sessionTemplate.list(Criteria.query(TableGoodsSupport.class)
                    .subjoin(TableSupport.class).eq(TableSupport.id, TableGoodsSupport.supportId).single().query()
                    .eq(TableGoodsSupport.goodsId, goodsId));

            if (supports != null && payTime != null) {
                for (ModelObject support : supports) {
                    support = support.getModelObject(TableSupport.class);
                    if (support != null) {
                        int type = support.getIntValue(TableSupport.type);
                        int serTime = support.getIntValue(TableSupport.serTime);
                        if ((payTime.getTime() + serTime * 60 * 60 * 1000l) > System.currentTimeMillis()) {
                            if (type == SupportServiceType.SALES_RETURN.getCode()) {
                                object.put(SupportServiceType.SALES_RETURN.name(), 1);
                            }
                            if (type == SupportServiceType.EXCHANGE_GOODS.getCode()) {
                                object.put(SupportServiceType.EXCHANGE_GOODS.name(), 1);
                            }
                            if (type == SupportServiceType.MAINTAIN.getCode()) {
                                object.put(SupportServiceType.MAINTAIN.name(), 1);
                            }
                        }
                    }
                }
            }
        }
        return object;
    }
}
