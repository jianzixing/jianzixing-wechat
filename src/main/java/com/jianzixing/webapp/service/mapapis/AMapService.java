package com.jianzixing.webapp.service.mapapis;

import com.jianzixing.webapp.tables.mapapis.TableMapConfig;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.HttpUtils;
import org.mimosaframework.orm.SessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AMapService implements MapService {

    private static final String IP_LOCATION_URL = "http://restapi.amap.com/v3/ip";
    private static final String PLACE_INFO_URL = "http://restapi.amap.com/v3/config/district";
    private static final String table_prefix = "amap";
    private static volatile Map<String, String> config = new ConcurrentHashMap<>();
    private static volatile long lastUpdateTime = 0;

    @Autowired
    SessionTemplate sessionTemplate;

    private void initConfig() {
        if (config == null || lastUpdateTime + 60 * 60 * 1000l < System.currentTimeMillis()) {
            List<ModelObject> objects = sessionTemplate.query(TableMapConfig.class)
                    .eq(TableMapConfig.type, table_prefix)
                    .queries();

            for (ModelObject object : objects) {
                config.put(object.getString(TableMapConfig.name), object.getString(TableMapConfig.value));
            }

            lastUpdateTime = System.currentTimeMillis();
        }
    }

    @Override
    public String getIpAddress(String ip) throws IOException {
        this.initConfig();
        String key = config.get("key");
        String sig = config.get("sig");
        StringBuilder sb = new StringBuilder();
        sb.append("key=").append(key).append("&");
        if (StringUtils.isNotBlank(sig)) {
            sb.append("sig=").append(sig).append("&");
        }
        sb.append("output=JSON&");
        sb.append("ip=").append(ip);
        String jsonString = HttpUtils.get(IP_LOCATION_URL + "?" + sb.toString());
        ModelObject object = ModelObject.parseObject(jsonString);
        if (object != null && "1".equals(object.getString("status"))) {
            return object.getString("province") + object.getString("city");
        }
        return "";
    }

    @Override
    public void updateAdministrativeRegion() {
        this.initConfig();
        //中国 http://lbs.amap.com/api/webservice/guide/api/district
//        String url = getAdminstrativeRegionUrl("中国", 3);
//        String provinceResult = HttpJsonClientUtils.get(url);
//        ModelObject provinceObject = Model.parseObject(provinceResult);
//        if (provinceObject != null && "1".equals(provinceObject.getString("status"))) {
//            ModelArray tempDistricts = provinceObject.getModelArray("districts");
//            if (tempDistricts != null && tempDistricts.size() > 0) {
//                //插入中国
//                ModelObject china = makeChinaAreaObject("中国", 0, 0);
//                sessionTemplate.save(china);
//                Integer countryId = china.getInteger(TableWholeWorldArea.id);
//                if (countryId != null && countryId > -1) {
//                    ModelObject modelObject = tempDistricts.getModelObject(0);
//                    ModelArray provinceDistricts = modelObject.getModelArray("districts");
//                    if (provinceDistricts != null && provinceDistricts.size() > 0) {
//                        for (Object province : provinceDistricts) { //省级区域
//                            String provinceAreaName = ((ModelObject) province).getString("name");
//                            ModelObject provinceModel = makeChinaAreaObject(provinceAreaName, countryId, 1);
//                            sessionTemplate.save(provinceModel);
//                            Integer provinceId = provinceModel.getInteger(TableWholeWorldArea.id);
//                            if (provinceId != null && provinceId > 0) {
//                                ModelArray cityDistricts = ((ModelObject) province).getModelArray("districts");
//                                if (cityDistricts != null && cityDistricts.size() > 0) {
//                                    for (Object city : cityDistricts) { //市级区域
//                                        String cityAreaName = ((ModelObject) city).getString("name");
//                                        ModelObject cityModel = makeChinaAreaObject(cityAreaName, provinceId, 2);
//                                        sessionTemplate.save(cityModel);
//                                        Integer cityId = cityModel.getInteger(TableWholeWorldArea.id);
//                                        if (cityId != null && cityId > 0) {
//                                            ModelArray countyDistricts = ((ModelObject) city).getModelArray("districts");
//                                            if (countyDistricts != null && countyDistricts.size() > 0) {
//                                                for (Object county : countyDistricts) { //区县
//                                                    String countyAreaName = ((ModelObject) county).getString("name");
//                                                    ModelObject countyModel = makeChinaAreaObject(countyAreaName, cityId, 3);
//                                                    sessionTemplate.save(countyModel);
//                                                    Integer countyId = countyModel.getInteger(TableWholeWorldArea.id);
//                                                    if (countyId != null && countyId > 0) {
//                                                        ModelArray townDistricts = ((ModelObject) county).getModelArray("districts");
//                                                        if (townDistricts != null && townDistricts.size() > 0) {
//                                                            for (Object town : townDistricts) { //乡镇
//                                                                String townAreaName = ((ModelObject) town).getString("name");
//                                                                ModelObject townModel = makeChinaAreaObject(townAreaName, countyId, 4);
//                                                                sessionTemplate.save(townModel);
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
        //外国

    }

    private String getAdminstrativeRegionUrl(String keywords, int subdistrict) {
        String key = config.get("key");
        StringBuilder sb = new StringBuilder();
        sb.append("key=").append(key).append("&");
        sb.append("keywords=").append(keywords).append("&");
        sb.append("subdistrict=").append(subdistrict).append("&");
        sb.append("extensions=base&");
        sb.append("output=JSON");
        return PLACE_INFO_URL + "?" + sb.toString();
    }

    private ModelObject makeAreaObject(int state, String stateName, String areaName, int parentId, int level) {
//        ModelObject object = new ModelObject();
//        object.setObjectClass(TableWholeWorldArea.class);
//        object.put(TableWholeWorldArea.state, state);
//        object.put(TableWholeWorldArea.stateName, stateName);
//        object.put(TableWholeWorldArea.areaName, areaName);
//        object.put(TableWholeWorldArea.parentId, parentId);
//        object.put(TableWholeWorldArea.level, level);
//        object.put(TableWholeWorldArea.enable, 1);
//        return object;
        return null;
    }

    private ModelObject makeChinaAreaObject(String areaName, int parentId, int level) {
        return makeAreaObject(0, "中国", areaName, parentId, level);
    }
}
