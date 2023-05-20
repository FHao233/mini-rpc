package com.fhao.rpc.core.proxy;

import com.fhao.rpc.core.client.RpcReferenceWrapper;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 16:01</p>
 * <p>description: 创建代理工厂，用来生成代理对象 </p>
 */

public interface ProxyFactory {
    <T> T getProxy(RpcReferenceWrapper rpcReferenceWrapper) throws Throwable;
}
