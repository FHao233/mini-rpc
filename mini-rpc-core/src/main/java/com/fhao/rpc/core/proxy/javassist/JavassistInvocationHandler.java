package com.fhao.rpc.core.proxy.javassist;

import com.fhao.rpc.core.client.RpcReferenceWrapper;
import com.fhao.rpc.core.common.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static com.fhao.rpc.core.common.cache.CommonClientCache.SEND_QUEUE;
import static com.fhao.rpc.core.common.cache.CommonClientCache.RESP_MAP;
import static com.fhao.rpc.core.common.constants.RpcConstants.DEFAULT_TIMEOUT;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 12:51</p>
 * <p>description:   </p>
 */
public class JavassistInvocationHandler implements InvocationHandler {


    private final static Object OBJECT = new Object();

    private Long timeOut;

    private RpcReferenceWrapper rpcReferenceWrapper;

    public JavassistInvocationHandler(RpcReferenceWrapper rpcReferenceWrapper) {

        this.rpcReferenceWrapper = rpcReferenceWrapper;
        timeOut = Long.valueOf(String.valueOf(rpcReferenceWrapper.getAttatchments().get("timeOut")));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setArgs(args);
        rpcInvocation.setTargetMethod(method.getName());
        rpcInvocation.setTargetServiceName(rpcReferenceWrapper.getAimClass().getName());
        rpcInvocation.setAttachments(rpcReferenceWrapper.getAttatchments());
        rpcInvocation.setUuid(UUID.randomUUID().toString());
        SEND_QUEUE.add(rpcInvocation);
        //既然是一步请求，就没有必要再在RESP_MAP中判断是否有响应结果了
        if (rpcReferenceWrapper.isAsync()) {
            return null;
        }
        RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);
        //代理类内部将请求放入到发送队列中，等待发送队列发送请求
        long beginTime = System.currentTimeMillis();
        //如果请求数据在指定时间内返回则返回给客户端调用方
        while (System.currentTimeMillis() - beginTime < timeOut) {
            Object object = RESP_MAP.get(rpcInvocation.getUuid());
            if (object instanceof RpcInvocation) {
                return ((RpcInvocation) object).getResponse();
            }
        }
        //修改异常信息
        throw new TimeoutException("Wait for response from server on client " + timeOut + "ms,Service's name is " + rpcInvocation.getTargetServiceName() + "#" + rpcInvocation.getTargetMethod());
    }
}
