package com.pgt.communication.controller;

import com.pgt.common.bean.CommPaginationBean;
import com.pgt.communication.bean.Discuss;
import com.pgt.communication.bean.DiscussCustom;
import com.pgt.communication.service.DiscussService;
import com.pgt.configuration.Configuration;
import com.pgt.configuration.URLConfiguration;
import com.pgt.constant.Constants;
import com.pgt.constant.UserConstant;
import com.pgt.product.bean.Product;
import com.pgt.product.service.ProductServiceImp;
import com.pgt.user.bean.User;
import com.pgt.utils.ResponseUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/discuss")
public class DiscussController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiscussController.class);

	@Autowired
	private DiscussService discussService;

	@Autowired
	private Configuration configuration;

	@Autowired
	private ProductServiceImp productServiceImp;

	@Autowired
	private URLConfiguration urlConfiguration;

	/**
	 * query discuss by productId
	 * @param productId
	 * @param modelAndView
	 * @param currentIndex
     * @return
     */
	@RequestMapping(value = "/query/{currentIndex}/{productId}", method = RequestMethod.GET)
	public ModelAndView queryDiscussByProductId(@PathVariable("productId") Integer productId, ModelAndView modelAndView,
			@PathVariable("currentIndex") Long currentIndex) {
		modelAndView.setViewName(Constants.DISCUSS_PAGE);
		if (productId == null) {
			LOGGER.debug("The product id is empty.");
			return modelAndView;
		}
		DiscussCustom discussCustom = new DiscussCustom();
		int total = discussService.queryProductAllDiscussCount(productId);
		LOGGER.debug("The query total is {}",total);
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
		modelAndView.addObject("discussList", discussList);
		// 带回分页的条件
		modelAndView.addObject("disPaginationBean", paginationBean);
		return modelAndView;
	}

	// 进入创建页面
	public String CreateDiscussUI() {

		return null;
	}

	/**
	 * create discuss
	 * @param modelAndView
	 * @param productId
	 * @param discuss
	 * @param request
	 * @param session
     * @param response
     */
	@RequestMapping(value = "/createDiscuss", method = RequestMethod.POST)
	public void createdDiscuss(ModelAndView modelAndView, Integer productId, Discuss discuss,
			HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		JSONObject jo = new JSONObject();
		if (ObjectUtils.isEmpty(productId)) {
			LOGGER.debug("The product id is empty.");
			return;
		}
		Product product = productServiceImp.queryProduct(productId);
		// 获取ip
		String ip = request.getRemoteAddr();
		User user = (User) session.getAttribute(UserConstant.CURRENT_USER);
		if (user == null) {
			LOGGER.debug("user is null , Redirect home page.");
			jo.put("logincheck", "no");
			ResponseUtils.renderJson(response, jo.toString());
			return;
		}
		discuss.setUser(user);
		discuss.setProduct(product);
		if (ObjectUtils.isEmpty(user)) {
			discuss.setUser(user);
		}
		discuss.setIp(ip);
		LOGGER.debug("The user ip is {}",ip);
		LOGGER.debug("The user id is {}",user.getId());
		try {
			// 保存咨询的内容
			discussService.createDiscuss(discuss);
		} catch (Exception e) {
			LOGGER.error("The save discuss is error");
		}
		jo.put("message", "successful");
		ResponseUtils.renderJson(response, jo.toString());
	}

	// 进入后台查看讨论页面
	public String queryAllDiscussUI() {
		return "";
	}

	// 查看详情
	public String queryDiscuss() {
		return "";
	}

	// 根据id删除一个咨询
	public String deleteById() {
		return "";
	}

	// 根除id批量删除ids
	public String deleteByIds() {
		return null;
	}

	// 是否显示
	public String isShow() {
		return null;
	}

}
