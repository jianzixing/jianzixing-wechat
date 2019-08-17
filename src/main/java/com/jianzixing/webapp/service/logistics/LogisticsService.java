package com.jianzixing.webapp.service.logistics;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.order.OrderGoodsModel;
import com.jianzixing.webapp.service.order.OrderModel;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.exception.TransactionException;

import java.util.List;

public interface LogisticsService {
    List<ModelObject> getSimpleTemplates(String keyword);

    ModelObject getTemplateById(long tid);

    /**
     * 计算订单运费
     *
     * @param orderModel
     * @param products
     * @return
     */
    LogisticsFreightModel calFreight(OrderModel orderModel, List<OrderGoodsModel> products);

    /**
     * 获取商品的配送列表
     *
     * @param goodsIds
     * @return
     */
    List<ModelObject> getGoodsLogistics(List<Long> goodsIds);

    List<ModelObject> getDeliveryTypes(List<ModelObject> products);

    List<ModelObject> getCompanies();

    ModelObject getCompanyById(int logisticsCompanyId);

    ModelObject getCompanyByCode(String code);

    void addCompany(ModelObject object) throws ModelCheckerException, ModuleException;

    void deleteCompany(int cid);

    void updateCompany(ModelObject object) throws ModelCheckerException, ModuleException;

    List<ModelObject> getCompany();

    void setDefaultCompany(int id);

    //运费模板相关的接口
    void saveTemplate(ModelObject object) throws ModuleException, ModelCheckerException, TransactionException;

    void deleteTemplate(int id);

    List<ModelObject> getTemplate();

    Paging getTemplates(ModelObject search, long start, long limit);
}
