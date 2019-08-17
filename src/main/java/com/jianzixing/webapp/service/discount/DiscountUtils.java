package com.jianzixing.webapp.service.discount;

import com.jianzixing.webapp.service.GlobalService;
import org.apache.commons.lang.StringUtils;
import org.mimosaframework.core.json.ModelObject;
import org.mimosaframework.core.utils.ExcelUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DiscountUtils {
    public static final List<ModelObject> getGoodsFromFileBySerialNumber(String fileName) throws Exception {
        if (StringUtils.isNotBlank(fileName)) {
            InputStream inputStream = GlobalService.fileService.readFileByName(fileName);
            if (inputStream != null) {
                List<ModelObject> full = new ArrayList<>();
                ExcelUtils.reads(inputStream, new String[]{"serialNumber", "goodsName"}, new ExcelUtils.ExcelReadCallback() {
                    @Override
                    public void reads(List<ModelObject> objects) {
                        full.addAll(objects);
                    }
                });
                List<String> serialNumbers = new ArrayList<>();
                if (full != null) {
                    for (ModelObject json : full) {
                        serialNumbers.add(json.getString("serialNumber"));
                    }
                }
                List<ModelObject> goods = GlobalService.goodsService.getGoodsBySerialNumber(serialNumbers);
                return goods;
            }
        }
        return null;
    }
}
