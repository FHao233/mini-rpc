package com.fhao.rpc.core.proxy.javassist;

import com.fhao.rpc.core.proxy.ProxyFactory;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 12:48</p>
 * <p>description:   </p>
 */
public class JavassistProxyFactory implements ProxyFactory {
    @Override
    public <T> T getProxy(Class clazz) throws Throwable {
        return (T) ProxyGenerator.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                clazz, new JavassistInvocationHandler(clazz));
    }
}
