package com.pgt.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.pgt.category.bean.Category;
import com.pgt.category.service.CategoryService;
import com.pgt.constant.Constants;
import com.pgt.home.bean.HomeTender;
import com.pgt.search.bean.ESAggregation;
import com.pgt.search.bean.ESRange;
import com.pgt.search.bean.ESSort;
import com.pgt.search.bean.ESTerm;
import com.pgt.tender.bean.ESTender;
import com.pgt.tender.bean.Tender;
import com.pgt.tender.service.TenderService;
import com.pgt.utils.PaginationBean;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by carlwang on 1/19/16.
 */

@Service
public class TenderSearchEngineService extends AbstractSearchEngineService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenderSearchEngineService.class);

    @Autowired
    private TenderService tenderService;
    @Autowired
    private CategoryService categoryService;


    @Override
    public void index() {

        tenderIndex();
        homeTenderIndex();
    }

    private void homeTenderIndex() {
        BulkResponse bulkResponse;
        LOGGER.debug("Begin to home tender index.");

        BulkRequestBuilder bulkRequest = null;
        try {
            bulkRequest = getIndexClient().prepareBulk();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Category> categories = categoryService.queryRootTenderCategories();
        if (CollectionUtils.isEmpty(categories)) {
            LOGGER.debug("The root tender category is empty.");
            return;
        }

        final BulkRequestBuilder finalBulkRequest = bulkRequest;
        categories.stream().forEach(category1 -> {
            List<Category> categoryList = category1.getChildren();
            HomeTender homeTender = new HomeTender();
            homeTender.setCategory(category1);
            indexHomeTenders(homeTender, categoryList, finalBulkRequest);
        });
        bulkResponse = bulkRequest.execute().actionGet(100000);
        if (bulkResponse.hasFailures()) {
            LOGGER.error("The tender index is failed.");
        }

    }

    private void indexHomeTenders(HomeTender homeTender, List<Category> categoryList, BulkRequestBuilder bulkRequest) {
        if (CollectionUtils.isEmpty(categoryList)) {
            LOGGER.debug("The category list is empty.");
            return;
        }
        try {

            categoryList.stream().forEach(category -> {
                Integer categoryId = category.getId();
                Tender tender=new Tender();
                tender.setCategoryId(categoryId);
                tender.setCategoryHot(true);
                List<Tender> tenders = tenderService.queryTenders(tender);
                homeTender.getTenderList().addAll(tenders);
            });
            try {
                Client client = getIndexClient();
                ObjectMapper mapper = new ObjectMapper();
                String data = mapper.writeValueAsString(homeTender);
                IndexRequestBuilder indexRequestBuilder = client.prepareIndex(Constants.SITE_INDEX_NAME, Constants
                                .HOME_TENDER_INDEX_TYPE,
                        homeTender.getCategory().getId() + "")
                        .setSource(data);
                bulkRequest.add(indexRequestBuilder);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void tenderIndex() {
        try {
            BulkResponse bulkResponse;
            LOGGER.debug("Begin to tender index.");
            Client client = getIndexClient();
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            List<Tender> tenders = tenderService.queryAllTender();
            if (!ObjectUtils.isEmpty(tenders)) {
                tenders.stream().forEach(tender -> {
                    Category category = tender.getCategory();
                    Category rootCategory = null;
                    if (!ObjectUtils.isEmpty(category)) {
                        rootCategory = category.getParent();
                    }
                    ESTender esTender = new ESTender(tender, rootCategory);
                    ObjectMapper mapper = new ObjectMapper();
                    String data = null;
                    try {
                        data = mapper.writeValueAsString(esTender);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    IndexRequestBuilder indexRequestBuilder = client.prepareIndex(Constants.SITE_INDEX_NAME, Constants
                                    .TENDER_INDEX_TYPE,
                            tender.getTenderId() + "")
                            .setSource(data);
                    bulkRequest.add(indexRequestBuilder);
                });
            }
            bulkResponse = bulkRequest.execute().actionGet(100000);
            if (bulkResponse.hasFailures()) {
                LOGGER.error("The tender index is failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SearchResponse findTender(ESTerm esTerm, List<ESTerm> esMatches, ESRange esRange, List<ESSort> esSortList,
                                     PaginationBean paginationBean,
                                     ESAggregation categoryIdAggregation, String indexType) {
        SearchResponse response = null;
        try {
            SearchRequestBuilder searchRequestBuilder = buildSearchRequestBuilder(Constants.SITE_INDEX_NAME, indexType);
            BoolQueryBuilder qb = boolQuery();
            searchRequestBuilder.setQuery(qb);

            buildQueryBuilder(esTerm, esMatches, esRange, esSortList, paginationBean, categoryIdAggregation, searchRequestBuilder, qb);
            response = searchRequestBuilder.execute()
                    .actionGet();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


    public SearchResponse findCategoryHotTender() {

        return findTender(null, null, null, null, null, null, Constants.HOME_TENDER_INDEX_TYPE);
    }

    public SearchResponse findSiteHotTender() {
        ESTerm esTerm = new ESTerm();
        List<ESTerm> matches = Lists.newArrayList(esTerm);
        esTerm.setPropertyName(Constants.SITE_HOT_PROPERTY);
        esTerm.setTermValue(String.valueOf(true));

        return findTender(null, matches, null, null, null, null, Constants.TENDER_INDEX_TYPE);
    }


    @Override
    public void update(Integer id) {

    }

    @Override
    public void initialIndex() {
        super.initialIndex();
        createTenderMapping();
    }


    private void createTenderMapping() {
        LOGGER.debug("Begin to create tender mapping.");
        createMapping(Constants.SITE_INDEX_NAME, Constants.TENDER_INDEX_TYPE, getEsConfiguration().getTenderAnalyzerFields());
        LOGGER.debug("End to create tender mapping.");
    }
}