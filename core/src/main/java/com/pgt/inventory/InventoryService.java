package com.pgt.inventory;

import com.pgt.cart.bean.CommerceItem;
import com.pgt.cart.bean.Order;
import com.pgt.product.bean.InventoryType;
import com.pgt.utils.Transactionable;

import java.util.*;

/**
 * Created by samli on 2015/12/20.
 */
public class InventoryService extends Transactionable {



    public void lockInventory(final Order order) {
        if (null == order) {
            throw new IllegalArgumentException("order is null");
        }

        List<CommerceItem> commerceItems = order.getCommerceItems();
        if (null == commerceItems || commerceItems.isEmpty()) {
            return;
        }
        List<Integer> productIds = new ArrayList<Integer>();
        for (CommerceItem commerceItem : commerceItems) {
            productIds.add(commerceItem.getReferenceId());
        }
        Collections.sort(productIds);
        Date now = new Date();
        ensureTransaction();
        try {
            acquireRowLock(productIds);
            Map<Integer, InventoryLock> existLocks = findExistLock(order.getId());
            removeNonExistLock(existLocks, productIds);
            lockInventory(order, existLocks, now);
        } catch (Exception e) {
            setAsRollback();
            // TODO: THROW WRAPPED EXCEPTION
        } finally {
            commit();
        }
    }

    /**
     * 1. if inventory lock is already exist. maintain quantity and expire date.
     * 2. if inventory lock is not exist. create a new one.
     *
     * @param order
     * @param existLocks
     * @param now
     */
    private void lockInventory(final Order order, final Map<Integer, InventoryLock> existLocks, final Date now) {
        boolean allLocked = true;
        Set<Integer> oosProductIds = new HashSet<Integer>();
        for (CommerceItem commerceItem : order.getCommerceItems()) {
            Integer productId = Integer.valueOf(commerceItem.getReferenceId());
            int quantityLeft = queryInventoryQuantiry(productId);
            int quantityRequire = commerceItem.getQuantity();

            if (null == existLocks || null == existLocks.get(productId)) {
                // create a new inventory lock
                if (quantityLeft < quantityRequire) {
                    allLocked = false;
                    oosProductIds.add(productId);
                    continue;
                }
                InventoryLock newLock = new InventoryLock();
                newLock.setOrderId(Long.valueOf(order.getId()));
                newLock.setProdudctId(Long.valueOf(productId));
                newLock.setQuantity(quantityRequire);
                newLock.setCreationDate(now);
                newLock.setUpdateDate(now);
                // TODO: set expire Date base on order status
                // newLock.setExpiredDate();
                createInventoryLock(newLock);
                changeInventory(productId, quantityRequire, InventoryType.CUT_DOWN);

                continue;
            }

            InventoryLock existLock = existLocks.get(Integer.valueOf(productId));

            int quantityLocked = existLock.getQuantity();

            if (quantityLocked == quantityRequire) {
                // TODO: set expire Date base on order status
                // existLock.setExpiredDate();
                updateInventoryLock(existLock);
                continue;
            }
            if (quantityLocked > quantityRequire) {
                // release from lock and increase inventory
                existLock.setQuantity(quantityRequire);
                // TODO: set expire Date base on order status
                // existLock.setExpiredDate();
                updateInventoryLock(existLock);
                changeInventory(productId, quantityLocked - quantityRequire, InventoryType.ADD);
                continue;
            }
            if (quantityLeft < (quantityRequire - quantityLocked)) {
                allLocked = false;
                oosProductIds.add(productId);
                continue;
            }
            // deduct inventory
            existLock.setQuantity(quantityRequire);
            // TODO: set expire Date base on order status
            // existLock.setExpiredDate();
            updateInventoryLock(existLock);
            changeInventory(productId, quantityRequire - quantityLocked, InventoryType.CUT_DOWN);
        }
        if (!allLocked) {
            // TODO throw check exception (InventoryException)
        }

    }

    /**
     *  1. remove non-exist commerce item inventory lock
     *  2. increase inventory quantity.
     *
     * @param existLocks
     * @param productIds
     */
    private void removeNonExistLock(final Map<Integer, InventoryLock> existLocks, final List<Integer> productIds) {
        if (null == existLocks || existLocks.isEmpty()) {
            return ;
        }

        for (Integer productId : existLocks.keySet()) {
            if (!productIds.contains(productId)) {
                InventoryLock nonExistLock = existLocks.get(productId);
                if (null == nonExistLock) {
                    continue;
                }
                int quantity = nonExistLock.getQuantity();
                deleteInventoryLog(nonExistLock);
                changeInventory(productId, quantity, InventoryType.ADD);

            }
        }
    }

    private void updateInventoryLock(InventoryLock existLock) {
    }

    private void createInventoryLock(InventoryLock newLock) {

    }

    private int queryInventoryQuantiry(final int productId) {
        // TODO: implement
        return 0;
    }

    private void deleteInventoryLog(InventoryLock nonExistLock) {
        
    }

    private void changeInventory(Integer productId, int quantity, InventoryType add) {
        // TODO increase inventory in DB

        // TODO increase inventory in index
    }

    /**
     * Find already exist inventory lock in DB.
     * @param orderId
     * @return
     */
    private Map<Integer, InventoryLock> findExistLock(final int orderId) {
        // TODO: implement
        return null;
    }


    private void acquireRowLock(final List<Integer> productIds) {
        // TODO: implement
    }
}
