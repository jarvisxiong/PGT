package com.pgt.cart.bean;

/**
 * Created by Yove on 10/27/2015.
 */
public interface OrderStatus {

    int INITIAL                   = 10;
    int FILLED_SHIPPING           = 20;
    int START_PAY                 = 25;
    int PAID                      = 30;
    int PENDING_SHIPPING          = 50;
    int PENDING_TRANSFER_TO_BUYER = 60;
    int TRANSIT                   = 80;
    int NO_PENDING_ACTION         = 100;
    int CANCEL                    = -10;
    int TRANSFER_TO_OWNER_FAILD   = -20;
    int TRANSFER_TO_BUYER_FAILD   = -30;
}
