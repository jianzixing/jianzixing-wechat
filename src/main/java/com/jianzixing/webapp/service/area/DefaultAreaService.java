package com.jianzixing.webapp.service.area;

import com.jianzixing.webapp.tables.area.TableArea;
import com.jianzixing.webapp.tables.area.TableCity;
import com.jianzixing.webapp.tables.area.TableProvince;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.orm.SessionTemplate;
import org.mimosaframework.orm.criteria.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * author qinmingtao
 * desc
 */
@Service
public class DefaultAreaService implements AreaService {

    @Autowired
    SessionTemplate sessionTemplate;

    @Override
    public List<ModelObject> getChinaProvince() {
        return sessionTemplate.list(Criteria.query(TableProvince.class).eq(TableProvince.country, CHINA_CODE));
    }

    @Override
    public List<ModelObject> getChinaCity(int provinceCode) {
        return sessionTemplate.list(Criteria.query(TableCity.class).eq(TableCity.provinceCode, provinceCode));
    }

    @Override
    public List<ModelObject> getChinaArea(int cityCode) {
        return sessionTemplate.list(Criteria.query(TableArea.class).eq(TableArea.cityCode, cityCode));
    }

    @Override
    public List<ModelObject> getChinaByClassify() {
        List<ModelObject> provinces = sessionTemplate.list(
                Criteria.query(TableProvince.class)
                        .eq(TableProvince.country, CHINA_CODE)
                        .subjoin(TableCity.class).eq(TableCity.provinceCode, TableProvince.code).aliasName("citys").query()
        );
        if (provinces != null) {
            List<ModelObject> areas = new ArrayList<>();
            AreaClassify[] classifies = AreaClassify.values();
            Map<Integer, List<ModelObject>> map = new LinkedHashMap<>();
            for (ModelObject p : provinces) {
                List list = map.get(p.getIntValue(TableProvince.type));
                if (list == null) list = new ArrayList();
                list.add(p);
                map.put(p.getIntValue(TableProvince.type), list);
            }
            for (AreaClassify ac : classifies) {
                ModelObject object = new ModelObject();
                object.put("code", ac.getCode());
                object.put("name", ac.getMsg());
                object.put("provinces", map.get(ac.getCode()));
                areas.add(object);
            }
            return areas;
        }
        return null;
    }

    @Override
    public List<ModelObject> getLineAreaByCode(List<String> codes) {
        if (codes != null && codes.size() > 0) {
            List<ModelObject> citys = sessionTemplate.list(Criteria.query(TableCity.class).in(TableCity.code, codes));
            if (citys != null) {
                List<Integer> provinceCodes = new ArrayList<>();
                for (ModelObject object : citys) {
                    int provinceCode = object.getIntValue(TableCity.provinceCode);
                    provinceCodes.add(provinceCode);
                }

                List<ModelObject> privinces = sessionTemplate.list(Criteria.query(TableProvince.class).in(TableProvince.code, provinceCodes));
                if (privinces != null) {
                    List<ModelObject> r = new ArrayList<>();
                    for (ModelObject p : privinces) {
                        int provinceCode = p.getIntValue(TableProvince.code);
                        String provinceName = p.getString(TableProvince.name);
                        for (ModelObject c : citys) {
                            int cityCode = c.getIntValue(TableCity.code);
                            int cityProvinceCode = c.getIntValue(TableCity.provinceCode);
                            String cityName = c.getString(TableCity.name);

                            if (cityProvinceCode == provinceCode) {
                                ModelObject item = new ModelObject();
                                item.put("codes", provinceCode + "," + cityCode);
                                item.put("countryCode", AreaService.CHINA_CODE);
                                item.put("provinceCode", provinceCode);
                                item.put("cityCode", cityCode);
                                item.put("name", provinceName + "," + cityName);
                                item.put("countryName", AreaService.CHINA_NAME);
                                item.put("provinceName", provinceName);
                                item.put("cityName", cityName);
                                r.add(item);
                            }
                        }
                    }
                    return r;
                }
            }
        }
        return null;
    }

    @Override
    public ModelObject getProvinceByCode(int provinceCode) {
        return sessionTemplate.get(TableProvince.class, provinceCode);
    }

    @Override
    public ModelObject getCityByCode(int cityCode) {
        return sessionTemplate.get(TableCity.class, cityCode);
    }

    @Override
    public ModelObject getAreaByCode(int areaCode) {
        return sessionTemplate.get(TableArea.class, areaCode);
    }
}
