package com.pgt.sms.controller;

import com.pgt.constant.Constants;
import com.pgt.sms.service.SmsService;
import com.pgt.tender.bean.TenderBuyer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by carlwang on 12/1/15.
 */

@RestController
@RequestMapping("/sms")
public class SMSController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SMSController.class);

    @Autowired
    private SmsService smsService;

    @Autowired
    private SimpleCacheManager cacheManager;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void sendLoginSms(@RequestParam(value = "phoneNumber", required = true) String phoneNumber,
                             @RequestParam(value = "phoneId", required = false) String phoneId, HttpServletRequest request) {
        String phoneCode = generate();

        savePhoneCode(request, phoneCode, Constants.LOGIN_SESSION_PHONE_CODE, phoneId);
        smsService.sendLoginSms(phoneNumber, phoneCode);
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public void sendRegisterSms(@RequestParam(value = "phoneNumber", required = true) String phoneNumber,
                                @RequestParam(value = "phoneId", required = false) String phoneId, HttpServletRequest request) {
        LOGGER.debug("The phone number is {},phone id is {},", phoneNumber, phoneId);
        String phoneCode = generate();
        LOGGER.debug("The phone code is {}", phoneCode);
        savePhoneCode(request, phoneCode, Constants.REGISTER_SESSION_PHONE_CODE, phoneId);
        smsService.sendRegisterSms(phoneNumber, phoneCode);
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
    public void sendResetPasswordSms(@RequestParam(value = "phoneNumber", required = true) String phoneNumber,
                                     @RequestParam(value = "phoneId", required = false) String phoneId, HttpServletRequest request) {
        LOGGER.debug("The phone number is {},phone id is {},", phoneNumber, phoneId);
        String phoneCode = generate();
        LOGGER.debug("The phone code is {}", phoneCode);
        savePhoneCode(request, phoneCode, Constants.RESET_PASSWOR_SESSION_PHONE_CODE, phoneId);
        smsService.sendResetPasswordSms(phoneNumber, phoneCode);
    }

    @RequestMapping(value = "/onlinePawn", method = RequestMethod.GET)
    public void sendOnlinePawnSms(@RequestParam(value = "phoneNumber", required = true) String phoneNumber,
                                  @RequestParam(value = "phoneId", required = false) String phoneId, HttpServletRequest request) {
        LOGGER.debug("The phone number is {},phone id is {},", phoneNumber, phoneId);
        String phoneCode = generate();
        LOGGER.debug("The phone code is {}", phoneCode);
        savePhoneCode(request, phoneCode, Constants.ONLINEPAWN_SESSION_PHONE_CODE, phoneId);
        smsService.sendOnlinePawnSms(phoneNumber, phoneCode);
    }

    @ResponseBody
    @RequestMapping(value = "/sendToBuyer", method = RequestMethod.POST)
    public Map<String, String> sendToBuyer(TenderBuyer tenderBuyer) {
        Map<String, String> map = new HashMap<>();
        if (ObjectUtils.isEmpty(tenderBuyer)) {
            map.put("success", "false");
            LOGGER.debug("The tender buyer is empty.");
            return map;
        }
        if (ObjectUtils.isEmpty(tenderBuyer.getSellerId())) {
            map.put("success", "false");
            LOGGER.debug("The seller is empty.");
            return map;
        }
        if (ObjectUtils.isEmpty(tenderBuyer.getBuyerNumber())) {
            map.put("success", "false");
            LOGGER.debug("The buyer phone number is empty.");
            return map;
        }
        if (ObjectUtils.isEmpty(tenderBuyer.getMessage())) {
            map.put("success", "false");
            LOGGER.debug("The buyer message is empty.");
            return map;
        }
        smsService.sendToBuyer(tenderBuyer);
        map.put("success", "true");
        return map;
    }


    private void savePhoneCode(HttpServletRequest request, String phoneCode, String codeKey, String phoneId) {
        if (!StringUtils.isBlank(phoneId)) {
            Cache cache = cacheManager.getCache(Constants.PHONE_CODE);
            cache.put(phoneId, phoneCode);
            return;
        }
        HttpSession session = request.getSession();
        session.setAttribute(codeKey, phoneCode.toString());
    }

    private String generate() {
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder phoneCode = new StringBuilder();
        int[] ret = new int[4];
        for (int i = 0; i < ret.length; i++) {
            phoneCode.append(rand.nextInt(10));
        }

        return phoneCode.toString();
    }

    public SmsService getSmsService() {
        return smsService;
    }

    public void setSmsService(SmsService smsService) {
        this.smsService = smsService;
    }

    public SimpleCacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(SimpleCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
