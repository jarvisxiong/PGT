package com.pgt.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgt.configuration.ESConfiguration;
import com.pgt.home.bean.RecommendedProduct;
import com.pgt.product.bean.ProductType;
import com.pgt.search.bean.ESFilter;
import com.pgt.search.bean.ESSort;
import com.pgt.search.procedure.RecommendIndexProcedure;
import com.pgt.utils.SearchConvertToList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * Created by carlwang on 4/7/16.
 */

@Service
public class AdvertisementSearchEngineService extends AbstractSearchEngineService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementSearchEngineService.class);
    @Resource(name = "recommendIndexProcedure")
    private RecommendIndexProcedure recommendIndexProcedure;

    @Autowired
    private ESConfiguration esConfiguration;


    public SearchResponse findAll(String indexName, String indexType) {
        return super.findAll(indexName, indexType);
    }

    @Override
    public void index() {
        BulkResponse bulkResponse;
        try {
            LOGGER.debug("Begin to recommend products index.");
            Client client = getIndexClient();
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            List<Pair<String, String>> recommendProducts = recommendIndexProcedure.buildSourceList();
            if (CollectionUtils.isEmpty(recommendProducts)) {
                LOGGER.debug("The recommend products is empty.");
                return;
            }
            recommendProducts.stream().forEach(stringStringPair -> {
                IndexRequestBuilder indexRequestBuilder =
                        client.prepareIndex(esConfiguration.getAdvertisementIndexName(), esConfiguration.getRecommendProductTypeName(),
                                stringStringPair.getLeft())
                                .setSource(stringStringPair.getRight());
                bulkRequest.add(indexRequestBuilder);
            });
            if (bulkRequest.numberOfActions() == 0) {
                LOGGER.debug("Not need create index, cause request number is 0.");
                return;
            }
            bulkResponse = bulkRequest.execute().actionGet(100000);
            if (bulkResponse.hasFailures()) {
                LOGGER.error("The recommend products index is failed.");
                return;
            }
            LOGGER.debug("The recommend products index is complete,success create {},the index name is {},the index type name is {}.", bulkResponse
                    .getItems
                            ().length, esConfiguration.getAdvertisementIndexName(), esConfiguration.getRecommendProductTypeName());

        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.debug("End to category index.");
    }

    /**
     * This method is used to create a RecommendedProduct in elasticsearch.
     *
     * @param recommendedProduct
     */
    public void createRecommendProduct(RecommendedProduct recommendedProduct) {
        if (ObjectUtils.isEmpty(recommendedProduct)) {
            LOGGER.debug("The recommend product is empty.");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();

        IndexResponse response;
        IndexRequestBuilder indexRequestBuilder =
                null;
        try {
            indexRequestBuilder = getIndexClient().prepareIndex(esConfiguration.getAdvertisementIndexName(), esConfiguration
                    .getRecommendProductTypeName(), recommendedProduct.getRecommendedProductId() + "")
                    .setSource(mapper.writeValueAsString(recommendedProduct));
        } catch (IOException e) {
            e.printStackTrace();
        }
        response = indexRequestBuilder.execute().actionGet(100000);
        if (response.isCreated()) {
            LOGGER.debug("Success to create RecommendedProduct.");
        }
    }

    /**
     * This method is used to update a RecommendedProduct in elasticsearch.
     *
     * @param recommendedProduct
     */
    public void updateRecommendProduct(RecommendedProduct recommendedProduct) {
        if (ObjectUtils.isEmpty(recommendedProduct)) {
            LOGGER.debug("The recommend product is empty.");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();

        UpdateResponse response;
        UpdateRequestBuilder updateRequestBuilder =
                null;
        try {
            updateRequestBuilder = getIndexClient().prepareUpdate(esConfiguration.getAdvertisementIndexName(), esConfiguration
                    .getRecommendProductTypeName(), recommendedProduct.getRecommendedProductId() + "")
                    .setDoc(mapper.writeValueAsString(recommendedProduct));
        } catch (IOException e) {
            e.printStackTrace();
        }
        response = updateRequestBuilder.execute().actionGet(100000);
        if (response.isCreated()) {
            LOGGER.debug("Success to update RecommendedProduct.");
        }
    }

    /**
     * This method is used to remove a RecommendedProduct from elasticsearch.
     *
     * @param recommendedProductId
     */
    public void daleteRecommendProduct(Integer recommendedProductId) {
        if (ObjectUtils.isEmpty(recommendedProductId)) {
            LOGGER.debug("The recommendedProductId is empty,do not run delete process.");
            return;
        }
        deleteIndex(String.valueOf(recommendedProductId), esConfiguration.getAdvertisementIndexName(), esConfiguration.getRecommendProductTypeName());
    }


    /**
     * This method is used to find recommendedProducts from elasticsearch.
     *
     * @param productType allow null
     * @return
     */
    public List<Map<String, Object>> findRecommendProducts(ProductType productType) {
        LOGGER.debug("Begin to findRecommendProducts.");
        LOGGER.debug("The product type is {}", productType);
        try {
            SearchRequestBuilder searchRequestBuilder =
                    getSearchClient().prepareSearch(esConfiguration.getAdvertisementIndexName()).setTypes(esConfiguration
                            .getRecommendProductTypeName())
                            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            BoolQueryBuilder qb = boolQuery();
            searchRequestBuilder.setQuery(qb);
            if (!ObjectUtils.isEmpty(productType)) {
                qb.filter(matchQuery("productType", productType));
            }
            FieldSortBuilder sortBuilder = new FieldSortBuilder("sort");
            sortBuilder.order(SortOrder.ASC);
            sortBuilder.unmappedType("long");
            LOGGER.debug("The sort key is {},value is {}.", "sort", SortOrder.ASC);
            searchRequestBuilder.addSort(sortBuilder);
            searchRequestBuilder.setTerminateAfter(Integer.MAX_VALUE);
            SearchResponse response = searchRequestBuilder.execute().actionGet();
            LOGGER.debug("End to findRecommendProducts.");
            return SearchConvertToList.searchConvertToList(response);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        LOGGER.debug("Some error occur.");
        return null;
    }

    @Override
    public void update(Integer id) {

    }

    @Override
    public void initialIndex() {
        LOGGER.debug("Begin to create recommend product mapping");
        createIndex(esConfiguration.getAdvertisementIndexName(), esConfiguration.isClearIndex());
        createMapping(esConfiguration.getAdvertisementIndexName(), esConfiguration.getRecommendProductTypeName(), null);
        LOGGER.debug("End to create recommend product mapping.");
    }

    @Override
    protected void buildSort(SearchRequestBuilder searchRequestBuilder, List<ESSort> esSorts) {

    }

    @Override
    protected void buildFilter(BoolQueryBuilder boolQueryBuilder, ESFilter esFilter) {

    }
}
