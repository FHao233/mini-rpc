package com.fhao.rpc.interfaces;

import java.util.List;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 12:59</p>
 * <p>description:   </p>
 */
public interface DataService {

    /**
     * 发送数据
     *
     * @param body
     */
    String sendData(String body);

    /**
     * 获取数据
     *
     * @return
     */
    List<String> getList();
}
