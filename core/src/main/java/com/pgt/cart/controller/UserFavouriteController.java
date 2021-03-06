package com.pgt.cart.controller;

import com.pgt.base.bean.MyAccountNavigationEnum;
import com.pgt.cart.bean.*;
import com.pgt.cart.bean.pagination.InternalPagination;
import com.pgt.cart.bean.pagination.InternalPaginationBuilder;
import com.pgt.cart.constant.CartConstant;
import com.pgt.cart.exception.OrderPersistentException;
import com.pgt.cart.exception.PriceOrderException;
import com.pgt.cart.service.PriceOrderService;
import com.pgt.cart.service.ResponseBuilderFactory;
import com.pgt.cart.service.ShoppingCartService;
import com.pgt.cart.service.UserFavouriteService;
import com.pgt.cart.util.RepositoryUtils;
import com.pgt.constant.Constants;
import com.pgt.product.bean.Product;
import com.pgt.product.service.ProductServiceImp;
import com.pgt.tender.bean.Tender;
import com.pgt.tender.service.TenderService;
import com.pgt.user.bean.User;
import com.pgt.utils.URLMapping;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Yove on 11/12/2015.
 */
@RequestMapping("/myAccount")
@RestController
public class UserFavouriteController extends TransactionBaseController implements FavouriteMessages, CartMessages {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserFavouriteController.class);

	@Resource(name = "userFavouriteService")
	private UserFavouriteService mUserFavouriteService;

	@Resource(name = "shoppingCartService")
	private ShoppingCartService mShoppingCartService;

	@Resource(name = "priceOrderService")
	private PriceOrderService mPriceOrderService;

	@Resource(name = "productServiceImp")
	private ProductServiceImp mProductServiceImp;

	@Autowired
	private TenderService mTenderService;

	@Autowired
	private URLMapping mURLMapping;

	@Resource(name = "responseBuilderFactory")
	private ResponseBuilderFactory mResponseBuilderFactory;

	@RequestMapping(value = "/favourite")
	@ResponseBody
	public ResponseEntity favourite(HttpServletRequest pRequest, HttpServletResponse pResponse,
	                                @RequestParam(value = "productId", required = true) int productId,
	                                @RequestParam(value = "type", required = false, defaultValue = "1") int favouriteType) {
		ResponseBuilder rb = getResponseBuilderFactory().buildResponseBean().setSuccess(false);
		// check user login state
		User currentUser = getCurrentUser(pRequest);
		if (currentUser == null) {
			LOGGER.debug("Current session user has not sign, skip add item to favourite.");
			rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_USER_LOGIN_REQUIRED);
			return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
		}
		if (!RepositoryUtils.idIsValid(productId)) {
			LOGGER.debug("Add item to favourite with an invalid product id: {}", productId);
			rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_PROD_INVALID);
			return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
		}
		Favourite favourite = null;
		if (favouriteType == FavouriteType.B2C_PRODUCT || favouriteType == FavouriteType.P2P_PRODUCT) {
			Product product = getProductServiceImp().queryProduct(productId);
			if (product == null || !RepositoryUtils.idIsValid(product.getProductId())) {
				LOGGER.debug("Add item to favourite failed for cannot find product with id: {}", productId);
				rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_PROD_INVALID);
				return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
			}
			// create favourite
			favourite = getUserFavouriteService().convertProductToFavourite(currentUser.getId().intValue(), product, favouriteType);
		} else if (favouriteType == FavouriteType.P2P_TENDER) {
			Tender tender = getTenderService().queryTenderById(productId, Boolean.TRUE);
			if (tender == null || !RepositoryUtils.idIsValid(tender.getTenderId())) {
				LOGGER.debug("Add item to favourite failed for cannot find tender with id: {}", productId);
				rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_PROD_INVALID);
				return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
			}
			// create favourite
			favourite = getUserFavouriteService().convertTenderToFavourite(currentUser.getId().intValue(), tender, favouriteType);
		}
		// check user has already add this item as favourite
		Favourite favouriteCopy = getUserFavouriteService().queryFavouriteByProduct(currentUser.getId().intValue(), productId, favouriteType);
		if (favouriteCopy != null && RepositoryUtils.idIsValid(favouriteCopy.getId())) {
			LOGGER.debug("User had added item: {} of type: {} as favourite: {}", productId, favouriteType, favouriteCopy.getId());
			rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, WARN_FAVOURITE_DUPLICATE);
			return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
		}

		// persist favourite
		TransactionStatus status = ensureTransaction();
		try {
			boolean result = getUserFavouriteService().createFavouriteItem(favourite);
			if (!result) {
				status.setRollbackOnly();
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Create favourite item of user: %d failed for item: %d of type: %d", currentUser.getId(), productId, favouriteType), e);
			status.setRollbackOnly();
		} finally {
			if (status.isRollbackOnly()) {
				LOGGER.debug("Transaction had been set as roll back during add item as favourite for product: {}",
						productId);
				rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_GENERAL_FAVOURITE_FAILED);
			} else {
				rb.setSuccess(true);
				rb.setData(favourite);
			}
			getTransactionManager().commit(status);
		}
		return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dislike")
	@ResponseBody
	public ResponseEntity dislike(HttpServletRequest pRequest, HttpServletResponse pResponse,
	                              @RequestParam(value = "favouriteId", required = true) int favouriteId) {
		ResponseBuilder rb = getResponseBuilderFactory().buildResponseBean().setSuccess(false);
		if (!RepositoryUtils.idIsValid(favouriteId)) {
			LOGGER.debug("Remove favourite with an invalid favourite id: {}", favouriteId);
			rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_FAVOURITE_INVALID);
			return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
		}
		// check user login state
		User currentUser = getCurrentUser(pRequest);
		if (currentUser == null) {
			LOGGER.debug("Current session user has not sign, skip remove favourite.");
			rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_USER_LOGIN_REQUIRED);
			return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
		}
		TransactionStatus status = ensureTransaction();
		try {
			getUserFavouriteService().deleteFavouriteItem(favouriteId);
		} catch (Exception e) {
			LOGGER.error("Remove favourite item of user: {} failed for favourite: {}", currentUser.getId(), favouriteId);
			status.setRollbackOnly();
		} finally {
			if (status.isRollbackOnly()) {
				LOGGER.debug("Transaction had been set as roll back during remove favourite with id: {}",
						favouriteId);
				rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_GENERAL_DISLIKE_FAILED);
			} else {
				rb.setSuccess(true);
			}
			getTransactionManager().commit(status);
		}
		return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
	}

	@RequestMapping(value = "/favourites")
	public ModelAndView favourites(HttpServletRequest pRequest, HttpServletResponse pResponse,
	                               @RequestParam(value = "currentIndex", required = false, defaultValue = "0") String currentIndex,
	                               @RequestParam(value = "capacity", required = false, defaultValue = "5") String capacity,
	                               @RequestParam(value = "keyword", required = false) String keyword,
	                               @RequestParam(value = "asc", required = false, defaultValue = "true") String asc,
	                               @RequestParam(value = "type", required = false, defaultValue = "1") int favouriteType) {
		// check user login state
		User currentUser = getCurrentUser(pRequest);
		if (currentUser == null) {
			LOGGER.debug("Current session user has not sign in, skip load favourite items.");
			return new ModelAndView("redirect:/user/login");
		}
		LOGGER.debug("Query favourite items of user: {}", currentUser.getId());
		long ciLong = RepositoryUtils.safeParse2LongId(currentIndex);
		long caLong = RepositoryUtils.safeParse2LongId(capacity);
		boolean ascBoolean = Boolean.valueOf(asc);
		LOGGER.debug("Query favourite items with index: {}, capacity: {} and keyword: {} with order asc: {}", ciLong, caLong, keyword, ascBoolean);
		InternalPaginationBuilder ipb = new InternalPaginationBuilder().setCurrentIndex(ciLong).setCapacity(caLong).setKeyword(keyword);
		InternalPagination pagination = ipb.setSortFieldName("fav.creation_date").setAsc(ascBoolean).createInternalPagination();
		getUserFavouriteService().queryFavouritePage(currentUser.getId().intValue(), favouriteType, pagination);
		if (favouriteType == FavouriteType.P2P_TENDER && CollectionUtils.isNotEmpty(pagination.getResult())) {
			List<Favourite> tenderFavourites = (List<Favourite>) pagination.getResult();
			tenderFavourites.forEach(pFavourite -> {
				Tender tender = getTenderService().queryTenderById(pFavourite.getProductId(), Boolean.FALSE);
				pFavourite.setTender(tender);
			});
		}

		ModelAndView mav = new ModelAndView("/my-account/favourites");
		mav.addObject(CartConstant.FAVOURITES, pagination);

		// left navigation for p2p
		mav.addObject(Constants.CURRENT_ACCOUNT_ITEM, MyAccountNavigationEnum.MY_FAVORITE);
		return mav;
	}

	@RequestMapping(value = "/ajaxFavourites")
	public ResponseEntity ajaxFavourites(HttpServletRequest pRequest, HttpServletResponse pResponse,
	                                     @RequestParam(value = "currentIndex", required = false, defaultValue = "0") String currentIndex,
	                                     @RequestParam(value = "capacity", required = false, defaultValue = "5") String capacity,
	                                     @RequestParam(value = "keyword", required = false) String keyword,
	                                     @RequestParam(value = "asc", required = false, defaultValue = "true") String asc,
	                                     @RequestParam(value = "type", required = false, defaultValue = "1") int favouriteType) {
		ResponseBuilder rb = getResponseBuilderFactory().buildResponseBean().setSuccess(false);
		// check user login state
		User currentUser = getCurrentUser(pRequest);
		if (currentUser == null) {
			LOGGER.debug("Current session user has not sign in, skip load favourite items.");
			rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_USER_LOGIN_REQUIRED);
			return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
		}
		LOGGER.debug("Query favourite items of user: {}", currentUser.getId());
		long ciLong = RepositoryUtils.safeParse2LongId(currentIndex);
		long caLong = RepositoryUtils.safeParse2LongId(capacity);
		boolean ascBoolean = Boolean.valueOf(asc);
		LOGGER.debug("Query favourite items with index: {}, capacity: {} and keyword: {} with order asc: {}", ciLong, caLong, keyword, ascBoolean);
		InternalPaginationBuilder ipb = new InternalPaginationBuilder().setCurrentIndex(ciLong).setCapacity(caLong).setKeyword(keyword);
		InternalPagination pagination = ipb.setSortFieldName("fav.creation_date").setAsc(ascBoolean).createInternalPagination();
		getUserFavouriteService().queryFavouritePage(currentUser.getId().intValue(), favouriteType, pagination);
		rb.setSuccess(true).setData(pagination);
		return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
	}

	@RequestMapping(value = "/favouriteItemFromCart")
	public ModelAndView favouriteItemFromCart(HttpServletRequest pRequest, HttpServletResponse pResponse,
	                                          @RequestParam(value = "productId", required = true) int productId,
	                                          @RequestParam(value = "type", required = false, defaultValue = "1") int favouriteType) {
		ModelAndView mav = new ModelAndView(getRedirectView(ShoppingCartModifierController.CART));
		// check user login state
		User currentUser = getCurrentUser(pRequest);
		if (currentUser == null) {
			LOGGER.debug("Current session user has not sign, skip favourite item from cart.");
			mav.addObject(CartConstant.ERROR_MSG, getMessageValue(ERROR_USER_LOGIN_REQUIRED, StringUtils.EMPTY));
			return mav;
		}
		// query product
		Product product = getProductServiceImp().queryProduct(productId);
		if (product == null || !RepositoryUtils.idIsValid(product.getProductId())) {
			LOGGER.debug("Favourite item from cart failed for cannot find product with id: {}", productId);
			mav.addObject(CartConstant.ERROR_MSG, getMessageValue(ERROR_PROD_INVALID, StringUtils.EMPTY));
			return mav;
		}
		// check user has already add this item as favourite
		Favourite favourite = getUserFavouriteService().queryFavouriteByProduct(currentUser.getId().intValue(), favouriteType, productId);
		Order order = getCurrentOrder(pRequest, true);
		if (favourite != null && RepositoryUtils.idIsValid(favourite.getId())) {
			LOGGER.debug("User had added product: {} as favourite: {}", productId, favourite.getId());
			mav.addObject(CartConstant.ERROR_MSG, getMessageValue(WARN_FAVOURITE_DUPLICATE, StringUtils.EMPTY));
			removeItemFromOrder(productId, mav, order);
			return mav;
		}
		// create favourite
		favourite = getUserFavouriteService().convertProductToFavourite(currentUser.getId().intValue(), product, favouriteType);
		// check and generate order
		synchronized (order) {
			LOGGER.debug("Synchronized order to add favourite and update order");
			// check order contains commerce item
			CommerceItem purchasedCommerceItem = order.getCommerceItemByProduct(productId);
			if (purchasedCommerceItem == null) {
				LOGGER.debug("Cannot find commerce item with product id: {}", productId);
				mav.addObject(CartConstant.ERROR_MSG, getMessageValue(ERROR_COMMERCE_ITEM_INVALID, StringUtils.EMPTY));
				return mav;
			}
			// remove commerce item
			purchasedCommerceItem = order.removeCommerceItemByProduct(productId);
			// persist changes to database
			TransactionStatus status = ensureTransaction();
			try {
				boolean result = true;
				getPriceOrderService().priceOrder(order);
				// anonymous user could only add item to cart but not persisted
				if (RepositoryUtils.idIsValid(order.getUserId())) {
					if (purchasedCommerceItem != null && RepositoryUtils.idIsValid(purchasedCommerceItem.getId())) {
						result = getShoppingCartService().deleteCommerceItem(purchasedCommerceItem.getId());
					}
					if (result) {
						result = getShoppingCartService().persistInitialOrder(order);
						LOGGER.debug("Persist order with result: {}", result ? "success" : "failed");
						if (!result) {
							status.setRollbackOnly();
							throw new OrderPersistentException("Persist order failed, stop favourite item from cart.");
						}
					}
				}
				// persist favourite
				result = getUserFavouriteService().createFavouriteItem(favourite);
				if (!result) {
					LOGGER.debug("Persist favourite with result: {}", result ? "success" : "failed");
					status.setRollbackOnly();
				}
			} catch (PriceOrderException e) {
				LOGGER.error(String.format("Favourite item from cart with product id: %d failed for price order exception.",
						productId), e);
				status.setRollbackOnly();
			} catch (OrderPersistentException e) {
				LOGGER.error(String.format("Favourite item from with product id: %d failed for persist order exception.",
						productId), e);
				status.setRollbackOnly();
			} catch (Exception e) {
				LOGGER.error(String.format("Favourite item from with product id: %d failed.", productId), e);
				status.setRollbackOnly();
			} finally {
				if (status.isRollbackOnly()) {
					LOGGER.debug("Transaction had been set as roll back during Favourite item from cart for product: {}",
							productId);
					mav.addObject(CartConstant.ERROR_MSG, getMessageValue(ERROR_GENERAL_FAVOURITE_FROM_CART_FAILED, StringUtils.EMPTY));
				}
				getTransactionManager().commit(status);
			}
		}
		return mav;
	}

	private boolean removeItemFromOrder(@RequestParam(value = "productId", required = true) int productId, ModelAndView mav, Order order) {
		synchronized (order) {
			LOGGER.debug("Synchronized order to add favourite and update order");
			// check order contains commerce item
			CommerceItem purchasedCommerceItem = order.getCommerceItemByProduct(productId);
			if (purchasedCommerceItem == null) {
				LOGGER.debug("Cannot find commerce item with product id: {}", productId);
				mav.addObject(CartConstant.ERROR_MSG, getMessageValue(ERROR_COMMERCE_ITEM_INVALID, StringUtils.EMPTY));
				return true;
			}
			// remove commerce item
			purchasedCommerceItem = order.removeCommerceItemByProduct(productId);
			// persist changes to database
			TransactionStatus status = ensureTransaction();
			try {
				boolean result = true;
				getPriceOrderService().priceOrder(order);
				// anonymous user could only add item to cart but not persisted
				if (RepositoryUtils.idIsValid(order.getUserId())) {
					if (purchasedCommerceItem != null && RepositoryUtils.idIsValid(purchasedCommerceItem.getId())) {
						result = getShoppingCartService().deleteCommerceItem(purchasedCommerceItem.getId());
					}
					if (result) {
						result = getShoppingCartService().persistInitialOrder(order);
						LOGGER.debug("Persist order with result: {}", result ? "success" : "failed");
						if (!result) {
							status.setRollbackOnly();
							throw new OrderPersistentException("Persist order failed, stop favourite item from cart.");
						}
					}
				}
			} catch (PriceOrderException e) {
				LOGGER.error(String.format("Favourite item from cart with product id: %d failed for price order exception.",
						productId), e);
				status.setRollbackOnly();
			} catch (OrderPersistentException e) {
				LOGGER.error(String.format("Favourite item from with product id: %d failed for persist order exception.",
						productId), e);
				status.setRollbackOnly();
			} catch (Exception e) {
				LOGGER.error(String.format("Favourite item from with product id: %d failed.", productId), e);
				status.setRollbackOnly();
			} finally {
				if (status.isRollbackOnly()) {
					LOGGER.debug("Transaction had been set as roll back during Favourite item from cart for product: {}",
							productId);
					mav.addObject(CartConstant.ERROR_MSG, getMessageValue(ERROR_GENERAL_FAVOURITE_FROM_CART_FAILED, StringUtils.EMPTY));
				}
				getTransactionManager().commit(status);
			}
		}
		return false;
	}

	@RequestMapping(value = "/ajaxFavouriteItemFromCart")
	@ResponseBody
	public ResponseEntity ajaxFavouriteItemFromCart(HttpServletRequest pRequest, HttpServletResponse pResponse,
	                                                @RequestParam(value = "productId", required = true) int productId,
	                                                @RequestParam(value = "type", required = false, defaultValue = "1") int favouriteType) {
		ResponseBuilder rb = getResponseBuilderFactory().buildResponseBean().setSuccess(false);
		// check user login state
		User currentUser = getCurrentUser(pRequest);
		if (currentUser == null) {
			LOGGER.debug("Current session user has not sign, skip favourite item from cart.");
			rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_USER_LOGIN_REQUIRED);
			return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
		}
		// query product
		Product product = getProductServiceImp().queryProduct(productId);
		if (product == null || !RepositoryUtils.idIsValid(product.getProductId())) {
			LOGGER.debug("Favourite item from cart failed for cannot find product with id: {}", productId);
			rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_PROD_INVALID);
			return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
		}
		if (!getShoppingCartService().checkProductValidity(product)) {
			LOGGER.debug("Favourite item from cart failed for product: {} not available", productId);
			rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_PROD_INVALID);
			return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
		}
		// check user has already add this item as favourite
		Favourite favourite = getUserFavouriteService().queryFavouriteByProduct(currentUser.getId().intValue(), favouriteType, productId);
		if (favourite != null && RepositoryUtils.idIsValid(favourite.getId())) {
			LOGGER.debug("User had added product: {} as favourite: {}", productId, favourite.getId());
			rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, WARN_FAVOURITE_DUPLICATE);
			return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
		}
		// create favourite
		favourite = getUserFavouriteService().convertProductToFavourite(currentUser.getId().intValue(), product, favouriteType);
		// check and generate order
		Order order = getCurrentOrder(pRequest, true);
		synchronized (order) {
			LOGGER.debug("Synchronized order to add favourite and update order");
			// check order contains commerce item
			CommerceItem purchasedCommerceItem = order.getCommerceItemByProduct(productId);
			if (purchasedCommerceItem == null) {
				LOGGER.debug("Cannot find commerce item with product id: {}", productId);
				rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_COMMERCE_ITEM_INVALID);
				return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
			}
			// remove commerce item
			purchasedCommerceItem = order.removeCommerceItemByProduct(productId);
			// persist changes to database
			TransactionStatus status = ensureTransaction();
			try {
				boolean result = true;
				getPriceOrderService().priceOrder(order);
				// anonymous user could only add item to cart but not persisted
				if (RepositoryUtils.idIsValid(order.getUserId())) {
					if (purchasedCommerceItem != null && RepositoryUtils.idIsValid(purchasedCommerceItem.getId())) {
						result = getShoppingCartService().deleteCommerceItem(purchasedCommerceItem.getId());
					}
					if (result) {
						result = getShoppingCartService().persistInitialOrder(order);
						LOGGER.debug("Persist order with result: {}", result ? "success" : "failed");
						if (!result) {
							status.setRollbackOnly();
							throw new OrderPersistentException("Persist order failed, stop favourite item from cart.");
						}
					}
				}
				// persist favourite
				result = getUserFavouriteService().createFavouriteItem(favourite);
				if (!result) {
					LOGGER.debug("Persist favourite with result: {}", result ? "success" : "failed");
					status.setRollbackOnly();
				}
			} catch (PriceOrderException e) {
				LOGGER.error(String.format("Favourite item from cart with product id: %d failed for price order exception.",
						productId), e);
				status.setRollbackOnly();
			} catch (OrderPersistentException e) {
				LOGGER.error(String.format("Favourite item from with product id: %d failed for persist order exception.",
						productId), e);
				status.setRollbackOnly();
			} catch (Exception e) {
				LOGGER.error(String.format("Favourite item from with product id: %d failed.", productId), e);
				status.setRollbackOnly();
			} finally {
				if (status.isRollbackOnly()) {
					LOGGER.debug("Transaction had been set as roll back during Favourite item from cart for product: {}",
							productId);
					rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_GENERAL_FAVOURITE_FROM_CART_FAILED);
				} else {
					rb.setSuccess(true);
					rb.setData(order);
				}
				getTransactionManager().commit(status);
			}
		}
		return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
	}

	@RequestMapping(value = "/ajaxMoveFavouriteToCart")
	@ResponseBody
	public ResponseEntity ajaxMoveFavouriteToCart(HttpServletRequest pRequest, HttpServletResponse pResponse,
	                                              @RequestParam(value = "favouriteId", required = true) int favouriteId) {
		ResponseBuilder rb = getResponseBuilderFactory().buildResponseBean().setSuccess(false);
		if (!RepositoryUtils.idIsValid(favouriteId)) {
			LOGGER.debug("Move favourite to cart with an invalid favourite id: {}", favouriteId);
			rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_FAVOURITE_INVALID);
			return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
		}
		// check user login state
		User currentUser = getCurrentUser(pRequest);
		if (currentUser == null) {
			LOGGER.debug("Current session user has not sign, skip Move favourite item to cart.");
			rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_USER_LOGIN_REQUIRED);
			return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
		}
		// query favourite
		Favourite favourite = getUserFavouriteService().queryFavourite(favouriteId);
		if (favourite == null) {
			LOGGER.debug("Move item to cart failed for cannot find favourite item with id: {}", favouriteId);
			rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_FAVOURITE_INVALID);
			return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
		}
		// check and generate order
		Order order = getCurrentOrder(pRequest, true);
		synchronized (order) {
			LOGGER.debug("Synchronized order to move favourite and update order");
			CommerceItem purchasedCommerceItem = order.getCommerceItemByProduct(favourite.getProductId());
			if (purchasedCommerceItem != null) {
				LOGGER.debug("Stop add item to cart for product: {} because cart item had been added to cart", favourite.getProductId());
				rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_PROD_ADDED_TO_CART);
				return new ResponseEntity(rb, HttpStatus.OK);
			}
			// persist changes to database
			TransactionStatus status = ensureTransaction();
			try {
				getUserFavouriteService().deleteFavouriteItem(favouriteId);
				// check order contains commerce item
				// check product
				Product product = getProductServiceImp().queryProduct(favourite.getProductId());
				if (!getShoppingCartService().checkProductValidity(product)) {
					LOGGER.debug("Stop move item to cart from favourite item for product: {} not available", favourite.getProductId());
					rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_PROD_INVALID);
					status.setRollbackOnly();
				} else {
					if (!getShoppingCartService().purchaseProduct(order, product)) {
						LOGGER.debug("Stop move item from favourite to cart for product: {} out of stock", favourite.getProductId());
						rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, FavouriteMessages.ERROR_PROD_OUT_STOCK);
						return new ResponseEntity(rb, HttpStatus.OK);
					}
					getPriceOrderService().priceOrder(order);
					// anonymous user could only add item to cart but not persisted
					if (RepositoryUtils.idIsValid(order.getUserId())) {
						boolean result = getShoppingCartService().persistInitialOrder(order);
						LOGGER.debug("Persist order with result: {}", result ? "success" : "failed");
						if (!result) {
							status.setRollbackOnly();
							throw new OrderPersistentException("Persist order failed, stop move favourite item to cart.");
						}
					}
				}
			} catch (PriceOrderException e) {
				LOGGER.error(String.format("Move favourite item to cart with product id: %d failed for price order exception.",
						favourite.getProductId()), e);
				status.setRollbackOnly();
			} catch (OrderPersistentException e) {
				LOGGER.error(String.format("Move favourite item to cart with product id: %d failed for persist order exception.",
						favourite.getProductId()), e);
				status.setRollbackOnly();
			} catch (Exception e) {
				LOGGER.error(String.format("Move favourite item to cart with product id: %d failed.", favourite.getProductId()), e);
				status.setRollbackOnly();
			} finally {
				if (status.isRollbackOnly()) {
					LOGGER.debug("Transaction had been set as roll back during move favourite item to cart for product: {}",
							favourite.getProductId());
					rb.addErrorMessage(ResponseBean.DEFAULT_PROPERTY, ERROR_GENERAL_MOVE_FAVOURITE_TO_CART_FAILED);
				} else {
					rb.setSuccess(true);
				}
				getTransactionManager().commit(status);
			}
		}
		return new ResponseEntity(rb.createResponse(), HttpStatus.OK);
	}

	public UserFavouriteService getUserFavouriteService() {
		return mUserFavouriteService;
	}

	public void setUserFavouriteService(final UserFavouriteService pUserFavouriteService) {
		mUserFavouriteService = pUserFavouriteService;
	}

	public ShoppingCartService getShoppingCartService() {
		return mShoppingCartService;
	}

	public void setShoppingCartService(final ShoppingCartService pShoppingCartService) {
		mShoppingCartService = pShoppingCartService;
	}

	public PriceOrderService getPriceOrderService() {
		return mPriceOrderService;
	}

	public void setPriceOrderService(final PriceOrderService pPriceOrderService) {
		mPriceOrderService = pPriceOrderService;
	}

	public ProductServiceImp getProductServiceImp() {
		return mProductServiceImp;
	}

	public void setProductServiceImp(final ProductServiceImp pProductServiceImp) {
		mProductServiceImp = pProductServiceImp;
	}

	public TenderService getTenderService() {
		return mTenderService;
	}

	public void setTenderService(final TenderService pTenderService) {
		mTenderService = pTenderService;
	}

	public ResponseBuilderFactory getResponseBuilderFactory() {
		return mResponseBuilderFactory;
	}

	public void setResponseBuilderFactory(final ResponseBuilderFactory pResponseBuilderFactory) {
		mResponseBuilderFactory = pResponseBuilderFactory;
	}

	public URLMapping getURLMapping() {
		return mURLMapping;
	}

	public void setURLMapping(final URLMapping pURLMapping) {
		mURLMapping = pURLMapping;
	}
}
