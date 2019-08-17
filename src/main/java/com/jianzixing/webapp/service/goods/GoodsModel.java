package com.jianzixing.webapp.service.goods;

import org.mimosaframework.core.json.ModelArray;
import org.mimosaframework.core.json.ModelObject;

import java.util.List;

/**
 * @author yangankang
 */
public class GoodsModel {
    private ModelObject info;
    private ModelArray attrs;
    private ModelArray images;
    private ModelArray sku;
    private String desc;

    private List<ModelObject> property;
    private List<ModelObject> skuList;
    private List<ModelObject> imageList;
    private ModelObject descObject;
    private List<ModelObject> supports;

    public ModelObject getInfo() {
        return info;
    }

    public void setInfo(ModelObject info) {
        this.info = info;
    }

    public ModelArray getAttrs() {
        return attrs;
    }

    public void setAttrs(ModelArray attrs) {
        this.attrs = attrs;
    }

    public ModelArray getImages() {
        return images;
    }

    public void setImages(ModelArray images) {
        this.images = images;
    }
    
    public ModelArray getSku() {
        return sku;
    }

    public void setSku(ModelArray sku) {
        this.sku = sku;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<ModelObject> getProperty() {
        return property;
    }

    public void setProperty(List<ModelObject> property) {
        this.property = property;
    }

    public List<ModelObject> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<ModelObject> skuList) {
        this.skuList = skuList;
    }

    public List<ModelObject> getImageList() {
        return imageList;
    }

    public void setImageList(List<ModelObject> imageList) {
        this.imageList = imageList;
    }

    public ModelObject getDescObject() {
        return descObject;
    }

    public void setDescObject(ModelObject descObject) {
        this.descObject = descObject;
    }

    public List<ModelObject> getSupports() {
        return supports;
    }

    public void setSupports(List<ModelObject> supports) {
        this.supports = supports;
    }
}
