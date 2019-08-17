package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

@APIController
public class AreaController {

    @Printer(name = "获取地区省份")
    public ResponseMessage getProvince() {
        return new ResponseMessage(GlobalService.areaService.getChinaProvince());
    }

    @Printer(name = "获取地区城市")
    public ResponseMessage getCity(int provinceCode) {
        return new ResponseMessage(GlobalService.areaService.getChinaCity(provinceCode));
    }

    @Printer(name = "获取地区市区")
    public ResponseMessage getArea(int cityCode) {
        return new ResponseMessage(GlobalService.areaService.getChinaArea(cityCode));
    }

    @Printer
    public ResponseMessage getClassifyArea() {
        return new ResponseMessage(GlobalService.areaService.getChinaByClassify());
    }
    
    @Printer
    public ResponseMessage getAreaByCodes(List<String> codes) {
        List<ModelObject> objects = GlobalService.areaService.getLineAreaByCode(codes);
        return new ResponseMessage(objects);
    }
}
