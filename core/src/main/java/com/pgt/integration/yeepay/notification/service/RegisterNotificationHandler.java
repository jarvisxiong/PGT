package com.pgt.integration.yeepay.notification.service;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.pgt.integration.yeepay.YeePayConstants;
import com.pgt.payment.bean.TransactionLog;
import com.pgt.user.bean.User;
import com.pgt.user.dao.UserMapper;
import com.pgt.utils.Transactionable;

public class RegisterNotificationHandler extends Transactionable implements YeepayNotificationHandler {

	private UserMapper userMapper;
	
	@Override
	public void handleCallback(Map<String, String> inboundParams, TransactionLog transactionLog) {
		Long userId = transactionLog.getUserId();
		// check user is registored or not

		ensureTransaction();
		try {
			User user = getUserMapper().selectUser(userId.intValue());
			if (user.getYeepayStatus() != YeePayConstants.REGISTOR_STATUS_NOT_REGIST) {
				
				// TODO REPLACE TO THROW EXCEPTION
				return;
			}
			String code = inboundParams.get(YeePayConstants.PARAM_NAME_CODE);
			if (YeePayConstants.CODE_SUCCESS.equals(code)) {
				user.setYeepayStatus(YeePayConstants.REGISTOR_STATUS_SUCCESS);
			} else {
				user.setYeepayStatus(YeePayConstants.REGISTOR_STATUS_FAILD);
			}
			user.setYeepayRegistoredDate(new Date());
			getUserMapper().update(user);
		} catch (Exception e) {
			setAsRollback();
			// TODO THROW WRAPPED EXCEPTOIN
		} finally {
			commit();
		}
	}
	
	

	@Override
	public void handleNotify(Map<String, String> inboundParams, TransactionLog transactionLog) {
		handleCallback(inboundParams, transactionLog);
	}

	public UserMapper getUserMapper() {
		return userMapper;
	}

	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

}