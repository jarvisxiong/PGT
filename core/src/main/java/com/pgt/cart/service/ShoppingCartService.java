package com.pgt.cart.service;

import com.pgt.cart.bean.*;
import com.pgt.cart.dao.ShoppingCartDao;
import com.pgt.cart.exception.OrderPersistentException;
import com.pgt.cart.util.RepositoryUtils;
import com.pgt.common.bean.Media;
import com.pgt.product.bean.Product;
import com.pgt.product.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yove on 10/27/2015.
 */
@Service(value = "shoppingCartService")
public class ShoppingCartService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCartService.class);

	@Resource(name = "shoppingCartDao")
	private ShoppingCartDao mShoppingCartDao;

	@Resource(name = "priceOrderService")
	private PriceOrderService mPriceOrderService;

	@Autowired
	private ProductService mProductService;

	private int mMaxItemCount4Cart = 2;

	@Autowired
	private DataSourceTransactionManager mTransactionManager;

	public Order loadInitialOrder(final int pUserId) {
		Order order = getShoppingCartDao().loadInitialOrderByUserId(pUserId);
		if (order != null && RepositoryUtils.idIsValid(order.getId())) {
			List<CommerceItem> commerceItems = getShoppingCartDao()
					.loadCommerceItemsFromOrderWithRealTimePrice(order.getId());
			order.setCommerceItems(commerceItems);
		}
		return order;
	}

	public Order mergeOrder(final Order pSourceOrder, final Order pDestinationOrder) {
		if (pSourceOrder.emptyOrder()) {
			return pDestinationOrder;
		}
		pDestinationOrder.getCommerceItems().addAll(pSourceOrder.getCommerceItems());
		pDestinationOrder.resetCommerceItemIndex();
		pDestinationOrder.getCommerceItems().forEach(ci -> {
			ci.setOrderId(pDestinationOrder.getId());
		});
		return pDestinationOrder;
	}

	public boolean persistInitialOrder(final Order pOrder) throws OrderPersistentException {
		synchronized (pOrder) {
			// check order ready to persist
			if (pOrder == null || pOrder.getStatus() != (OrderStatus.INITIAL)) {
				LOGGER.debug("Cannot update initial order for null order or not an initial order.");
				return false;
			}
			if (!RepositoryUtils.idIsValid(pOrder.getUserId())) {
				LOGGER.debug("Cannot update initial order invalid user id: {}", pOrder.getUserId());
				return false;
			}
			// start transaction
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			TransactionStatus status = getTransactionManager().getTransaction(def);
			boolean result;
			try {
				if (RepositoryUtils.idIsValid(pOrder.getId())) {
					result = getShoppingCartDao().updateOrder(pOrder) > 0;
				} else {
					result = getShoppingCartDao().createOrder(pOrder) > 0;
				}
				// persist transient commerce items first
				if (result) {
					List<CommerceItem> transientItems = pOrder.getTransientCommerceItems();
					if (!CollectionUtils.isEmpty(transientItems)) {
						for (CommerceItem ci : transientItems) {
							ci.setOrderId(pOrder.getId());
							result &= getShoppingCartDao().createCommerceItem(ci) > 0;
						}
					}
				}
				// update commerce items
				if (result) {
					List<CommerceItem> persistentItems = pOrder.getPersistedCommerceItems();
					if (!CollectionUtils.isEmpty(persistentItems)) {
						persistentItems.forEach(ci -> {
							ci.setOrderId(pOrder.getId());
						});
						result = getShoppingCartDao().updateCommerceItems(persistentItems) > 0;
					}
				}
				// check result to set roll back
				if (!result) {
					status.setRollbackOnly();
				}
			} catch (Exception e) {
				String msg = String.format("Update initial order: %d of user: %d failed", pOrder.getId(),
						pOrder.getUserId());
				LOGGER.error(msg, e);
				throw new OrderPersistentException(msg, e);
			} finally {
				getTransactionManager().commit(status);
				return !status.isRollbackOnly();
			}
		}
	}

	public CommerceItemBuilder convertProductToCommerceItemBuilder(final Product pProduct) {
		CommerceItemBuilder cib = new CommerceItemBuilder().setName(pProduct.getName()).setQuality(pProduct.getIsNew())
				.setReferenceId(pProduct.getProductId()).setMerchant(pProduct.getMerchant());
		return cib.setListPrice(pProduct.getListPrice()).setSalePrice(pProduct.getSalePrice());
	}

	public boolean deleteCommerceItem(final int pCommerceItemId) {
		return getShoppingCartDao().deleteCommerceItem(pCommerceItemId) > 0;
	}

	public boolean purchaseProduct(Order pOrder, Product pProduct) {
		int productId = pProduct.getProductId();
		// check existence of commerce item that wrapped same product
		CommerceItem purchaseCommerceItem = pOrder.getCommerceItemByProduct(productId);
		if (purchaseCommerceItem == null) {
			// check inventory
			if (pProduct.getStock() == 0) {
				LOGGER.debug("Cannot get product: {} to add item to cart for no inventory", productId);
				return false;
			}
			LOGGER.debug("Cannot find commerce item with product: {} in cart, try to create new commerce item",
					productId);
			// convert product to commerce item
			CommerceItemBuilder cib = convertProductToCommerceItemBuilder(pProduct);
			// set index
			cib.setIndex(pOrder.getCommerceItems().size());
			// set snapshot
			Media snapshotMedia = null;
			if (!ObjectUtils.isEmpty(pProduct.getThumbnailMedia())) {
				snapshotMedia = pProduct.getThumbnailMedia();
				cib.setSnapshotId(snapshotMedia.getId());
			}
			// set order id
			if (RepositoryUtils.idIsValid(pOrder.getId())) {
				cib.setOrderId(pOrder.getId());
			}
			// set quantity
			cib.setQuantity(1);
			// generate commerce item
			purchaseCommerceItem = cib.createCommerceItem();
			if (snapshotMedia != null) {
				purchaseCommerceItem.setSnapshotMedia(snapshotMedia);
			}
			pOrder.getCommerceItems().add(purchaseCommerceItem);
			LOGGER.debug("New created commerce item with product: {} had been add to cart object", productId);
		} else {
			// check inventory
			if (pProduct.getStock() != -1 && purchaseCommerceItem.getQuantity() + 1 > pProduct.getStock()) {
				LOGGER.debug("Cannot get product: {} to add item to cart for inventory limit", productId);
				return false;
			}
			purchaseCommerceItem.setQuantity(purchaseCommerceItem.getQuantity() + 1);
			LOGGER.debug("Commerce item with product: {} had been updated in cart object", productId);
		}
		return true;
	}

	public boolean checkProductValidity(Product pProduct) {
		if (pProduct == null) {
			LOGGER.debug("Cannot find product.");
			return false;
		}
		if (pProduct.getStatus() == Product.INVALID) {
			LOGGER.debug("Invalid status product: {}", pProduct.getProductId());
			return false;
		}
		if (pProduct.getStock() == 0) {
			LOGGER.debug("No available stock for product: {}", pProduct.getProductId());
			return false;
		}
		return true;
	}

	public boolean checkCartItemCount(Order pOrder) {
		return pOrder.getCommerceItemCount() <= getMaxItemCount4Cart();
	}

	public boolean ensureCartItemCapacity(Order pOrder) {
		return pOrder.getCommerceItemCount() < getMaxItemCount4Cart();
	}

	public void updateOrder(Order pOrder) {
		getShoppingCartDao().updateOrder(pOrder);
	}

	public boolean deleteCommerceItems(final List<Integer> pCommerceItemIds) {
		return getShoppingCartDao().deleteCommerceItems(pCommerceItemIds) > 0;
	}

	public void checkInventory(Order pOrder) {
		if (pOrder.emptyOrder()) {
			return;
		}
		Set<Integer> productIds = pOrder.populateProductIdSet();
		Map<Integer, ProductInventoryVector> productInvMap = getShoppingCartDao().loadProductInventory(productIds.toArray(new Integer[0]));
		for (CommerceItem ci : pOrder.getCommerceItems()) {
			ProductInventoryVector pi = productInvMap.get(ci.getReferenceId());
			if (pi == null) {
				// no related product found
				ci.setInStock(false);
				continue;
			}
			ci.setInStock(pi.getStock() != 0);
		}
	}

	public String convertProductIdsToProductNames(String pProductIds, String pSplit) {
		String[] prodIds = pProductIds.split(pSplit);
		String productNames = StringUtils.EMPTY;
		int count = 0;
		for (String prodId : prodIds) {
			count++;
			Product product = getProductService().queryProduct(Integer.valueOf(prodId));
			if (null == product) {
				continue;
			}
			productNames += product.getName();
			if (count != prodIds.length) {
				productNames += ",";
			}
		}
		return productNames;
	}

	public void deleteAllCommerceItems(final int pOrderId) {
		getShoppingCartDao().deleteAllCommerceItems(pOrderId);
	}

	public ShoppingCartDao getShoppingCartDao() {
		return mShoppingCartDao;
	}

	public void setShoppingCartDao(ShoppingCartDao pShoppingCartDao) {
		mShoppingCartDao = pShoppingCartDao;
	}

	public PriceOrderService getPriceOrderService() {
		return mPriceOrderService;
	}

	public void setPriceOrderService(PriceOrderService pPriceOrderService) {
		mPriceOrderService = pPriceOrderService;
	}

	public ProductService getProductService() {
		return mProductService;
	}

	public void setProductService(ProductService pProductService) {
		mProductService = pProductService;
	}

	public DataSourceTransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	public void setTransactionManager(DataSourceTransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}

	public int getMaxItemCount4Cart() {
		return mMaxItemCount4Cart;
	}

	public void setMaxItemCount4Cart(final int pMaxItemCount4Cart) {
		mMaxItemCount4Cart = pMaxItemCount4Cart;
	}
}
