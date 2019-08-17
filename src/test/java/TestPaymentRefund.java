import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.order.OrderPayStatus;
import com.jianzixing.webapp.service.order.OrderStatus;
import com.jianzixing.webapp.tables.order.TableOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.springmvc.exception.ModuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试退款机制
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring-context.xml"})
public class TestPaymentRefund {

    @Autowired
    SessionTemplate sessionTemplate;

    /**
     * 测试取消订单退款
     */
    @Test
    public void testCancelOrderRefund() throws ModuleException {
        long uid = 1;
        long addressId = 7;

        ModelObject order = new ModelObject();
        order.put("uid", uid);
        order.put("addressId", addressId);
        List<ModelObject> orderGoodsList = new ArrayList<>();
        ModelObject orderGoods = new ModelObject();
        orderGoods.put("pid", 2);
        orderGoods.put("skuId", 6);
        orderGoods.put("buyAmount", 1);
        orderGoodsList.add(orderGoods);
        order = GlobalService.orderService.addOrder(order, orderGoodsList);

        ModelObject updateOrder = new ModelObject();
        updateOrder.put(TableOrder.id, order.getLongValue(TableOrder.id));
        updateOrder.put(TableOrder.payStatus, OrderPayStatus.PAYED.getCode());
        updateOrder.put(TableOrder.status, OrderStatus.FINISH.getCode());
        sessionTemplate.update(updateOrder);


    }
}
