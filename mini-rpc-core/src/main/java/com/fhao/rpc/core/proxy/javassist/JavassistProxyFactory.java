package com.fhao.rpc.core.proxy.javassist;

import com.fhao.rpc.core.client.RpcReferenceWrapper;
import com.fhao.rpc.core.proxy.ProxyFactory;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 12:48</p>
 * <p>description:   </p>
 */
public class JavassistProxyFactory implements ProxyFactory {
    @Override
    public <T> T getProxy(RpcReferenceWrapper rpcReferenceWrapper) throws Throwable {

        return (T) ProxyGenerator.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                rpcReferenceWrapper.getAimClass(), new JavassistInvocationHandler(rpcReferenceWrapper));
    }
}
