package com.pgt.sso.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgt.cart.bean.Order;
import com.pgt.configuration.Configuration;
import com.pgt.constant.Constants;
import com.pgt.search.bean.ESFilter;
import com.pgt.search.bean.ESSort;
import com.pgt.search.service.AbstractSearchEngineService;
import com.pgt.sso.bean.UserCache;
import com.pgt.user.bean.User;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by carlwang on 2/18/16.
 */
@Service
public class SSOService extends AbstractSearchEngineService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SSOService.class);
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private Configuration configuration;

    public boolean cacheUser(User user, Order b2cOrder, Order p2pOrder) {
        if (ObjectUtils.isEmpty(user)) {
            LOGGER.debug("The user is empty.");
            return false;
        }
        UserCache userCache = new UserCache(new Date(), user, b2cOrder, p2pOrder);
        try {
            Client client = getIndexClient();
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(userCache);
            IndexRequestBuilder indexRequestBuilder = client.prepareIndex(Constants.SITE_INDEX_NAME, Constants
                            .USER_CACHE_INDEX_TYPE,null
                    )
                    .setSource(data);
            IndexResponse indexResponse = indexRequestBuilder.execute().get();
            if (!indexResponse.isCreated()) {
                LOGGER.warn("Not success create the user cache index.");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        LOGGER.debug("Success to create the user cache index.");
        writeCookie(userCache);
        return true;
    }

    private void writeCookie(UserCache userCache) {
        Cookie cookie = new Cookie(configuration.getUserCacheTokenKey(), userCache.getToken());
        cookie.setDomain(configuration.getDomain());
        cookie.setPath("/");
        cookie.setMaxAge(-1);
       response.addCookie(cookie);
    }


    public boolean updateCacheUser(User user, Order b2cOrder, Order p2pOrder,String tokenId,String token) {
        if (ObjectUtils.isEmpty(user)) {
            LOGGER.debug("The user is empty.");
            return false;
        }
        try {
            UserCache userCache = new UserCache(new Date(), user, b2cOrder, p2pOrder);
            userCache.setToken(token);
            Client client = getIndexClient();
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(userCache);
            UpdateRequestBuilder updateRequestBuilder = client.prepareUpdate(Constants.SITE_INDEX_NAME, Constants
                            .USER_CACHE_INDEX_TYPE,
                    tokenId)
                    .setDoc(data);

               UpdateResponse updateResponse = updateRequestBuilder.execute().actionGet(1000000);
               if (updateResponse.getShardInfo().getFailed() > 0) {
                   LOGGER.debug("Not success update the user cache index.");
                   return false;
               }

        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.debug("Success to update the user cache index.");
        return true;
    }


    public boolean deleteCacheUser(String tokenId) {
        if (ObjectUtils.isEmpty(tokenId)) {
            LOGGER.debug("The user is empty.");
            return false;
        }
        try {
            Client client = getIndexClient();
            DeleteRequestBuilder deleteRequestBuilder = client.prepareDelete(Constants.SITE_INDEX_NAME, Constants
                            .USER_CACHE_INDEX_TYPE,
                    tokenId);
            DeleteResponse deleteResponse = deleteRequestBuilder.execute().get();
            if (!deleteResponse.isFound()) {

                LOGGER.debug("Can not find the user token with id is {}.",tokenId);
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        LOGGER.debug("Success to delete the user cache index.");
        return true;
    }

    public boolean isUserExpire(Integer userId) {
        if (ObjectUtils.isEmpty(userId)) {
            LOGGER.debug("The user is empty.");
            return true;
        }
        try {
            SearchRequestBuilder searchRequestBuilder = initialSearchRequestBuilder(Constants.SITE_INDEX_NAME, Constants.USER_CACHE_INDEX_TYPE);
            BoolQueryBuilder qb = boolQuery();
            searchRequestBuilder.setQuery(qb);
            qb.must(termQuery("user.id", userId));
            SearchResponse response = searchRequestBuilder.execute().actionGet();
            SearchHits searchHits = response.getHits();
            if (ObjectUtils.isEmpty(searchHits) || ArrayUtils.isEmpty(searchHits.getHits())) {
                LOGGER.debug("Can not find the user with id is {}, so may not login.", userId);
                return true;
            }
            SearchHit[] searchHits1 = searchHits.getHits();
            if (searchHits1.length > 1) {
                LOGGER.debug("Is not the only user ID. ");
                return true;
            }
            SearchHit searchHit = searchHits1[0];
            Long updateTime = (Long) searchHit.getSource().get("updateTime");
            long expireTime = updateTime + 30 * 60 * 1000;
            Long currentTime = System.currentTimeMillis();
            if (expireTime < currentTime) {
                LOGGER.debug("The user is expire. ");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.debug("The user not expire.");
        return false;
    }


    public boolean isUserExpire(String userCacheToken) {
        if (ObjectUtils.isEmpty(userCacheToken)) {
            LOGGER.debug("The user cache token is empty.");
            return true;
        }
        try {
            SearchRequestBuilder searchRequestBuilder = initialSearchRequestBuilder(Constants.SITE_INDEX_NAME, Constants.USER_CACHE_INDEX_TYPE);
            BoolQueryBuilder qb = boolQuery();
            searchRequestBuilder.setQuery(qb);
            qb.must(termQuery("token", userCacheToken));
            SearchResponse response = searchRequestBuilder.execute().actionGet();
            SearchHits searchHits = response.getHits();
            if (ObjectUtils.isEmpty(searchHits) || ArrayUtils.isEmpty(searchHits.getHits())) {
                LOGGER.debug("Can not find the user with token is {}, so may not login.", userCacheToken);
                return true;
            }
            SearchHit[] searchHits1 = searchHits.getHits();
            if (searchHits1.length > 1) {
                LOGGER.debug("Is not the only user ID. ");
                return true;
            }
            SearchHit searchHit = searchHits1[0];
            Long updateTime = (Long) searchHit.getSource().get("updateTime");
            long expireTime = updateTime + 30 * 60 * 1000;
            Long currentTime = System.currentTimeMillis();
            if (expireTime < currentTime) {
                LOGGER.debug("The user is expire. ");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.debug("The user not expire.");
        return false;
    }


    public String findUserUsername(String userCacheToken) {
         if (ObjectUtils.isEmpty(userCacheToken)) {
            LOGGER.debug("The user cache token is empty.");
            return null;
           }
        try {
            SearchRequestBuilder searchRequestBuilder = initialSearchRequestBuilder(Constants.SITE_INDEX_NAME, Constants.USER_CACHE_INDEX_TYPE);
            BoolQueryBuilder qb = boolQuery();
            searchRequestBuilder.setQuery(qb);
            qb.must(termQuery("token", userCacheToken));
            SearchResponse response = searchRequestBuilder.execute().actionGet();
            SearchHits searchHits = response.getHits();
            if (ObjectUtils.isEmpty(searchHits) || ArrayUtils.isEmpty(searchHits.getHits())) {
                LOGGER.debug("Can not find the user with token is {}, so may not login.", userCacheToken);
                return null;
            }
            SearchHit[] searchHits1 = searchHits.getHits();
            if (searchHits1.length > 1) {
                LOGGER.debug("Is not the only user ID. ");
                return null;
            }
            SearchHit searchHit = searchHits1[0];
            Map map= (Map) searchHit.getSource().get("user");
            String username =(String)map.get("username");
            if(username==null){
                LOGGER.debug("The find username is fail");
                return null;
            }
                LOGGER.debug("The find username is success and username is {}.",username);
                return username;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.debug("The user not expire.");
        return null;
    }


    public String findSSOTokenId(String userCacheToken) {
        if (ObjectUtils.isEmpty(userCacheToken)) {
            LOGGER.debug("The user cache token is empty.");
            return null;
        }
        try {
            SearchRequestBuilder searchRequestBuilder = initialSearchRequestBuilder(Constants.SITE_INDEX_NAME, Constants.USER_CACHE_INDEX_TYPE);
            BoolQueryBuilder qb = boolQuery();
            searchRequestBuilder.setQuery(qb);
            qb.must(termQuery("token", userCacheToken));
            SearchResponse response = searchRequestBuilder.execute().actionGet();
            SearchHits searchHits = response.getHits();
            if (ObjectUtils.isEmpty(searchHits) || ArrayUtils.isEmpty(searchHits.getHits())) {
                LOGGER.debug("Can not find the user with token is {}, so may not login", userCacheToken);
                return null;
            }
            SearchHit[] searchHits1 = searchHits.getHits();
            if (searchHits1.length > 1) {
                LOGGER.debug("Is not the only user ID. ");
                return null;
            }
            SearchHit searchHit = searchHits1[0];
            String tokenId  =searchHit.getId();
            LOGGER.debug("The find username is success and token Id {}.",tokenId);
            return tokenId;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.debug("The tokenId not expire.");
        return null;
    }



    @Override
    public void index() {

    }

    @Override
    public void update(Integer id) {

    }

    @Override
    public void initialIndex() {

    }

    @Override
    protected void buildSort(SearchRequestBuilder searchRequestBuilder, List<ESSort> esSorts) {

    }

    @Override
    protected void buildFilter(BoolQueryBuilder boolQueryBuilder, ESFilter esFilter) {

    }
}
