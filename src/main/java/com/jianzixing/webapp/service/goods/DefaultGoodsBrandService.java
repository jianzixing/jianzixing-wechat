package com.jianzixing.webapp.service.goods;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.BasicService;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.goods.TableGoodsBrand;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yangankang
 */
@Service
public class DefaultGoodsBrandService extends BasicService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public SessionTemplate getSessionTemplate() {
        return sessionTemplate;
    }

    @Override
    public Class getTable() {
        return TableGoodsBrand.class;
    }

    @Override
    public Object getIdName() {
        return TableGoodsBrand.id;
    }

    @Override
    public void delete(int id) throws ModuleException {
        boolean has = GlobalService.goodsService.isUsedBid(id);
        if (has) {
            throw new ModuleException(StockCode.USING, "品牌已经被商品使用不允许删除");
        }
        super.delete(id);
    }

    @Override
    public void delete(List ids) throws ModuleException {
        if (ids != null) {
            for (Object id : ids) {
                boolean has = GlobalService.goodsService.isUsedBid(Integer.parseInt("" + id));
                if (has) {
                    throw new ModuleException(StockCode.USING, "品牌已经被商品使用不允许删除");
                }
            }
        }
        super.delete(ids);
    }

    public Paging getBrands(ModelObject search, int start, int limit) {
        Query query = Criteria.query(TableGoodsBrand.class);
        if (search != null && search.isNotEmpty(TableGoodsBrand.name)) {
            query.like(TableGoodsBrand.name, '%' + search.getString(TableGoodsBrand.name) + '%');
        }
        query.limit(start, limit);
        query.order(TableGoodsBrand.id, false);
        return sessionTemplate.paging(query);
    }

    public List<ModelObject> getBrands(List<Integer> ids) {
        return sessionTemplate.list(Criteria.query(TableGoodsBrand.class).in(TableGoodsBrand.id, ids));
    }
}
