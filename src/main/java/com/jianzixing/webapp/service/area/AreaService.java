package com.jianzixing.webapp.service.area;

import org.mimosaframework.core.json.ModelObject;

import java.util.List;

/**
 * author qinmingtao
 * https://github.com/modood/Administrative-divisions-of-China
 * 这个项目可以爬取全国行政划分
 * desc 关于区域的
 */
public interface AreaService {
    String CHINA_NAME = "中国";
    String CHINA_CODE = "CHN";

    List<ModelObject> getChinaProvince();

    List<ModelObject> getChinaCity(int provinceCode);

    List<ModelObject> getChinaArea(int cityCode);

    List<ModelObject> getChinaByClassify();

    List<ModelObject> getLineAreaByCode(List<String> code);

    ModelObject getProvinceByCode(int provinceCode);

    ModelObject getCityByCode(int provinceCode);

    ModelObject getAreaByCode(int provinceCode);
}
