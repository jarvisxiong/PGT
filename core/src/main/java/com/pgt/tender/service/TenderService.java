package com.pgt.tender.service;

import com.pgt.search.service.TenderSearchEngineService;
import com.pgt.tender.bean.Tender;
import com.pgt.tender.bean.TenderCategory;
import com.pgt.tender.bean.TenderQuery;
import com.pgt.tender.mapper.TenderCategoryMapper;
import com.pgt.tender.mapper.TenderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by carlwang on 1/16/16.
 */
@Service
@Transactional
public class TenderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenderService.class);

    @Autowired
    private TenderMapper tenderMapper;

    @Autowired
    private TenderCategoryMapper tenderCategoryMapper;

    @Autowired
    private TenderSearchEngineService tenderSearchEngineService;

    public List<Tender> queryTenderByQuery (TenderQuery tenderQuery) {
        List<Tender> tenderList = tenderMapper.queryTenderByQuery(tenderQuery);
        if (ObjectUtils.isEmpty(tenderList)) {
            LOGGER.debug("Can not find tenderList");
            return tenderList;
        }
        return tenderList;
    }

    public List<Tender> queryAllTender() {
        List<Tender> tenders = tenderMapper.queryAllTender();
        if (ObjectUtils.isEmpty(tenders)) {
            LOGGER.debug("Can not find tenders");
            return tenders;
        }
        LOGGER.debug("Find {} tenders.", tenders.size());
        return tenders;
    }


    public Tender queryTenderById(Integer tenderId, Boolean onlyActive) {
        LOGGER.debug("The tender id is {},onlyActive is {}.", tenderId, onlyActive);
        Tender tender = tenderMapper.queryTenderById(tenderId, onlyActive);
        LOGGER.debug("The tender is {}.", tender);
        return tender;
    }

    public List<Tender> queryTenders(Tender tender) {
        return tenderMapper.queryTenders(tender);
    }


    public List<Tender> queryTendersByCategoryId(Integer categoryId, Boolean onlyActive) {
        LOGGER.debug("The category id is {},onlyActive is {}.", categoryId, onlyActive);
        List<Tender> tenders = tenderMapper.queryTendersByCategoryId(categoryId, onlyActive);
        if (ObjectUtils.isEmpty(tenders)) {
            LOGGER.debug("Can not find tender with category id is {}.", categoryId);
            return tenders;
        }
        LOGGER.debug("Find {} tenders with category id is {}.", tenders.size(), categoryId);
        return tenders;
    }

    public Integer createTender(Tender tender) {
        LOGGER.debug("Begin to create tender.");
        tender.setCreationDate(new Date());
        tender.setUpdateDate(new Date());
        tender.setTenderTotal(0.00);
        tenderMapper.createTender(tender);
        LOGGER.debug("The new tender id is {}.", tender.getTenderId());
        if(!ObjectUtils.isEmpty(tender.getTenderId())){
            TenderCategory tenderCategory = new TenderCategory();
            tenderCategory.setCategoryId(tender.getCategoryId());
            tenderCategory.setTenderId(tender.getTenderId());
            tenderCategoryMapper.createTenderCategory(tenderCategory);
            LOGGER.debug("The new tenderCategory tenderId is {},and categoryId is{}",tenderCategory.getTenderId(),tenderCategory.getCategoryId());
        }
        tenderSearchEngineService.createTenderIndex(tender);
        return tender.getTenderId();
    }


    public Integer updateTender(Tender tender) {
        LOGGER.debug("Begin to update tender,the tender id is {}.", tender.getTenderId());

        Tender old_tender=tenderMapper.queryTenderById(tender.getTenderId(),false);
        if(!ObjectUtils.isEmpty(old_tender)){
            TenderCategory tenderCategory = new TenderCategory();

            tenderCategory.setCategoryId(old_tender.getCategory().getId());
            tenderCategory.setTenderId(old_tender.getTenderId());
            TenderCategory old_tenderCategory= tenderCategoryMapper.findTenderByTenderIdAndCategoryId(tenderCategory);
            LOGGER.debug("The told_TenderCategory tenderId is {},and categoryId is{}",old_tenderCategory.getTenderId(),old_tenderCategory.getCategoryId());
            old_tenderCategory.setCategoryId(tender.getCategoryId());
            old_tenderCategory.setTenderId(tender.getTenderId());
            tenderCategoryMapper.updateTenderCategory(old_tenderCategory);
            LOGGER.debug("The update tenderCategory tenderId is {},and categoryId is{}",old_tenderCategory.getTenderId(),old_tenderCategory.getCategoryId());
        }
        tender.setUpdateDate(new Date());
        tenderMapper.updateTender(tender);
        LOGGER.debug("Success for update tender.");
        tenderSearchEngineService.updateTender(tender);
        return tender.getTenderId();
    }

    public Integer deleteTender(Integer tenderId) {
        LOGGER.debug("Begin to delete tender with id is {}.", tenderId);
        tenderMapper.deleteTender(tenderId);
        Tender tender=tenderMapper.queryTenderById(tenderId,false);
        TenderCategory tenderCategory = new TenderCategory();
        if(!ObjectUtils.isEmpty(tender.getCategory())) {
            tenderCategory.setCategoryId(tender.getCategory().getId());
            tenderCategory.setTenderId(tender.getTenderId());
            TenderCategory old_tenderCategory = tenderCategoryMapper.findTenderByTenderIdAndCategoryId(tenderCategory);
            if (ObjectUtils.isEmpty(old_tenderCategory)) {
                tenderMapper.deleteTender(tenderId);
                LOGGER.debug("Success for delete tender.");
                tenderCategoryMapper.deleteTenderCategory(old_tenderCategory.getId());
            }
        }
        return tenderId;
    }

}
