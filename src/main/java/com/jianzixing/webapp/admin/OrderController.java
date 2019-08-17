package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.order.OrderStatus;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.exception.TransactionException;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@APIController
public class OrderController {

    @Printer
    public ResponsePageMessage getOrders(ModelObject search,
                                         List<Integer> status,
                                         long start, long limit) {
        Paging paging = GlobalService.orderService.getOrders(search, status, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage getOrder(long id) {
        ModelObject order = GlobalService.orderService.getOrderById(id);
        return new ResponseMessage(order);
    }

    @Printer
    public ResponseMessage sendOutOrder(long oid, String cid, String number) {
        try {
            GlobalService.orderService.sendOutOrder(oid, cid, number);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateOrderDelivery(long oid, String cid, String number) {
        try {
            GlobalService.orderService.updateOrderLogistics(oid, cid, number);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage setOrderFinish(long oid) {
        if (oid > 0) {
            GlobalService.orderService.updateOrderStatus(oid, OrderStatus.FINISH.getCode());
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage addOrder(ModelObject order, List<ModelObject> products) {
        if (order != null && products != null) {
            try {
                GlobalService.orderService.addOrder(order, products);
            } catch (Exception e) {
                return new ResponseMessage(e);
            }
        } else {
            return new ResponseMessage(-100, "订单信息和商品信息必须填写");
        }
        return new ResponseMessage();
    }

    @Printer
    private ResponseMessage getOrderPrices(ModelObject order, List<ModelObject> products) {
        if (order != null && products != null) {
            try {
                ModelObject prices = GlobalService.orderService.getOrderPrices(order, products);
                return new ResponseMessage(prices);
            } catch (Exception e) {
                return new ResponseMessage(e);
            }
        } else {
            return new ResponseMessage(-100, "订单信息和商品信息必须填写");
        }
    }

    @Printer
    public ResponseMessage deleteOrders(List<Long> ids) {
        try {
            if (ids != null) {
                for (long id : ids) {
                    GlobalService.orderService.deleteOrder(id);
                }
            }
        } catch (TransactionException e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage setOrderDelivery(List<Long> ids) {
        if (ids != null) {
            for (long id : ids) {
                try {
                    GlobalService.orderService.setOrderDelivery(id);
                } catch (Exception e) {
                    return new ResponseMessage(e);
                }
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage setOrderCancel(List<Long> ids) {
        if (ids != null) {
            for (long id : ids) {
                try {
                    GlobalService.orderService.cancelOrder(id);
                } catch (Exception e) {
                    return new ResponseMessage(e);
                }
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateOrderPrice(long id, String price) {
        try {
            GlobalService.orderService.updateOrderPrice(id, price);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateOrderAddress(ModelObject object) {
        try {
            GlobalService.orderService.updateOrderAddress(object);
        } catch (Exception e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getOrderPlatforms() {
        List<ModelObject> platforms = GlobalService.discountService.getPlatforms();
        return new ResponseMessage(platforms);
    }
}
