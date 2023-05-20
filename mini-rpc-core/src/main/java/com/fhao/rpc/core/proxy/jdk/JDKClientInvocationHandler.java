package com.fhao.rpc.core.proxy.jdk;

import com.fhao.rpc.core.client.RpcReferenceWrapper;
import com.fhao.rpc.core.common.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static com.fhao.rpc.core.common.cache.CommonClientCache.RESP_MAP;
import static com.fhao.rpc.core.common.cache.CommonClientCache.SEND_QUEUE;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 19:32</p>
 * <p>description:  各种代理工厂统一使用这个InvocationHandler </p>
 */
public class JDKClientInvocationHandler implements InvocationHandler {
    // 用于占位，防止缓存中的key为null
    private final static Object OBJECT = new Object();
    // 代理的接口
    private RpcReferenceWrapper rpcReferenceWrapper;

    // 代理的接口的实现类
    public JDKClientInvocationHandler(RpcReferenceWrapper rpcReferenceWrapper) {
        this.rpcReferenceWrapper = rpcReferenceWrapper;
    }

    // 通过代理对象调用方法时，会调用这个方法
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcInvocation rpcInvocation = new RpcInvocation();// 封装请求信息
        rpcInvocation.setArgs(args);// 请求参数
        rpcInvocation.setTargetMethod(method.getName());// 请求方法名
        rpcInvocation.setTargetServiceName(rpcReferenceWrapper.getAimClass().getName());// 请求接口名
        rpcInvocation.setAttachments(rpcReferenceWrapper.getAttatchments());
        rpcInvocation.setUuid(UUID.randomUUID().toString()); //这里面注入了一个uuid，对每一次的请求都做单独区分
        RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);// 先占位
        SEND_QUEUE.add(rpcInvocation);// 将请求信息放入队列中
        long beginTime = System.currentTimeMillis();// 记录开始时间
        while (System.currentTimeMillis() - beginTime < 3 * 1000) {//
            Object object = RESP_MAP.get(rpcInvocation.getUuid());
            if (object instanceof RpcInvocation) {// 如果是RpcInvocation说明服务端返回结果
                return ((RpcInvocation) object).getResponse();// 返回结果
            }
        }
        throw new TimeoutException("client wait server's response timeout!");
    }
}
