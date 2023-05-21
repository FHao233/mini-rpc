package com.fhao.rpc.core.client;


import com.fhao.rpc.core.proxy.ProxyFactory;

import static com.fhao.rpc.core.common.cache.CommonClientCache.CLIENT_CONFIG;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 16:00</p>
 * <p>description:   </p>
 */
public class RpcReference {
    public ProxyFactory proxyFactory;

    public RpcReference(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    /**
     * 根据接口类型获取代理对象
     *
     * @param rpcReferenceWrapper
     * @param <T>
     * @return
     */
    public <T> T get(RpcReferenceWrapper<T> rpcReferenceWrapper) throws Throwable {
        initGlobalRpcReferenceWrapperConfig(rpcReferenceWrapper);
        return proxyFactory.getProxy(rpcReferenceWrapper);
    }
    /**
     * 初始化远程调用的一些全局配置,例如超时
     *
     * @param rpcReferenceWrapper
     */
    private void initGlobalRpcReferenceWrapperConfig(RpcReferenceWrapper rpcReferenceWrapper) {
        if (rpcReferenceWrapper.getTimeOUt() == null) {
            rpcReferenceWrapper.setTimeOut(CLIENT_CONFIG.getTimeOut());
        }
    }
}
