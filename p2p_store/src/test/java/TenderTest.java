import com.google.common.collect.Lists;
import com.pgt.constant.Constants;
import com.pgt.product.bean.P2PProduct;
import com.pgt.product.bean.Product;
import com.pgt.product.service.TenderProductService;
import com.pgt.search.bean.ESTerm;
import com.pgt.search.bean.SearchPaginationBean;
import com.pgt.search.service.TenderSearchEngineService;
import com.pgt.tender.bean.Tender;
import com.pgt.tender.bean.TenderCategory;
import com.pgt.tender.mapper.TenderCategoryMapper;
import com.pgt.tender.service.TenderService;
import org.elasticsearch.action.search.SearchResponse;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * Created by carlwang on 1/19/16.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-core-config.xml") // 加载
public class TenderTest {

    @Autowired
    private TenderService tenderService;
    @Autowired
    private TenderProductService tenderProductService;
    @Autowired
    private TenderCategoryMapper tenderCategoryMapper;
    @Autowired
    private TenderSearchEngineService tenderSearchEngineService;

    @Test
    public void createTender() {
        Tender tender = new Tender();
        tender.setCreationDate(new Date());
        tender.setDescription("description");
        tender.setDueDate(new Date());
        tender.setInterestRate(0.08);
        tender.setName("Tender name");
        tender.setPawnShopId(100);
        tender.setPawnTicketId(1000);
        tender.setPostPeriod(10);
        tender.setPrePeriod(10000);
        tender.setTenderQuantity(10000);
        tender.setTenderTotal(new Double(100000));
        tender.setUpdateDate(new Date());
        tender.setPublishDate(new Date());
        Integer tenderId = tenderService.createTender(tender);
        TenderCategory tenderCategory = new TenderCategory();
        tenderCategory.setTenderId(tenderId);
        tenderCategory.setCategoryId(44);
        SearchPaginationBean searchPaginationBean = new SearchPaginationBean();
        searchPaginationBean.setCategoryId(44 + "");
        searchPaginationBean.setCapacity(100);
//        List<P2PProduct> products = tenderProductService.queryProducts(searchPaginationBean);
//        tender.setProducts(products);
        tenderCategoryMapper.createTenderCategory(tenderCategory);
        Assert.assertNotNull(tenderId);
    }


    @Test
    public void createTenders() {
        for (int i = 0; i < 100; i++) {
            createTender();
        }
    }

    @Test
    public void queryTender() {

    }

    @Test
    public void queryTenderByCategoryId() {
        List<Tender> tenders = tenderService.queryTendersByCategoryId(44, false);
        Assert.assertNotNull(tenders);
        Tender tender = tenders.get(0);
        Assert.assertNotNull(tender);
        Assert.assertTrue(tender.getProducts().size() > 0);
    }

    @Test
    public void indexTender() {
        tenderSearchEngineService.initialIndex();
        tenderSearchEngineService.index();
    }

    @Test
    public void queryTenderByEs() {
        ESTerm term = new ESTerm();
        term.setPropertyName("tender.name");
        term.setTermValue("Tender");

        SearchResponse response = tenderSearchEngineService.findTender(null, Lists.newArrayList(term), null, null, null, null, Constants
                .TENDER_INDEX_TYPE);
        Assert.assertNotNull(response);
    }

    @Test
    public void queryCategoryHot() {
        SearchResponse response = tenderSearchEngineService.findCategoryHotTender();
        Assert.assertNotNull(response);
    }

    @Test
    public void querySiteHot() {
        SearchResponse response = tenderSearchEngineService.findSiteHotTender();
        Assert.assertNotNull(response);
    }

}

