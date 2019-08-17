package com.jianzixing.webapp.service.goods;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.goods.TableGoods;
import com.jianzixing.webapp.tables.goods.TableGoodsComment;
import com.jianzixing.webapp.tables.goods.TableGoodsCommentImage;
import com.jianzixing.webapp.tables.goods.TableGoodsImage;
import com.jianzixing.webapp.tables.order.TableOrderGoods;
import com.jianzixing.webapp.tables.user.TableUser;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class DefaultGoodsCommentService implements GoodsCommentService {
    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addComment(ModelObject object) throws ModelCheckerException, ModuleException, TransactionException {
        object.setObjectClass(TableGoodsComment.class);
        object.checkAndThrowable();
        long userId = object.getLongValue(TableGoodsComment.userId);
        long orderGoodsId = object.getLongValue(TableGoodsComment.orderGoodsId);

        ModelObject orderGoods = GlobalService.orderService.getOrderGoodsWithSku(orderGoodsId);
        long gid = orderGoods.getLongValue(TableOrderGoods.goodsId);
        long orderId = orderGoods.getLongValue(TableOrderGoods.orderId);
        object.put(TableGoodsComment.goodsSku, orderGoods.getString("goodsSkuName"));
        object.put(TableGoodsComment.orderId, orderId);

        ModelObject user = GlobalService.userService.getUser(userId);
        ModelObject goods = GlobalService.goodsService.getSimpleGoodsById(gid);
        if (user == null) {
            throw new ModuleException(StockCode.ARG_NULL, "评论用户不存在或被加黑");
        }
        if (goods == null) {
            throw new ModuleException(StockCode.ARG_NULL, "评论商品已下架或者不存在");
        }

        ModelObject o = sessionTemplate.get(Criteria.query(TableGoodsComment.class)
                .eq(TableGoodsComment.userId, userId)
                .eq(TableGoodsComment.orderId, orderId)
                .eq(TableGoodsComment.orderGoodsId, orderGoodsId)
        );

        object.put(TableGoodsComment.goodsId, gid);
        object.put(TableGoodsComment.skuId, orderGoods.getLongValue(TableOrderGoods.skuId));

        if (o != null) {
            throw new ModuleException(StockCode.EXIST_OBJ, "改用户已经评价过当前商品");
        }
        object.put(TableGoodsComment.userName, user.getString(TableUser.userName));
        object.put(TableGoodsComment.openid, user.getString(TableUser.openid));
        object.put(TableGoodsComment.serialNumber, user.getString(TableGoods.serialNumber));
        object.put(TableGoodsComment.createTime, new Date());


        List<String> images = object.getArray("images");
        List<ModelObject> commentImages = new ArrayList<>();
        if (images != null && images.size() > 0) {
            int index = 0;
            for (String image : images) {
                ModelObject commentImage = new ModelObject(TableGoodsCommentImage.class);
                commentImage.put(TableGoodsCommentImage.index, ++index);
                commentImage.put(TableGoodsCommentImage.fileName, image);
                commentImages.add(commentImage);
            }
            object.put(TableGoodsComment.hasImg, 1);
        }

        sessionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean invoke(Transaction transaction) throws Exception {
                sessionTemplate.save(object);
                if (commentImages != null && commentImages.size() > 0) {
                    for (ModelObject commentImage : commentImages) {
                        commentImage.put(TableGoodsCommentImage.cmtId, object.getLongValue(TableGoodsComment.id));
                    }
                    sessionTemplate.save(commentImages);
                }
                GlobalService.orderService.setOrderComment(userId, orderId);
                return true;
            }
        });
    }

    @Override
    public void deleteComment(long id) {
        sessionTemplate.delete(TableGoodsComment.class, id);
    }

    @Override
    public void updateComment(ModelObject object) throws ModelCheckerException {
        object.setObjectClass(TableGoodsComment.class);
        object.remove(TableGoodsComment.userId);
        object.remove(TableGoodsComment.goodsId);
        object.remove(TableGoodsComment.createTime);
        object.checkUpdateThrowable();
        sessionTemplate.update(object);
    }

    @Override
    public Paging getComments(ModelObject search, long start, long limit) {
        Query query = Criteria.query(TableGoodsComment.class);
        query.limit(start, limit);
        query.order(TableGoodsComment.id, false);
        query.subjoin(TableGoods.class).eq(TableGoods.id, TableGoodsComment.goodsId).single();
        query.subjoin(TableUser.class).eq(TableUser.id, TableGoodsComment.userId).single();
        if (search != null) {
            search.clearEmpty();
            if (search.isNotEmpty("comment"))
                query.like(TableGoodsComment.comment, "%" + search.getString("comment") + "%");
            if (search.isNotEmpty("userName"))
                query.like(TableGoodsComment.userName, search.getString("userName"));
            if (search.isNotEmpty("openid"))
                query.eq(TableGoodsComment.openid, search.getString("openid"));
            if (search.isNotEmpty("gid"))
                query.eq(TableGoodsComment.goodsId, search.getString("gid"));
            if (search.isNotEmpty("serialNumber"))
                query.eq(TableGoodsComment.serialNumber, search.getString("serialNumber"));
            if (search.isNotEmpty("createTimeStart"))
                query.gte(TableGoodsComment.createTime, search.getString("createTimeStart"));
            if (search.isNotEmpty("createTimeEnd"))
                query.lte(TableGoodsComment.createTime, search.getString("createTimeEnd"));
        }
        return sessionTemplate.paging(query);
    }

    @Override
    public List<ModelObject> getTopPriorityCommentByGid(long id, int priorityCount, int elseCount) {
        List<ModelObject> imageComments = sessionTemplate.list(
                Criteria.query(TableGoodsComment.class)
                        .subjoin(TableGoodsCommentImage.class).eq(TableGoodsCommentImage.cmtId, TableGoodsComment.id).query()
                        .subjoin(TableUser.class).eq(TableUser.id, TableGoodsComment.userId).single().query()
                        .eq(TableGoodsComment.goodsId, id)
                        .eq(TableGoodsComment.hasImg, 1)
                        .order(TableGoodsComment.id, false)
                        .limit(0, priorityCount)
        );
        if (imageComments != null) {
            if (priorityCount == imageComments.size()) elseCount = 0;
            else {
                elseCount = elseCount - imageComments.size() * 2;
            }
        }

        if (elseCount != 0) {
            List<ModelObject> comments = sessionTemplate.list(
                    Criteria.query(TableGoodsComment.class)
                            .subjoin(TableGoodsCommentImage.class).eq(TableGoodsCommentImage.cmtId, TableGoodsComment.id).query()
                            .subjoin(TableUser.class).eq(TableUser.id, TableGoodsComment.userId).single().query()
                            .eq(TableGoodsComment.goodsId, id)
                            .eq(TableGoodsComment.hasImg, 0)
                            .order(TableGoodsComment.id, false)
                            .limit(0, elseCount)
            );
            if (imageComments == null) imageComments = new ArrayList<>();
            if (comments != null) imageComments.addAll(comments);
        }

        if (imageComments != null) {
            for (ModelObject comment : imageComments) {
                this.setCommentSkuName(comment);
            }
        }
        return imageComments;
    }

    private void setCommentSkuName(ModelObject comment) {
        String goodsSKu = comment.getString(TableGoodsComment.goodsSku);
        if (StringUtils.isNotBlank(goodsSKu)) {
            String[] s1 = goodsSKu.split(";");
            List<ModelObject> skus = new ArrayList<>();
            for (String s2 : s1) {
                String[] s3 = s2.split(":");
                if (s3.length > 1) {
                    ModelObject skuName = new ModelObject();
                    skuName.put("key", s3[0]);
                    skuName.put("value", s3[1]);
                    skus.add(skuName);
                }
            }
            comment.put(TableGoodsComment.goodsSku, skus);
        }
    }

    @Override
    public long getCommentCountByGid(long id) {
        return sessionTemplate.count(
                Criteria.query(TableGoodsComment.class)
                        .eq(TableGoodsComment.goodsId, id)
        );
    }

    @Override
    public List<ModelObject> getCommentsByOrderGoods(long oid, List<Long> orderGoodsIds) {
        return sessionTemplate.list(Criteria.query(TableGoodsComment.class)
                .eq(TableGoodsComment.orderId, oid)
                .in(TableGoodsComment.orderGoodsId, orderGoodsIds));
    }

    @Override
    public List<ModelObject> getComments(int page, int type, long gid, long skuId) {
        int limit = 20;
        long start = (page - 1) * limit;

        Query query = Criteria.query(TableGoodsComment.class);
        query.subjoin(TableGoodsCommentImage.class).eq(TableGoodsCommentImage.cmtId, TableGoodsComment.id).aliasName("images");
        query.subjoin(TableUser.class).eq(TableUser.id, TableGoodsComment.userId).single();
        if (gid > 0) {
            query.eq(TableGoodsComment.goodsId, gid);
        }
        if (skuId > 0) {
            query.eq(TableGoodsComment.skuId, skuId);
        }
        if (type == 1) {
            query.eq(TableGoodsComment.score, 5);
            query.eq(TableGoodsComment.hasImg, 1);
        }
        if (type == 2) {
            query.eq(TableGoodsComment.score, 5);
        }
        if (type == 3) {
            query.gte(TableGoodsComment.score, 2);
            query.lte(TableGoodsComment.score, 3);
        }
        if (type == 4) {
            query.eq(TableGoodsComment.score, 1);
        }
        if (type == 5) {
            query.eq(TableGoodsComment.hasImg, 1);
        }
        query.order(TableGoodsComment.createTime, false);
        query.limit(start, limit);
        List<ModelObject> comments = sessionTemplate.list(query);
        if (comments != null) {
            for (ModelObject comment : comments) {
                int anonymity = comment.getIntValue(TableGoodsComment.anonymity);
                if (anonymity == 1) {
                    comment.remove(TableUser.class);
                }

                this.setCommentSkuName(comment);
            }
        }
        return comments;
    }

    @Override
    public long getGoodCommentCount(long id) {
        return sessionTemplate.count(Criteria.query(TableGoodsComment.class)
                .eq(TableGoodsComment.goodsId, id)
                .eq(TableGoodsComment.score, 5));
    }

    @Override
    public long getMiddleCommentCount(long id) {
        return sessionTemplate.count(Criteria.query(TableGoodsComment.class)
                .eq(TableGoodsComment.goodsId, id)
                .gte(TableGoodsComment.score, 2)
                .lte(TableGoodsComment.score, 3));
    }

    @Override
    public long getBadCommentCount(long id) {
        return sessionTemplate.count(Criteria.query(TableGoodsComment.class)
                .eq(TableGoodsComment.goodsId, id)
                .eq(TableGoodsComment.score, 1));
    }

    @Override
    public long getImageCommentCount(long id) {
        return sessionTemplate.count(Criteria.query(TableGoodsComment.class)
                .eq(TableGoodsComment.goodsId, id)
                .eq(TableGoodsComment.hasImg, 1));
    }
}
