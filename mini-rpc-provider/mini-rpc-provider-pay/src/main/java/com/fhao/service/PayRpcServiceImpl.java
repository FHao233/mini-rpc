package com.fhao.service;



import com.fhao.common.IRpcService;
import com.fhao.rpc.interfaces.pay.PayRpcService;

import java.util.List;

@IRpcService
public class PayRpcServiceImpl implements PayRpcService {
    @Override
    public boolean doPay() {
        return false;
    }

    @Override
    public List<String> getPayHistoryByGoodNo(String goodNo) {
        return null;
    }
}
