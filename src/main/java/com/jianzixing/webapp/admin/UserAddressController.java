package com.jianzixing.webapp.admin;

import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.tables.order.TableUserAddress;
import org.mimosaframework.springmvc.utils.ResponseMessage;
import org.mimosaframework.springmvc.utils.ResponsePageMessage;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.springmvc.APIController;
import org.mimosaframework.springmvc.Printer;
import org.mimosaframework.springmvc.SearchForm;

import java.util.List;

@APIController
public class UserAddressController {

    @Printer
    public ResponsePageMessage getUserAddress(ModelObject search, long start, long limit) {
        Paging paging = GlobalService.userAddressService.getUserAddresses(search, start, limit);
        return new ResponsePageMessage(paging);
    }

    @Printer
    public ResponseMessage addUserAddress(ModelObject object) {
        if (object != null) {
            try {
                GlobalService.userAddressService.addUserAddress(object);
            } catch (Exception e) {
                return new ResponseMessage(e);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage deleteUserAddress(List<Long> ids) {
        if (ids != null) {
            for (long id : ids) {
                GlobalService.userAddressService.deleteUserAddress(id);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage updateUserAddress(ModelObject object) {
        if (object != null) {
            try {
                GlobalService.userAddressService.updateUserAddress(object);
            } catch (Exception e) {
                return new ResponseMessage(e);
            }
        }
        return new ResponseMessage();
    }

    @Printer
    public ResponseMessage getUserAddressByUserUid(long uid) {
        List<ModelObject> address = GlobalService.userAddressService.getUserAddressByUid(uid);
        if (address != null) {
            for (ModelObject object : address) {
                object.put("selectName",
                        object.getString(TableUserAddress.realName) + "  " +
                                object.getString(TableUserAddress.country) +
                                object.getString(TableUserAddress.province) +
                                object.getString(TableUserAddress.city) +
                                object.getString(TableUserAddress.county) +
                                object.getString(TableUserAddress.address) + "  " +
                                object.getString(TableUserAddress.phoneNumber));
            }
        }
        return new ResponseMessage(address);
    }
}
