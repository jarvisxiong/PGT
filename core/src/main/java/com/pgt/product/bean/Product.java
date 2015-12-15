package com.pgt.product.bean;

import org.springframework.util.ObjectUtils;

import com.pgt.shipping.bean.ShippingAddress;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by carlwang on 10/28/15.
 */
public class Product implements Serializable {

    public static final int INVALID = 0;
    public static final int AVAILABLE = 1;

    private Integer productId;
    private String name;
    private String serialNumber;
    private Double salePrice;
    private Double listPrice;
    private Integer status;
    private String description;
    private Double shippingFee;
    private String relatedCategoryId;
    private Integer stock;
    private Date creationDate;
    private Date updateDate;
    private List<ProductMedia> medias;
    private List<ProductMedia> mainMedias;
    private List<ProductMedia> heroMedias;
    private List<ProductMedia> thumbnailMedias;
    private ProductMedia advertisementMedia;
    private ProductMedia frontMedia;
    private ProductMedia expertMedia;
    private String isNew;// 新旧程度
    private String title;// 标题
    private String shortDescription;
    private ShippingAddress shippingAddress;
    private boolean isHot;

    public List<ProductMedia> getMainMedias() {
        return mainMedias;
    }

    public void setMainMedias(List<ProductMedia> mainMedias) {
        this.mainMedias = mainMedias;
    }

    public List<ProductMedia> getHeroMedias() {
        return heroMedias;
    }

    public void setHeroMedias(List<ProductMedia> heroMedias) {
        this.heroMedias = heroMedias;
    }



    public ProductMedia getAdvertisementMedia() {
        return advertisementMedia;
    }

    public void setAdvertisementMedia(ProductMedia advertisementMedia) {
        this.advertisementMedia = advertisementMedia;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }


    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Double getListPrice() {
        return listPrice;
    }

    public void setListPrice(Double listPrice) {
        this.listPrice = listPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(Double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getRelatedCategoryId() {
        return relatedCategoryId;
    }

    public void setRelatedCategoryId(String relatedCategoryId) {
        this.relatedCategoryId = relatedCategoryId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public List<ProductMedia> getMedias() {
        return medias;
    }

    public void setMedias(List<ProductMedia> medias) {
        this.medias = medias;
    }

    public ProductMedia getFrontMedia() {
        if (frontMedia != null) {
            return frontMedia;
        }
        if (!ObjectUtils.isEmpty(medias)) {
            medias.get(0);
        }
        return frontMedia;
    }

    public void setFrontMedia(ProductMedia frontMedia) {
        this.frontMedia = frontMedia;
    }

    public List<ProductMedia> getThumbnailMedias() {
        return thumbnailMedias;
    }

    public void setThumbnailMedias(List<ProductMedia> thumbnailMedias) {
        this.thumbnailMedias = thumbnailMedias;
    }

    public ProductMedia getExpertMedia() {
        return expertMedia;
    }

    public void setExpertMedia(ProductMedia expertMedia) {
        this.expertMedia = expertMedia;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setIsHot(boolean isHot) {
        this.isHot = isHot;
    }
}