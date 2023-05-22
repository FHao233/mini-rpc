package com.fhao.service;


import com.fhao.common.IRpcService;
import com.fhao.rpc.interfaces.goods.GoodRpcService;

import java.util.Arrays;
import java.util.List;


@IRpcService(limit = 2)
public class GoodRpcServiceImpl implements GoodRpcService {

    @Override
    public boolean decreaseStock() {
        System.out.println("yessssssssssssss");
        return true;
    }

    @Override
    public List<String> selectGoodsNoByUserId(String userId) {
        return Arrays.asList(userId + "-good-01", userId + "-good-02");
    }
}
