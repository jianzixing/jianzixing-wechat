package com.jianzixing.webapp.service.order;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.json.ModelObject;

public interface OrderCheckstandInterface {
    OrderModel checkin(OrderModel orderModel) throws ModuleException;

    void checkout(ModelObject order);
}
