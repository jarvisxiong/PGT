package com.tencent.service;

import com.pgt.integration.wechat.bean.UnifiedOrderReqData;
import com.tencent.common.Configure;

/**
 * Created by Administrator on 2016/3/23.
 */
public class UnifiedOrderService  extends BaseService{

    public UnifiedOrderService() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        super(Configure.UNIFIED_ORDER);
    }

    /**
     * ����֧������
     * @param reqData ������ݶ������������APIҪ���ύ�ĸ��������ֶ�
     * @return API���ص�����
     * @throws Exception
     */
    public String request(UnifiedOrderReqData reqData) throws Exception {

        //--------------------------------------------------------------------
        //����HTTPS��Post����API��ַ
        //--------------------------------------------------------------------
        String responseString = sendPost(reqData);

        return responseString;
    }
}
