package com.jianzixing.webapp.service.order;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.criteria.Query;

import java.util.List;

public interface UserAddressService {
    void addUserAddress(ModelObject address) throws ModelCheckerException, ModuleException;

    void deleteUserAddress(long id);

    void updateUserAddress(ModelObject address) throws ModelCheckerException, ModuleException;

    Paging<ModelObject> getUserAddresses(ModelObject search, long start, long limit);

    List<ModelObject> getUserAddressByUid(long uid);

    ModelObject getUserAddressById(long addressId);

    ModelObject getUserAddressById(long uid, long aid);

    ModelObject saupAddress(ModelObject address) throws ModuleException, ModelCheckerException;

    ModelObject getDefaultAddress(long uid);

    void setAddressDefault(long uid, long aid);

    void deleteUserAddress(long uid, long addrid);

    long getUserAddressCount(long uid);
}
