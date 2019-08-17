package com.jianzixing.webapp.service.discount;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.order.OrderDiscountModelWrapper;
import org.mimosaframework.core.json.ModelObject;

public interface DiscountCalculateInterface {
    /**
     * 获得当前优惠的界面名称
     *
     * @return
     */
    String getView();

    /**
     * 执行计算优惠活动价格
     * 如果优惠减免成功则返回true
     *
     * @param wrapper
     * @return
     */
    boolean calculate(OrderDiscountModelWrapper wrapper);

    String getName();

    void checkSaveParams(ModelObject object) throws ModuleException;
}
