package com.pgt.order;

import com.pgt.cart.bean.CommerceItem;
import com.pgt.cart.bean.Order;
import com.pgt.cart.bean.OrderStatus;
import com.pgt.cart.bean.OrderType;
import com.pgt.cart.exception.OrderPersistentException;
import com.pgt.cart.service.OrderService;
import com.pgt.cart.service.ShoppingCartService;
import com.pgt.com.pgt.order.bean.P2PInfo;
import com.pgt.product.bean.Product;
import com.pgt.tender.bean.Tender;
import com.pgt.user.bean.User;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Samli on 2016/1/18.
 */

// TODO log debug
public class P2POrderService extends OrderService {

    public static final long MILLISECOND_ONE_DAY = 1000 * 60 * 60 * 24;

    public static final long DAYS_ONE_YEAR = 365;

    private static final Logger LOGGER = LoggerFactory.getLogger(P2POrderService.class);

    private ShoppingCartService shoppingCartService;

    public Pair<Order, P2PInfo> createP2POrder(User user, Tender tender, List<Product> relatedProducts, String[] productIds, int placeQuantity) throws OrderPersistentException {
        LOGGER.debug("==================== Start method createP2POrder ====================");
        LOGGER.debug("tender : " + tender.toString());
        P2PInfo info = new P2PInfo();
        info.setTenderId(tender.getTenderId());
        info.setPawnShopOwnerId(tender.getPawnShopOwnerId());
        info.setPawnShopId(tender.getPawnShopId());
        info.setPostPeriod(tender.getPostPeriod());
        info.setPrePeriod(tender.getPrePeriod());
        info.setPublishDate(tender.getPublishDate());
        info.setExpectDueDate(tender.getDueDate());
        info.setPlaceQuantity(placeQuantity);
        info.setUnitPrice(tender.getUnitPrice());
        info.setInterestRate(tender.getInterestRate());
        info.setHandlingFeeRate(tender.getHandlingFeeRate());
        calculateP2PInfo(info);
        persistenceP2PInfo(info);
        LOGGER.debug("persistenceP2PInfo infoId: " + info.getId());

        Order order = new Order();
        order.setStatus(OrderStatus.INITIAL);
        order.setType(OrderType.P2P_ORDER);
        order.setUserId(user.getId().intValue());
        order.setStatus(OrderStatus.INITIAL);
        order.setP2pInfoId(info.getId());

        // create commerceItem
        maintainCommerceItem(order, info, relatedProducts, productIds);
//        calculateCommerceItem(order, info);
        calculateOrder(order, info);
        // persistence order
        getShoppingCartService().persistInitialOrder(order);
        LOGGER.debug("==================== end method createP2POrder ====================");
        return Pair.of(order, info);
    }

    private void maintainCommerceItem(Order order, P2PInfo info, List<Product> relatedProducts, String[] productIds) {
        double investTotal = info.getUnitPrice() * info.getPlaceQuantity();
        // TODO ROUND;
        double productTotal = 0D;

        List<CommerceItem> commerceItems = new ArrayList<CommerceItem>();
        int count = 0;
        for (String productId : productIds) {
            int id = Integer.valueOf(productId);
            for (Product relatedProduct : relatedProducts) {
                if (id != relatedProduct.getProductId()) {
                    continue;
                }

                CommerceItem ci = new CommerceItem();
                if (null == relatedProduct.getSalePrice() && null == relatedProduct.getListPrice()) {
                    LOGGER.error("No price for product(id= " + relatedProduct.getProductId() + ").");
                    throw new IllegalArgumentException("NO_PRICE_FOR COMMERCE_ITEM");
                }
                ci.setAmount(relatedProduct.getSalePrice() == null ? relatedProduct.getListPrice() : relatedProduct.getSalePrice());
                ci.setIndex(count);
//                ci.setOrderId(order.getId());
                ci.setListPrice(relatedProduct.getListPrice());
                ci.setName(relatedProduct.getName());
                ci.setQuantity(1);
                ci.setReferenceId(relatedProduct.getProductId());
                ci.setSalePrice(relatedProduct.getSalePrice());
                // TODO Snapshotid
                // ci.setSnapshotId();
                ci.setType(CommerceItem.TYPE_P2P_NOMAL);
                productTotal += ci.getAmount();
                //TODO ROUND
                commerceItems.add(ci);
                count++;
            }
        }
        if (productTotal < investTotal) {
            double moneyOnlyAmount = investTotal - productTotal;
            // TOTO round

            CommerceItem ci = new CommerceItem();
            ci.setAmount(moneyOnlyAmount);
            ci.setIndex(count);
//            ci.setOrderId(order.getId());
            ci.setListPrice(moneyOnlyAmount);
            ci.setName("投资");
            ci.setQuantity(1);
            ci.setReferenceId(-1);
            ci.setSalePrice(moneyOnlyAmount);
            // TODO Snapshotid
            // ci.setSnapshotId();
            ci.setType(CommerceItem.TYPE_P2P_MONEY);
            productTotal += ci.getAmount();
            //TODO ROUND
            commerceItems.add(ci);
        }
        // TODO persistence commerceItem

    }

    private void calculateOrder(Order order, P2PInfo info) {
        List<CommerceItem> commerceItems = order.getCommerceItems();
        double orderTotal = 0D;
        for (CommerceItem item : commerceItems) {
            LOGGER.debug("item id=" + item.getId() + "; item type=" + item.getType() + "; amount=" + item.getAmount());
            orderTotal += item.getAmount();
        }
        // TODO ROUND
        LOGGER.debug("order subtotal=" + orderTotal);
        order.setSubtotal(orderTotal);
        double shippingFee = order.getShippingFee() == null ? 0D : order.getShippingFee();
        LOGGER.debug("order shippingFee=" + shippingFee);
        order.setTotal(order.getSubtotal() + shippingFee);
    }

    private void persistenceP2PInfo(P2PInfo info) {
        // TODO
    }

    private void calculateP2PInfo(P2PInfo info) {
        double incoming = calculateIncoming(info.getPublishDate(), info.getExpectDueDate(), info.getPrePeriod(), info.getUnitPrice(), info.getPlaceQuantity(), info.getInterestRate());
        info.setExpectIncoming(incoming);
        double handlingFee = calculateHandlingFee(info);
        info.setHandlingFee(handlingFee);
    }

    private double calculateHandlingFee(P2PInfo info) {
        if (null == info.getUnitPrice()) {
            LOGGER.error("NO unit price for p2p info.tender(id=" + info.getTenderId() + ")");
            throw new IllegalArgumentException("INVALID.TENDER");
        }
        if (null == info.getHandlingFeeRate()) {
            LOGGER.error("NO handling fee rate for p2p info.tender(id=" + info.getTenderId() + ")");
            throw new IllegalArgumentException("INVALID.TENDER");
        }
        LOGGER.debug("unitPrice=" + info.getUnitPrice()  + "; placeQuantity=" + info.getPlaceQuantity() + "; HandlingFee=" + info.getHandlingFeeRate());
        double total = info.getUnitPrice() * info.getPlaceQuantity();
        double result = total * info.getHandlingFeeRate();
        // TODO ROUND
        LOGGER.debug(" HandlingFee result=" + result);
        return result;
    }

    private double calculateIncoming(Date publishDate, Date expectDueDate, Integer prePeriod, Double unitPrice, int placeQuantity, Double interestRate) {

        boolean hasIllegalArgument = false;
        if (null == publishDate) {
            LOGGER.error("No publish date");
            hasIllegalArgument= true;
        }
        if (null == expectDueDate) {
            LOGGER.error("No expect due date");
            hasIllegalArgument= true;
        }

        if (null == prePeriod) {
            LOGGER.error("No pre period");
            hasIllegalArgument= true;
        }

        if (null == unitPrice) {
            LOGGER.error("No unit price");
            hasIllegalArgument= true;
        }

        if (null == interestRate) {
            LOGGER.error("No interest rate");
            hasIllegalArgument= true;
        }
        if ( hasIllegalArgument) {
            throw new IllegalArgumentException("INVALID.TENDER");
        }
        if (expectDueDate.before(publishDate)) {
            LOGGER.error("expectDueDate before publish date. expectDueDate(" + expectDueDate + "); publishDate(" + publishDate+ ")");
            throw new IllegalArgumentException("INVALID.TENDER");
        }

        LOGGER.debug("publishDate: " + publishDate + "; expectDueDate" + expectDueDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expectDueDate);
        calendar.add(Calendar.DATE, 1);

        // TODO date start time
        int dateGap = (int) ((calendar.getTimeInMillis() - publishDate.getTime()) / MILLISECOND_ONE_DAY);
        if (dateGap < prePeriod) {
            LOGGER.error("dateGap < prePeriod. dateGap(" + dateGap +"); prePeriod(" + prePeriod + "); expectDueDate(" + expectDueDate + "); publishDate(" + publishDate+ ")");
            throw new IllegalArgumentException("INVALID.TENDER");
        }
        LOGGER.debug("unitPrice=" + unitPrice  + "; placeQuantity=" + placeQuantity + "; interestRate=" + interestRate);
        double total = unitPrice * placeQuantity * ( 1 + interestRate);
        int effectDates = dateGap - prePeriod;
        LOGGER.debug("total=" + total  + "; dateGap=" + dateGap + "; prePeriod=" + prePeriod + "; effectDates=" + effectDates );
        double result = total * effectDates / DAYS_ONE_YEAR;
        // TODO ROUND
        LOGGER.debug("Incoming result=" + result );
        return result;
    }


    public ShoppingCartService getShoppingCartService() {
        return shoppingCartService;
    }

    public void setShoppingCartService(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }
}