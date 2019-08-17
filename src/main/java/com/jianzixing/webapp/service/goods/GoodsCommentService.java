package com.jianzixing.webapp.service.goods;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;
import org.mimosaframework.orm.exception.TransactionException;

import java.util.List;

public interface GoodsCommentService {
    void addComment(ModelObject object) throws ModelCheckerException, ModuleException, TransactionException;

    void deleteComment(long id);

    void updateComment(ModelObject object) throws ModelCheckerException;

    Paging getComments(ModelObject search, long start, long limit);

    List<ModelObject> getTopPriorityCommentByGid(long id, int priorityCount, int elseCount);

    long getCommentCountByGid(long id);

    List<ModelObject> getCommentsByOrderGoods(long oid, List<Long> orderGoodsIds);

    /**
     * 获取带条件的商品评论
     *
     * @param page
     * @param type  0全部 1最新 2好评 3中评 4差评 5有图
     * @param gid
     * @param skuId
     */
    List<ModelObject> getComments(int page, int type, long gid, long skuId);

    /**
     * 获取好评数量
     *
     * @param id
     * @return
     */
    long getGoodCommentCount(long id);

    long getMiddleCommentCount(long id);

    long getBadCommentCount(long id);

    long getImageCommentCount(long id);
}
