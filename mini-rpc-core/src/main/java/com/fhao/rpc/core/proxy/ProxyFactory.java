package com.fhao.rpc.core.proxy;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 16:01</p>
 * <p>description:   </p>
 */
public interface ProxyFactory {
    <T> T getProxy(final Class clazz) throws Throwable;
}
