package com.fhao.rpc.core.router;

import com.fhao.rpc.core.common.ChannelFutureWrapper;
import com.fhao.rpc.core.registy.URL;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 13:30</p>
 * <p>description:   </p>
 */
public interface IRouter {

    /**
     * 刷新路由数组
     *
     * @param selector
     */
    void refreshRouterArr(Selector selector);

    /**
     * 获取到请求到连接通道
     *
     * @return
     */
    ChannelFutureWrapper select(Selector selector);

    /**
     * 更新权重信息
     *
     * @param url
     */
    void updateWeight(URL url);
}
