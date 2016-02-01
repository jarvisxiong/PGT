package com.pgt.tender.bean;

import com.pgt.category.bean.Category;
import com.pgt.product.bean.Product;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by carlwang on 1/16/16.
 */
public class Tender implements Serializable {
    private Integer tenderId;
    /**
     * 当铺的id
     */
    private Integer pawnShopId;
    /**
     * 当铺所有者的id
     */
    private Integer pawnShopOwnerId;
    /**
     * 当票编号
     */
    private Integer pawnTicketId;
    /**
     * 投资总金额
     */
    private Double tenderTotal;
    /**
     * 可以投资的份数
     */
    private Integer tenderQuantity;
    /**
     * 最小投资金额
     */
    private Double  smallMoney;

    /**
     * 开标时间
     */
    private Date publishDate;
    /**
     * 截至时间
     */
    private Date dueDate;
    /**
     * 收益率
     */
    private Double interestRate;
    /**
     * 投资名称
     */
    private String name;
    /**
     * 投资的详情
     */
    private String description;
    /**
     * 投资后多久天后开始算收益
     */
    private Integer prePeriod;
    /**
     * 无息天数
     */
    private Integer postPeriod;
    /**
     * 被投资的产品
     */
    private List<Product> products;
    /**
     * 投资创建日期
     */
    private Date creationDate;
    /**
     * 投资更新日期
     */
    private Date updateDate;
    /**
     * 投资所属分类
     */
    private Category category;
    /**
     * 手续费费率
     */
    private Double handlingFeeRate;
    /**
     * 是否是分类的热门
     */
    private Boolean categoryHot = false;
    /**
     * 是否是网站的热门
     */


    private Boolean siteHot = false;

    private Integer categoryId;


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("tenderId:").append(tenderId);
        stringBuilder.append(",");
        stringBuilder.append("pawnshopId:").append(pawnShopId);
        stringBuilder.append(",");
        stringBuilder.append("pawnTicketId:").append(pawnTicketId);
        stringBuilder.append(",");
        stringBuilder.append("tenderTotal:").append(tenderTotal);
        stringBuilder.append(",");
        stringBuilder.append("tenderQuantity:").append(tenderQuantity);
        stringBuilder.append(",");
        stringBuilder.append("publishDate:").append(publishDate);
        stringBuilder.append(",");
        stringBuilder.append("dueDate:").append(dueDate);
        stringBuilder.append(",");
        stringBuilder.append("interestRate:").append(interestRate);
        stringBuilder.append(",");
        stringBuilder.append("name:").append(name);
        stringBuilder.append(",");
        stringBuilder.append("description:").append(description);
        stringBuilder.append(",");
        stringBuilder.append("prePeriod:").append(prePeriod);
        stringBuilder.append(",");
        stringBuilder.append("postPeriod:").append(postPeriod);
        stringBuilder.append(",");
        stringBuilder.append("creationDate:").append(creationDate);
        stringBuilder.append(",");
        stringBuilder.append("updateDate:").append(updateDate);
        stringBuilder.append(",");
        stringBuilder.append("categoryId:").append(category == null ? null : category.getId());
        stringBuilder.append(",");
        stringBuilder.append("creationDate:");
        if (!ObjectUtils.isEmpty(products)) {
            products.stream().forEach(product -> stringBuilder.append(product.getProductId()).append(","));
        }
        return stringBuilder.toString();
    }

    public double getUnitPrice() {
        if (tenderQuantity > 0) {
            return tenderTotal / tenderQuantity;
        }
        return 0;
    }

    public Double getSmallMoney () {
        return smallMoney;
    }

    public void setSmallMoney (Double smallMoney) {
        this.smallMoney = smallMoney;
    }

    public Integer getPawnShopId() {
        return pawnShopId;
    }

    public void setPawnShopId(Integer pawnShopId) {
        this.pawnShopId = pawnShopId;
    }

    public Integer getPawnShopOwnerId() {
        return pawnShopOwnerId;
    }

    public void setPawnShopOwnerId(Integer pawnShopOwnerId) {
        this.pawnShopOwnerId = pawnShopOwnerId;
    }

    public Integer getPawnTicketId() {
        return pawnTicketId;
    }

    public void setPawnTicketId(Integer pawnTicketId) {
        this.pawnTicketId = pawnTicketId;
    }

    public Double getTenderTotal() {
        return tenderTotal;
    }

    public void setTenderTotal(Double tenderTotal) {
        this.tenderTotal = tenderTotal;
    }

    public Integer getTenderQuantity() {
        return tenderQuantity;
    }

    public void setTenderQuantity(Integer tenderQuantity) {
        this.tenderQuantity = tenderQuantity;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrePeriod() {
        return prePeriod;
    }

    public void setPrePeriod(Integer prePeriod) {
        this.prePeriod = prePeriod;
    }

    public Integer getPostPeriod() {
        return postPeriod;
    }

    public void setPostPeriod(Integer postPeriod) {
        this.postPeriod = postPeriod;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
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

    public Integer getTenderId() {
        return this.tenderId;
    }

    public void setTenderId (Integer tenderId) {
        this.tenderId = tenderId;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory (Category category) {
        this.category = category;
    }

    public Double getHandlingFeeRate() {
        return handlingFeeRate;
    }

    public void setHandlingFeeRate(Double handlingFeeRate) {
        this.handlingFeeRate = handlingFeeRate;
    }

    public Boolean getCategoryHot() {
        return categoryHot;
    }

    public void setCategoryHot(Boolean categoryHot) {
        this.categoryHot = categoryHot;
    }

    public Boolean getSiteHot() {
        return siteHot;
    }

    public void setSiteHot(Boolean siteHot) {
        this.siteHot = siteHot;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}