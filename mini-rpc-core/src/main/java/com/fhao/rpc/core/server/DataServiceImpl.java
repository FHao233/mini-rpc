package com.fhao.rpc.core.server;

import com.fhao.rpc.interfaces.DataService;

import java.util.ArrayList;
import java.util.List;

/**
 * author: FHao
 * create time: 2023-04-26 15:21
 * description:
 */
public class DataServiceImpl implements DataService {

    @Override
    public String sendData(String body) {
        System.out.println("己收到的参数长度："+body.length());
        int a = 1/0;
        return "success";
    }

    @Override
    public List<String> getList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("idea1");
        arrayList.add("idea2");
        arrayList.add("idea3");
        return arrayList;
    }

    @Override
    public String testErrorV2() {
        throw new RuntimeException("error");
    }
}
