package com.pgt.checkout.controller;

import com.pgt.address.bean.AddressInfo;
import com.pgt.address.service.AddressInfoService;
import com.pgt.cart.bean.Order;
import com.pgt.cart.bean.OrderStatus;
import com.pgt.cart.bean.OrderType;
import com.pgt.cart.constant.CartConstant;
import com.pgt.cart.controller.CartMessages;
import com.pgt.cart.exception.OrderPersistentException;
import com.pgt.city.bean.Province;
import com.pgt.city.service.CityService;
import com.pgt.com.pgt.order.bean.P2PInfo;
import com.pgt.configuration.URLConfiguration;
import com.pgt.constant.UserConstant;
import com.pgt.inventory.LockInventoryException;
import com.pgt.inventory.service.InventoryService;
import com.pgt.inventory.service.TenderInventoryService;
import com.pgt.mail.MailConstants;
import com.pgt.mail.service.MailService;
import com.pgt.order.P2POrderService;
import com.pgt.payment.PaymentConstants;
import com.pgt.product.bean.Product;
import com.pgt.session.SessionHelper;
import com.pgt.shipping.bean.ShippingVO;
import com.pgt.shipping.service.ShippingService;
import com.pgt.tender.bean.Tender;
import com.pgt.tender.service.TenderService;
import com.pgt.user.bean.User;
import com.pgt.user.bean.UserInformation;
import com.pgt.user.service.UserInformationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Samli on 2016/1/16.
 */

@RestController
@RequestMapping("/checkout")
public class P2PCheckoutController {
    private static final int NO_ERROR = 0;

    private static final int NO_PRODUCT_IDS = 1;

    private static final int NO_QUANTITIES = 2;

    private static final int ID_QUANTITY_NOT_MATCH = 3;

    private static final int BLANK_PRODUCT_ID = 4;

    private static final int BLANK_QUANTITY = 5;

    private static final int PRODUCT_NOT_EXIST = 6;

    private static final int INVEST_TOTAL_NOT_ENOUGH = 7;

    private static final Logger LOGGER = LoggerFactory.getLogger(P2PCheckoutController.class);

    @Resource(name = "p2pOrderService")
    private P2POrderService orderService;

    @Autowired
    private TenderInventoryService tenderInventoryService;

    @Autowired
    private TenderService tenderService;

    @Autowired
    private URLConfiguration urlConfiguration;

    @Resource(name = "inventoryService")
    private InventoryService inventoryService;

    @Autowired
    private MailService mailService;
    @Autowired
    private UserInformationService userInformationService;

    @Autowired
    private ShippingService shippingService;

    @Autowired
    private AddressInfoService addressInfoService;
    @Autowired
    private CityService cityService;

//    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @RequestMapping(value = "/create")
    public ModelAndView createOrder(HttpServletRequest pRequest, HttpServletResponse pResponse) {
        User user = SessionHelper.getUser(pRequest, pResponse);
        LOGGER.debug("============= P2PCheckoutController#createOrder start =============");
        if (null == user) {
            ModelAndView modelAndView = new ModelAndView("redirect:" + getUrlConfiguration().getLoginPage());
            LOGGER.debug("no user redirect to login page");
            LOGGER.debug("============= P2PCheckoutController#createOrder end =============");
            return modelAndView;
        }
        String tenderIdStr = pRequest.getParameter("tenderId");
        String[] productIds = pRequest.getParameterValues("productIds");
        String[] quantities = pRequest.getParameterValues("quantities");
        if (StringUtils.isBlank(tenderIdStr) || !StringUtils.isNumeric(tenderIdStr)) {
            ModelAndView modelAndView = new ModelAndView("redirect:" + getUrlConfiguration().getHomePage());
            LOGGER.debug("tenderId is not a number, redirect to home page");
            LOGGER.debug("============= P2PCheckoutController#createOrder end =============");
            return modelAndView;
        }
        int tenderId = Integer.valueOf(tenderIdStr);

        // check if has not pay order
        if (getOrderService().hasUncompleteOrder(user.getId().intValue(), OrderType.P2P_ORDER, 0)) {
            LOGGER.debug("user has incomplete order redirect to tender page");
            LOGGER.debug("============= P2PCheckoutController#createOrder end =============");
            // TODO redirect to tendId
            ModelAndView modelAndView = new ModelAndView("redirect:/tender/" + tenderIdStr);
            return modelAndView;
        }
        getTenderInventoryService().ensureTransaction();

        Tender tender = getOrderService().queryTenderById(tenderId);
        List<Product> relatedProducts = getTenderService().queryTenderProduct(tenderId);

        // check productIds is valid
        int errorCode = isProductIdsValid(productIds, quantities, relatedProducts, tender);
        if (NO_ERROR != errorCode) {
            LOGGER.debug("product parameter is not correct redirect to tender page");
            LOGGER.debug("============= P2PCheckoutController#createOrder end =============");
            getTenderInventoryService().setAsRollback();
            // TODO redirect to tendId
            ModelAndView modelAndView = new ModelAndView("redirect:/tender/" + tenderIdStr);
            return modelAndView;
        }
        // create order
        try {
            Pair<Order, P2PInfo> result = getOrderService().createP2POrder(user, tender, relatedProducts, productIds, quantities);
            Order order = result.getLeft();

            // check inventory
            getTenderInventoryService().lockInventory(order);
            ModelAndView modelAndView = new ModelAndView("redirect:/checkout/shipping");
            modelAndView.addObject(CartConstant.ORDER_ID, order.getId());
            return modelAndView;
        } catch (OrderPersistentException | LockInventoryException ex) {
            LOGGER.error(ex.getMessage(), ex);
            getTenderInventoryService().setAsRollback();
            LOGGER.debug("============= P2PCheckoutController#createOrder end =============");
            ModelAndView modelAndView = new ModelAndView("redirect:/tender/" + tenderIdStr);
            return modelAndView;
        } finally {
            getTenderInventoryService().commit();
        }
    }

    @RequestMapping(value = "/shipping")
    public ModelAndView shippingPage(ModelAndView modelAndView, HttpServletRequest pRequest, HttpServletResponse pResponse) {
        String orderIdStr = pRequest.getParameter(CartConstant.ORDER_ID);
        User user = SessionHelper.getUser(pRequest, pResponse);
        if (StringUtils.isBlank(orderIdStr) || !StringUtils.isNumeric(orderIdStr)) {
            modelAndView = new ModelAndView("redirect:" + getUrlConfiguration().getHomePage());
            return modelAndView;
        }
        if (null == user) {
            modelAndView = new ModelAndView("redirect:" + getUrlConfiguration().getHomePage());
            return modelAndView;
        }

        Order order = getOrderService().loadOrder(Integer.valueOf(orderIdStr));
        if (null == order) {
            modelAndView = new ModelAndView("redirect:" + getUrlConfiguration().getHomePage());
            return modelAndView;
        }
        if (order.getUserId() != user.getId().intValue()) {
            modelAndView = new ModelAndView("redirect:" + getUrlConfiguration().getHomePage());
            return modelAndView;
        }
        modelAndView = new ModelAndView();
        if (order.getShippingVO() == null) {
            ShippingVO shipping = getShippingService().findShippingByOrderId(String.valueOf(order.getId()));
            order.setShippingVO(shipping);
        }
        List<AddressInfo> addressInfoList = getAddressInfoService().queryAddressByUserId(user.getId().intValue());
        if (user.getDefaultAddressId() != null) {
            addressInfoList = getAddressInfoService().sortAddress(user.getDefaultAddressId(), addressInfoList);
            AddressInfo defaultAddress = getAddressInfoService().findAddress(user.getDefaultAddressId());
            modelAndView.addObject("defaultAddress", defaultAddress);
            if (order.getShippingVO() == null) {
                getShippingService().addAddress(user.getDefaultAddressId(), order);
            }
        }
        P2PInfo info = getOrderService().queryP2PInfoByOrderId(order.getId());
        // TODO SHIPPING PAGE
        List<Province> provinces = cityService.getAllProvince();
        modelAndView.addObject(CartConstant.ADDRESS_INFO_LIST, addressInfoList);
        modelAndView.addObject(CartConstant.PROVINCES_LIST, provinces);
        modelAndView.addObject(CartConstant.ORDER, order);
        modelAndView.addObject(CartConstant.P2P_INFO, info);
        modelAndView.setViewName("/checkout/shipping");
        return modelAndView;
    }


    @RequestMapping(value = "/addAddressToOrder", method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> addAddressToOrder(@RequestParam("addressInfoId") String addressInfoId,
                                                 HttpServletRequest request, HttpSession session) {
        Map<String, Object> map = new HashMap<String, Object>();
        User user = (User) session.getAttribute(UserConstant.CURRENT_USER);
        Order order = getOrderService().getSessionOrder(request);
        if (getOrderService().isInvalidOrder(user, order)) {
            LOGGER.error("Falied to add address to order-{}.", request.getParameter(CartConstant.ORDER_ID));
            map.put("redirectUrl", urlConfiguration.getShoppingCartPage());
            map.put("success", "false");
            return map;
        }
        if (StringUtils.isBlank(addressInfoId)) {
            LOGGER.error("Need parameter 'addressInfoId' when adding address to order-{}.", order.getId());
            map.put("success", "false");
            return map;
        }
        getShippingService().addAddress(Integer.parseInt(addressInfoId), order);
        map.put("success", "true");
        return map;
    }


    @RequestMapping(value = "/review")
    public ModelAndView orderReview(String comments, HttpServletRequest pRequest, HttpServletResponse pResponse) {
        String orderIdStr = pRequest.getParameter(CartConstant.ORDER_ID);
        ModelAndView modelAndView ;
        User user = SessionHelper.getUser(pRequest, pResponse);
        if (StringUtils.isBlank(orderIdStr) || !StringUtils.isNumeric(orderIdStr)) {
            modelAndView = new ModelAndView("redirect:" + getUrlConfiguration().getHomePage());
            return modelAndView;
        }
        if (null == user) {
            modelAndView = new ModelAndView("redirect:" + getUrlConfiguration().getHomePage());
            return modelAndView;
        }
        Order order = getOrderService().loadOrder(Integer.valueOf(orderIdStr));
        order.setUserComments(comments);
        getOrderService().updateOrder(order);
        if (null == order) {
            modelAndView = new ModelAndView("redirect:" + getUrlConfiguration().getHomePage());
            return modelAndView;
        }
        if (order.getUserId() != user.getId().intValue()) {
            modelAndView = new ModelAndView("redirect:" + getUrlConfiguration().getHomePage());
            return modelAndView;
        }
        P2PInfo info = getOrderService().queryP2PInfoByOrderId(order.getId());
        ShippingVO shipping = shippingService.findShippingByOrderId(String.valueOf(order.getId()));
        order.setShippingVO(shipping);
        modelAndView = new ModelAndView("/checkout/review");
        modelAndView.addObject(CartConstant.ORDER, order);
        modelAndView.addObject(CartConstant.P2P_INFO, info);
        return modelAndView;
    }


    @RequestMapping(value = "/redirectToPayment", method = RequestMethod.GET)
    public ModelAndView redirectToPayment(HttpServletRequest request, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        String method = request.getParameter("method");
        User user = (User) session.getAttribute(UserConstant.CURRENT_USER);
        if (null == user) {
            modelAndView.setViewName("redirect:" + urlConfiguration.getLoginPage());
            return modelAndView;
        }
        Order order = getOrderService().getSessionOrder(request);
        if (null == order) {
            modelAndView.setViewName("redirect:" + urlConfiguration.getHomePage());
            return modelAndView;
        }

        if (getOrderService().hasUncompleteOrder(user.getId().intValue(), OrderType.P2P_ORDER, order.getId())) {
            // TODO REDIRECT TO MY ORDERS
            modelAndView.setViewName("redirect:" + urlConfiguration.getShippingPage());
            modelAndView.addObject(CartConstant.ORDER_ID, order.getId());
            modelAndView.addObject("error", "HAS.UNSUBMIT.ORDER");
            return modelAndView;
        }

        if (getOrderService().isInvalidOrder(user, order)) {
            LOGGER.error("Current order is invalid and will redirect to shopping cart.");
            modelAndView.setViewName("redirect:" + urlConfiguration.getShoppingCartPage());
            return modelAndView;
        }

        // TODO CHECK SHIPPING
        getInventoryService().ensureTransaction();
        try {
            getInventoryService().lockInventory(order);
            order.setStatus(OrderStatus.FILLED_SHIPPING);
            order.setSubmitDate(new Date());
            if (null != order.getShippingVO() && null != order.getShippingVO().getShippingAddress()) {
                String alias = order.getShippingVO().getShippingAddress().getName();
                order.setHolderAlias(alias);
            }
            getOrderService().updateOrder(order);
        } catch (LockInventoryException e) {
            String oosProdId = StringUtils.join(e.getOosProductIds(), "_");

            // TODO goto my orders page
            modelAndView.setViewName("redirect:" + urlConfiguration.getShoppingCartPage() + "?oosProdId=" + oosProdId);

            return modelAndView;
        } catch (Exception e) {
            String message = CartMessages.ERROR_INV_CHECK_FAILED;
            LOGGER.error("lock inventory failed", e);
            // TODO goto my orders page
            modelAndView.setViewName("redirect:" + urlConfiguration.getShoppingCartPage() + "?error=" + message);
            return modelAndView;
        } finally {
            getInventoryService().commit();
        }
        sendEmail(user, order);
        request.removeAttribute(CartConstant.CURRENT_ORDER);

        //choose the method and go to associate page to pay.
        if (PaymentConstants.METHOD_YEEPAY.equals(method)) {
            modelAndView.setViewName("redirect:/yeepay/yeepayB2cPay?orderId=" + order.getId());
            return modelAndView;
        } else if (PaymentConstants.METHOD_ALIPAY.equals(method)) {
            modelAndView.setViewName("redirect:/alipay/request?orderId=" + order.getId());
            return modelAndView;
        }


        modelAndView.setViewName("redirect:/checkout/payment");
        modelAndView.addObject(CartConstant.ORDER_ID, order.getId());
        return modelAndView;
    }


    @RequestMapping(value = "/payment", method = RequestMethod.GET)
    public ModelAndView paymentPage(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        User user = (User) session.getAttribute(UserConstant.CURRENT_USER);
        if (null == user) {
            mav.setViewName("redirect:" + urlConfiguration.getLoginPage());
            return mav;
        }
        Order order = getOrderService().getSessionOrder(request);
        if (null == order) {
            mav.setViewName("redirect:" + urlConfiguration.getHomePage());
            return mav;
        }
        ShippingVO shipping = getShippingService().findShippingByOrderId(String.valueOf(order.getId()));
        order.setShippingVO(shipping);
        mav.setViewName("/checkout/pay");
        mav.addObject(CartConstant.ORDER, order);
        return mav;
    }


    public void sendEmail(User user, Order order) {
        try {
            UserInformation userInformation = userInformationService.queryUserInformation(user);
            if (userInformation == null || userInformation.getPersonEmail() == null) {
                LOGGER.info(
                        "Current user has not add user information so that cannot send place order email to he/she. User id is:{}.",
                        user.getId());
                return;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("user", user);
            map.put("order", order);
            getMailService().sendEmail(MailConstants.SUBJECT_PLACE_ORDER, map, MailConstants.TEMPLATE_PLACE_ORDER,
                    userInformation.getPersonEmail());
        } catch (Exception e) {
            LOGGER.error("Failed to send place order email to user:" + user.getId() + ".", e);
        }
    }


    private int isProductIdsValid(String[] productIds, String[] quantities, List<Product> relatedProducts, Tender tender) {
        if (productIds == null || productIds.length == 0) {
            LOGGER.error("productIds is null or length == 0");
            return NO_PRODUCT_IDS;
        }

        if (quantities == null || quantities.length == 0) {
            LOGGER.error("quantities is null or length == 0");
            return NO_QUANTITIES;
        }

        for (String productId : productIds) {
            if (StringUtils.isBlank(productId)) {
                LOGGER.error("productIds has blank data");
                return BLANK_PRODUCT_ID;
            }
        }

        for (String quantity : quantities) {
            if (StringUtils.isBlank(quantity)) {
                LOGGER.error("quantities has blank data");
                return BLANK_QUANTITY;
            }
        }
        if (productIds.length != quantities.length) {
            LOGGER.error("productIds length and quantities length is not match");
            return ID_QUANTITY_NOT_MATCH;
        }


        if (null != productIds) {
            // check relatedProducts need contains productIds
            if (null == relatedProducts || relatedProducts.isEmpty()) {
                LOGGER.error("no relate product");
                return PRODUCT_NOT_EXIST;
            }
            for (String productId : productIds) {
                if (null == productId) {
                    continue;
                }
                int id = 0;
                try {
                    id = Integer.valueOf(productId);
                } catch (Exception e) {
                    LOGGER.error("productId is not number");
                    return PRODUCT_NOT_EXIST;
                }
                boolean match = false;
                for (Product relatedProduct : relatedProducts) {
                    if (null == relatedProduct || null == relatedProduct.getProductId()) {
                        continue;
                    }
                    if (id == relatedProduct.getProductId()) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    LOGGER.error("productId[" + productId + "] is match.");
                    return PRODUCT_NOT_EXIST;
                }
            }
        }
        return NO_ERROR;
    }


    public ModelAndView createOrderAbondon(HttpServletRequest pRequest, HttpServletResponse pResponse) {
        User user = SessionHelper.getUser(pRequest, pResponse);
        if (null == user) {
            //  TODOo REDIRECT TO LOGIN
        }
        String tenderIdStr = pRequest.getParameter("tenderId");
        String placeQuantityStr = pRequest.getParameter("placeQuantity");
        String[] productIds = pRequest.getParameterValues("productIds");
        if (StringUtils.isBlank(tenderIdStr) || !StringUtils.isNumeric(tenderIdStr)) {
            // TODOo
        }
        if (StringUtils.isBlank(placeQuantityStr) || !StringUtils.isNumeric(placeQuantityStr)) {
            // TODOo
        }
        int tenderId = Integer.valueOf(tenderIdStr);
        int placeQuantity = Integer.valueOf(placeQuantityStr);

        // check if has not pay order
        if (getOrderService().hasUncompleteOrder(user.getId().intValue(), OrderType.P2P_ORDER, 0)) {
            // TODOo redirect to tendId
        }
        // TODOo QUERY TENDER BY ID
        Tender tender = null;
        // TODOo QUERY related product
        List<Product> relatedProducts = null;

        // check productIds is valid
        int errorCode = isProductIdsValid(productIds, relatedProducts, tender, placeQuantity);
        if (NO_ERROR != errorCode) {
            // TODOo redirect to tendId
        }
        // check inventory

        // create order
        // TODOo EXCEIPTOIN
        try {
            getOrderService().createP2POrder(user, tender, relatedProducts, productIds, placeQuantity);
        } catch (OrderPersistentException e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * @param productIds
     * @param relatedProducts
     * @param tender
     * @param placeQuantity
     * @return error code
     */
    private int isProductIdsValid(String[] productIds, List<Product> relatedProducts, Tender tender, int placeQuantity) {
        if (null != productIds) {
            // check relatedProducts need contains productIds
            if (null == relatedProducts || relatedProducts.isEmpty()) {
                // TODOo LOG
                return PRODUCT_NOT_EXIST;
            }
            double total = 0D;
            for (String productId : productIds) {
                if (null == productId) {
                    continue;
                }
                int id = 0;
                try {
                    id = Integer.valueOf(productId);
                } catch (Exception e) {
                    // TODOo log
                    return PRODUCT_NOT_EXIST;
                }
                boolean match = false;
                for (Product relatedProduct : relatedProducts) {
                    if (null == relatedProduct || null == relatedProduct.getProductId()) {
                        continue;
                    }
                    if (id == relatedProduct.getProductId()) {
                        match = true;
                        total += relatedProduct.getSalePrice();
                        // TODOo ROUND
                        break;
                    }
                }
                if (!match) {
                    // TODOo log
                    return PRODUCT_NOT_EXIST;
                }
            }
            double investTotal = tender.getUnitPrice() * placeQuantity;
            // TODOo ROUND;
            if (total > investTotal) {
                // TODOo log
                return INVEST_TOTAL_NOT_ENOUGH;
            }
        }
        return NO_ERROR;
    }

    public P2POrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(P2POrderService orderService) {
        this.orderService = orderService;
    }

    public InventoryService getTenderInventoryService() {
        return tenderInventoryService;
    }

    public void setTenderInventoryService(TenderInventoryService tenderInventoryService) {
        this.tenderInventoryService = tenderInventoryService;
    }

    public URLConfiguration getUrlConfiguration() {
        return urlConfiguration;
    }

    public void setUrlConfiguration(URLConfiguration urlConfiguration) {
        this.urlConfiguration = urlConfiguration;
    }

    public TenderService getTenderService() {
        return tenderService;
    }

    public void setTenderService(TenderService tenderService) {
        this.tenderService = tenderService;
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    public UserInformationService getUserInformationService() {
        return userInformationService;
    }

    public void setUserInformationService(UserInformationService userInformationService) {
        this.userInformationService = userInformationService;
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public ShippingService getShippingService() {
        return shippingService;
    }

    public void setShippingService(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    public AddressInfoService getAddressInfoService() {
        return addressInfoService;
    }

    public void setAddressInfoService(AddressInfoService addressInfoService) {
        this.addressInfoService = addressInfoService;
    }
}
