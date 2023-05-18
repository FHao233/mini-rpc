package com.fhao.rpc.core.proxy.jdk;

import com.fhao.rpc.core.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 19:32</p>
 * <p>description:   </p>
 */
public class JDKProxyFactory implements ProxyFactory {
    @Override
    public <T> T getProxy(final Class clazz) throws Throwable {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                new JDKClientInvocationHandler(clazz));
    }
}
