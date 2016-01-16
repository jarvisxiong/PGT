package com.pgt.mobile.communication.controller;

import com.pgt.common.bean.CommPaginationBean;
import com.pgt.communication.bean.Discuss;
import com.pgt.communication.bean.DiscussCustom;
import com.pgt.communication.service.DiscussService;
import com.pgt.configuration.Configuration;
import com.pgt.constant.UserConstant;
import com.pgt.mobile.base.controller.BaseMobileController;
import com.pgt.mobile.base.constans.MobileConstans;
import com.pgt.product.bean.Product;
import com.pgt.product.service.ProductService;
import com.pgt.user.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mDiscuss")
public class DiscussMobileController extends BaseMobileController{

	@Autowired
	private DiscussService discussService;

	@Autowired
	private Configuration configuration;

	@Autowired
	private ProductService productService;



	private static final Logger LOGGER = LoggerFactory.getLogger(DiscussMobileController.class);


	// 展示一个商品下面的讨论
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	public  Map<String,Object> queryDiscussByProductId(Integer productId, Long currentIndex) {

		Map<String,Object> responseMap = new HashMap<String,Object>();
		if (productId == null) {
			return responseMobileFail(responseMap, "Product.empty");
		}
		DiscussCustom discussCustom = new DiscussCustom();
		int total = discussService.queryProductAllDiscussCount(productId);
		if (currentIndex == null) {
			currentIndex = 0L;
		}
		CommPaginationBean paginationBean = new CommPaginationBean(configuration.getCommunicationCapacity(),
				currentIndex, total);
		if (currentIndex != null) {
			paginationBean.setCurrentIndex(currentIndex);
		}
		discussCustom.setPaginationBean(paginationBean);
		// 查询某个商品讨论列表 productId
		List<Discuss> discussList = discussService.queryProductAllDiscuss(productId, discussCustom);
		responseMap.put(MobileConstans.MOBILE_STATUS, MobileConstans.MOBILE_STATUS_SUCCESS);
		responseMap.put("discussList",discussList);
		responseMap.put("disPaginationBean",paginationBean);
		return responseMap;
	}

	// 创建一个讨论
	@RequestMapping(value = "/createDiscuss", method = RequestMethod.POST)
	public  Map<String,Object> createdDiscuss(Integer productId,String phoneId ,Discuss discuss,
			HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		Map<String,Object> responseMap = new HashMap<String,Object>();
		if (ObjectUtils.isEmpty(productId)) {
			LOGGER.warn("The product id is empty.");
			return responseMobileFail(responseMap, "Product.empty");
		}
		Product product = productService.queryProduct(productId);
		// 获取ip
		String ip = request.getRemoteAddr();

		if(StringUtils.isEmpty(phoneId)){
			return responseMobileFail(responseMap, "PhoneId.empty");
		}
		User user = (User) session.getAttribute(UserConstant.CURRENT_USER);
		if(ObjectUtils.isEmpty(user)){
			return responseMobileFail(responseMap, "User.empty");
		}

		discuss.setUser(user);
		discuss.setProduct(product);
		if (ObjectUtils.isEmpty(user)) {
			discuss.setUser(user);
		}
		discuss.setIp(ip);
		try {
			// 保存咨询的内容
			discussService.createDiscuss(discuss);
		} catch (Exception e) {
			LOGGER.warn("The save discuss is error");
			return responseMobileFail(responseMap, "save.error");
		}
		responseMap.put(MobileConstans.MOBILE_STATUS, MobileConstans.MOBILE_STATUS_SUCCESS);
        return responseMap;
	}

}
