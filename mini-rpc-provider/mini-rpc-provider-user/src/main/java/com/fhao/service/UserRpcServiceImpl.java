package com.fhao.service;



import com.fhao.common.IRpcReference;
import com.fhao.common.IRpcService;
import com.fhao.rpc.interfaces.goods.GoodRpcService;
import com.fhao.rpc.interfaces.pay.PayRpcService;
import com.fhao.rpc.interfaces.user.UserRpcService;

import java.util.*;

@IRpcService
public class UserRpcServiceImpl implements UserRpcService {

    @IRpcReference
    private GoodRpcService goodRpcService;

    @IRpcReference
    private PayRpcService payRpcService;

    @Override
    public String getUserId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public List<Map<String, String>> findMyGoods(String userId) {
        List<String> goodsNoList = goodRpcService.selectGoodsNoByUserId(userId);
        List<Map<String, String>> finalResult = new ArrayList<>();
        goodsNoList.forEach(goodsNo -> {
            Map<String, String> item = new HashMap<>(2);
            List<String> payHistory = payRpcService.getPayHistoryByGoodNo(goodsNo);
            item.put(goodsNo, payHistory.toString());
            finalResult.add(item);
        });
        return finalResult;
    }
}
