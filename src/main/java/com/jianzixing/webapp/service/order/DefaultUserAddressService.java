package com.jianzixing.webapp.service.order;

import org.mimosaframework.springmvc.exception.ModuleException;
import org.mimosaframework.springmvc.exception.StockCode;
import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.area.AreaService;
import com.jianzixing.webapp.tables.area.TableArea;
import com.jianzixing.webapp.tables.area.TableCity;
import com.jianzixing.webapp.tables.area.TableProvince;
import com.jianzixing.webapp.tables.order.TableUserAddress;
import com.jianzixing.webapp.tables.user.TableUser;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.exception.ModelCheckerException;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.Paging;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.mimosaframework.orm.criteria.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DefaultUserAddressService implements UserAddressService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public void addUserAddress(ModelObject address) throws ModelCheckerException, ModuleException {
        setUserAddressBase(address);

        address.checkAndThrowable();
        String phone = address.getString(TableUserAddress.phoneNumber);
        String tel = address.getString(TableUserAddress.telNumber);
        if (StringUtils.isBlank(phone) && StringUtils.isBlank(tel)) {
            throw new ModuleException(StockCode.ARG_NULL, "手机号码必须填写");
        }
        address.put(TableUserAddress.createTime, new Date());
        sessionTemplate.save(address);
        int def = address.getIntValue(TableUserAddress.isDefault);
        if (def == 1) {
            this.setAddressDefault(
                    address.getLongValue(TableUserAddress.userId),
                    address.getLongValue(TableUserAddress.id));
        }
    }

    @Override
    public void deleteUserAddress(long id) {
        sessionTemplate.delete(TableUserAddress.class, id);
    }

    @Override
    public void updateUserAddress(ModelObject address) throws ModelCheckerException, ModuleException {
        setUserAddressBase(address);
        address.checkUpdateThrowable();
        address.remove(TableUserAddress.createTime);

        String phone = address.getString(TableUserAddress.phoneNumber);
        String tel = address.getString(TableUserAddress.telNumber);
        if (StringUtils.isBlank(phone) && StringUtils.isBlank(tel)) {
            throw new ModuleException(StockCode.ARG_NULL, "手机号码必须填写");
        }

        sessionTemplate.update(address);

        int def = address.getIntValue(TableUserAddress.isDefault);
        if (def == 1) {
            this.setAddressDefault(
                    address.getLongValue(TableUserAddress.userId),
                    address.getLongValue(TableUserAddress.id));
        }
    }

    private void setUserAddressBase(ModelObject address) throws ModuleException {
        address.setObjectClass(TableUserAddress.class);

        String country = address.getString(TableUserAddress.country);
        if (country == null) {
            address.put(TableUserAddress.countryCode, AreaService.CHINA_CODE);
            address.put(TableUserAddress.country, "中国");
        }
        int provinceCode = address.getIntValue(TableUserAddress.provinceCode);
        if (provinceCode <= 0) {
            throw new ModuleException(StockCode.ARG_NULL, "所属省不存在");
        }
        ModelObject province = GlobalService.areaService.getProvinceByCode(provinceCode);
        if (province == null) {
            throw new ModuleException(StockCode.ARG_NULL, "所属省不存在");
        }
        address.put(TableUserAddress.province, province.getString(TableProvince.name));
        int cityCode = address.getIntValue(TableUserAddress.cityCode);
        if (cityCode <= 0) {
            throw new ModuleException(StockCode.ARG_NULL, "所属市不存在");
        }
        ModelObject city = GlobalService.areaService.getCityByCode(cityCode);
        if (city == null) {
            throw new ModuleException(StockCode.ARG_NULL, "所属市不存在");
        }
        address.put(TableUserAddress.city, city.getString(TableCity.name));
        int countyCode = address.getIntValue(TableUserAddress.countyCode);
        if (countyCode <= 0) {
            throw new ModuleException(StockCode.ARG_NULL, "所属区县不存在");
        }
        ModelObject county = GlobalService.areaService.getAreaByCode(countyCode);
        if (county == null) {
            throw new ModuleException(StockCode.ARG_NULL, "所属区县不存在");
        }
        address.put(TableUserAddress.county, county.getString(TableArea.name));
    }

    @Override
    public Paging<ModelObject> getUserAddresses(ModelObject search, long start, long limit) {
        Query query = Criteria.query(TableUserAddress.class);
        query.subjoin(TableUser.class).eq(TableUser.id, TableUserAddress.userId).single();
        query.limit(start, limit);

        if (search != null) {
            if (search.isNotEmpty("userName")) {
                ModelObject user = GlobalService.userService.getUserByUserName(search.getString("userName"));
                if (user != null) {
                    query.eq(TableUserAddress.userId, user.getLongValue(TableUser.id));
                } else {
                    return null;
                }
            }
            if (search.isNotEmpty("realName")) {
                query.like(TableUserAddress.realName, "%" + search.getString("realName") + "%");
            }
            if (search.isNotEmpty("address")) {
                query.like(TableUserAddress.address, "%" + search.getString("address") + "%");
            }
            if (search.isNotEmpty("phone")) {
                query.eq(TableUserAddress.phoneNumber, "%" + search.getString("phone") + "%");
            }
        }

        return sessionTemplate.paging(query);
    }

    @Override
    public List<ModelObject> getUserAddressByUid(long uid) {
        return sessionTemplate.list(Criteria.query(TableUserAddress.class)
                .eq(TableUserAddress.userId, uid));
    }

    @Override
    public ModelObject getUserAddressById(long addressId) {
        return sessionTemplate.get(
                Criteria.query(TableUserAddress.class)
                        .eq(TableUserAddress.id, addressId)
        );
    }

    @Override
    public ModelObject getUserAddressById(long uid, long aid) {
        return sessionTemplate.get(
                Criteria.query(TableUserAddress.class)
                        .eq(TableUserAddress.id, aid)
                        .eq(TableUserAddress.userId, uid)
        );
    }

    @Override
    public ModelObject saupAddress(ModelObject address) throws ModuleException, ModelCheckerException {
        long id = address.getLongValue(TableUserAddress.id);
        if (id > 0) {
            ModelObject old = sessionTemplate.get(TableUserAddress.class, id);
            if (old != null) {
                this.updateUserAddress(address);
                return old;
            } else {
                throw new ModuleException(StockCode.NOT_EXIST, "用户地址不存在");
            }
        } else {
            address.setObjectClass(TableUserAddress.class);
            address.put(TableUserAddress.createTime, new Date());

            this.addUserAddress(address);
        }
        return address;
    }

    @Override
    public ModelObject getDefaultAddress(long uid) {
        ModelObject address = sessionTemplate.get(
                Criteria.query(TableUserAddress.class)
                        .eq(TableUserAddress.userId, uid)
                        .eq(TableUserAddress.isDefault, 1)
        );
        if (address == null) {
            address = sessionTemplate.get(
                    Criteria.query(TableUserAddress.class)
                            .eq(TableUserAddress.userId, uid)
                            .eq(TableUserAddress.isDefault, 0)
                            .limit(0, 1)
            );
        }
        return address;
    }

    @Override
    public void setAddressDefault(long uid, long aid) {
        sessionTemplate.update(
                Criteria.update(TableUserAddress.class)
                        .eq(TableUserAddress.userId, uid)
                        .value(TableUserAddress.isDefault, 0)
        );
        ModelObject object = new ModelObject(TableUserAddress.class);
        object.put(TableUserAddress.id, aid);
        object.put(TableUserAddress.isDefault, 1);
        sessionTemplate.update(object);
    }

    @Override
    public void deleteUserAddress(long uid, long addrid) {
        sessionTemplate.delete(
                Criteria.delete(TableUserAddress.class)
                        .eq(TableUserAddress.userId, uid)
                        .eq(TableUserAddress.id, addrid)
        );
    }

    @Override
    public long getUserAddressCount(long uid) {
        return sessionTemplate.count(Criteria.query(TableUserAddress.class)
                .eq(TableUserAddress.userId, uid));
    }
}
