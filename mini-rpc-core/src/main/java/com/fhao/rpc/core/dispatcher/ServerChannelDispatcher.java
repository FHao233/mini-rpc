package com.fhao.rpc.core.dispatcher;

import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.common.RpcProtocol;
import com.fhao.rpc.core.server.ServerChannelReadData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

import static com.fhao.rpc.core.common.cache.CommonServerCache.*;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-21 19:58</p>
 * <p>description:   </p>
 */
public class ServerChannelDispatcher {
    private BlockingQueue<ServerChannelReadData> RPC_DATA_QUEUE;
    private ExecutorService executorService;

    public ServerChannelDispatcher() {

    }

    public void init(int queueSize, int bizThreadNums) {
        RPC_DATA_QUEUE = new ArrayBlockingQueue<>(queueSize);
        executorService = new ThreadPoolExecutor(bizThreadNums, bizThreadNums,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(512));
    }

    public void add(ServerChannelReadData serverChannelReadData) {
        RPC_DATA_QUEUE.add(serverChannelReadData);
    }

    class ServerJobCoreHandle implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    ServerChannelReadData serverChannelReadData = RPC_DATA_QUEUE.take();
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                RpcProtocol rpcProtocol = serverChannelReadData.getRpcProtocol();
                                RpcInvocation rpcInvocation = SERVER_SERIALIZE_FACTORY.deserialize(rpcProtocol.getContent(), RpcInvocation.class);
                                SERVER_FILTER_CHAIN.doFilter(rpcInvocation);
                                Object aimObject = PROVIDER_CLASS_MAP.get(rpcInvocation.getTargetServiceName());
                                Method[] methods = aimObject.getClass().getDeclaredMethods();
                                Object result = null;
                                for (Method method : methods) {//遍历目标对象的所有方法
                                    //找到目标方法
                                    if(method.getName().equals(rpcInvocation.getTargetMethod())){
                                        // 通过反射找到目标对象，然后执行目标方法并返回对应值
                                        if (method.getReturnType().equals(Void.TYPE)) {
                                            method.invoke(aimObject, rpcInvocation.getArgs());
                                        }else {
                                            result = method.invoke(aimObject, rpcInvocation.getArgs());
                                        }
                                        break;
                                    }
                                }
                                rpcInvocation.setResponse(result);
                                RpcProtocol respRpcProtocol = new RpcProtocol(SERVER_SERIALIZE_FACTORY.serialize(rpcInvocation));
                                serverChannelReadData.getChannelHandlerContext().writeAndFlush(respRpcProtocol);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void startDataConsume() {
        Thread thread = new Thread(new ServerJobCoreHandle());
        thread.start();
    }

}
