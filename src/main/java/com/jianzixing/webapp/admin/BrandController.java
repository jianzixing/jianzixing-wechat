package com.jianzixing.webapp.admin;

import org.mimosaframework.springmvc.exception.ModuleException;
import com.jianzixing.webapp.service.GlobalService;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;

import java.util.List;

/**
 * @author yangankang
 */
@APIController
public class BrandController {

    @Printer
    public ResponsePageMessage getBrands(ModelObject search, String keyword, int start, int limit) {
        if (StringUtils.isNotBlank(keyword)) search.put("name", keyword);
        return new ResponsePageMessage(GlobalService.goodsBrandService.getBrands(search, start, limit));
    }

    @Printer
    public ResponseMessage addBrand(ModelObject object) {
        try {
            GlobalService.goodsBrandService.add(object);
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteBrand(List<Integer> ids) {
        try {
            GlobalService.goodsBrandService.delete(ids);
        } catch (ModuleException e) {
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateBrand(ModelObject object) {
        try {
            GlobalService.goodsBrandService.update(object);
        } catch (ModuleException e) {
            e.printStackTrace();
            return new ResponseMessage(e);
        }
        return new ResponseMessage();
    }
}
