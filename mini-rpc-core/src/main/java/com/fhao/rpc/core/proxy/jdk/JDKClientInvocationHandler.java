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

    private Long timeOut;

    // 代理的接口
    private RpcReferenceWrapper rpcReferenceWrapper;

    // 代理的接口的实现类
    public JDKClientInvocationHandler(RpcReferenceWrapper rpcReferenceWrapper) {
        this.rpcReferenceWrapper = rpcReferenceWrapper;
        timeOut = Long.valueOf(String.valueOf(rpcReferenceWrapper.getAttachments().get("timeOut")));

    }

    /**
     * 封装RpcInvocation，并将其放入
     * 1.RESP_MAP -> 将请求与响应相关联，便于客户端接收结果时加以判断
     * 2.SEND_QUEUE -> 客户端中的异步线程会从阻塞队列中取出并按照顺序发送给服务端
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    // 通过代理对象调用方法时，会调用这个方法
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcInvocation rpcInvocation = new RpcInvocation();// 封装请求信息
        rpcInvocation.setArgs(args);// 请求参数
        rpcInvocation.setTargetMethod(method.getName());// 请求方法名
        rpcInvocation.setTargetServiceName(rpcReferenceWrapper.getAimClass().getName());// 请求接口名
        rpcInvocation.setAttachments(rpcReferenceWrapper.getAttachments());
        rpcInvocation.setUuid(UUID.randomUUID().toString()); //这里面注入了一个uuid，对每一次的请求都做单独区分
        SEND_QUEUE.add(rpcInvocation);// 将请求信息放入队列中
        if (rpcReferenceWrapper.isAsync()) {
            return null;
        }
        RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);// 先占位
        long beginTime = System.currentTimeMillis();// 记录开始时间
        int retryTimes = 0;
        while (System.currentTimeMillis() - beginTime < timeOut || rpcInvocation.getRetry() > 0) {
            Object object = RESP_MAP.get(rpcInvocation.getUuid());
            if (object instanceof RpcInvocation) {
                RpcInvocation rpcInvocationResp = (RpcInvocation) object;
                if (rpcInvocationResp.getRetry() == 0 && rpcInvocationResp.getE() == null) {
                    return rpcInvocationResp.getResponse();
                } else if (rpcInvocationResp.getE() != null) {
                    if (rpcInvocationResp.getRetry() == 0) {
                        return rpcInvocationResp.getResponse();
                    }
                    if (System.currentTimeMillis() - beginTime > timeOut) {
                        retryTimes++;
                        rpcInvocation.setResponse(null);
                        rpcInvocation.setRetry(rpcInvocationResp.getRetry() - 1);
                        RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);
                        SEND_QUEUE.add(rpcInvocation);
                    }
                }
            }
        }
        RESP_MAP.remove(rpcInvocation.getUuid());
        //修改异常信息
        throw new TimeoutException("Wait for response from server on client " + timeOut + "ms,retry times is " + retryTimes + ",service's name is " + rpcInvocation.getTargetServiceName() + "#" + rpcInvocation.getTargetMethod());
    }
}
