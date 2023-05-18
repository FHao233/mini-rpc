package com.fhao.rpc.core.client;


import com.fhao.rpc.core.proxy.ProxyFactory;

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
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T get(Class<T> tClass) throws Throwable {
        return proxyFactory.getProxy(tClass);
    }
}
