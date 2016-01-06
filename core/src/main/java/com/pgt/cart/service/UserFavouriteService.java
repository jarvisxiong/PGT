package com.pgt.cart.service;

import com.pgt.cart.bean.Favourite;
import com.pgt.cart.bean.FavouriteBuilder;
import com.pgt.cart.bean.pagination.InternalPagination;
import com.pgt.cart.dao.UserFavouriteDao;
import com.pgt.product.bean.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yove on 11/12/2015.
 */
@Service("userFavouriteService")
public class UserFavouriteService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserFavouriteService.class);

	@Resource(name = "userFavouriteDao")
	private UserFavouriteDao mUserFavouriteDao;

	public List<Favourite> queryFavouritePage(final int pUserId, final InternalPagination pPagination) {
		long count = getUserFavouriteDao().queryFavouriteCount(pUserId, pPagination);
		pPagination.setCount(count);
		LOGGER.debug("Get favourite item of count: {} with keyword: {} for user: {}", count, pPagination.getKeyword(), pUserId);
		if (count > 0) {
			List<Favourite> favourites = getUserFavouriteDao().queryFavouritePage(pUserId, pPagination);
			pPagination.setResult(favourites);
		} else {
			pPagination.setResult(Collections.EMPTY_LIST);
		}
		return (List<Favourite>) pPagination.getResult();
	}

	public Favourite queryFavouriteByProduct(final int pUserId, final int pProductId) {
		return getUserFavouriteDao().queryFavouriteByProduct(pUserId, pProductId);
	}

	public List<Favourite> queryFavourites(final int pUserId) {
		return getUserFavouriteDao().queryFavourites(pUserId);
	}

	public Favourite queryFavourite(final int pFavouriteId) {
		return getUserFavouriteDao().queryFavourite(pFavouriteId);
	}

	public boolean createFavouriteItem(final Favourite pFavourite) {
		return getUserFavouriteDao().createFavouriteItem(pFavourite) > 0;
	}

	public boolean deleteFavouriteItem(final int pFavouriteId) {
		return getUserFavouriteDao().deleteFavouriteItem(pFavouriteId) > 0;
	}

	public Favourite convertProductToFavourite(int pUserId, Product pProduct) {
		FavouriteBuilder fb = new FavouriteBuilder().setUserId(pUserId);
		fb.setProductId(pProduct.getProductId()).setName(pProduct.getName()).setDescription(pProduct.getDescription());
		// set final price
		if (pProduct.getSalePrice() != null && pProduct.getSalePrice().doubleValue() > 0d) {
			fb.setFinalPrice(pProduct.getSalePrice().doubleValue());
		} else if (pProduct.getListPrice() != null) {
			fb.setFinalPrice(pProduct.getListPrice().doubleValue());
		} else {
			fb.setFinalPrice(0d);
		}
		if (!ObjectUtils.isEmpty(pProduct.getFrontMedia())) {
			fb.setSnapshotId(pProduct.getFrontMedia().getId());
		}
		return fb.createFavourite();
	}

	public UserFavouriteDao getUserFavouriteDao() {
		return mUserFavouriteDao;
	}

	public void setUserFavouriteDao(final UserFavouriteDao pUserFavouriteDao) {
		mUserFavouriteDao = pUserFavouriteDao;
	}
}
