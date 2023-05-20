package com.fhao.rpc.core.router;

import com.fhao.rpc.core.common.ChannelFutureWrapper;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 13:34</p>
 * <p>description:   </p>
 */
public class Selector {
    /**
     * 服务命名
     * eg: com.sise.test.DataService
     */
    private String providerServiceName;

    public ChannelFutureWrapper[] getChannelFutureWrappers() {
        return channelFutureWrappers;
    }

    public void setChannelFutureWrappers(ChannelFutureWrapper[] channelFutureWrappers) {
        this.channelFutureWrappers = channelFutureWrappers;
    }

    /**
     * 经过二次筛选之后的future集合
     */
    private ChannelFutureWrapper[] channelFutureWrappers;

    public String getProviderServiceName() {
        return providerServiceName;
    }

    public void setProviderServiceName(String providerServiceName) {
        this.providerServiceName = providerServiceName;
    }
}
