package com.fhao.rpc.core.dispatcher;

import com.fhao.Main;
import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.common.RpcProtocol;
import com.fhao.rpc.core.common.exception.IRpcException;
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

        //        @Override
//        public void run() {
//            while (true) {
//                try {
//                    ServerChannelReadData serverChannelReadData = RPC_DATA_QUEUE.take();
//                    executorService.submit(new Runnable() {
//                        @Override
//                        public void run() {
//                            RpcProtocol rpcProtocol = serverChannelReadData.getRpcProtocol();
//                            RpcInvocation rpcInvocation = SERVER_SERIALIZE_FACTORY.deserialize(rpcProtocol.getContent(), RpcInvocation.class);
//                            try {
//                                SERVER_BEFORE_FILTER_CHAIN.doFilter(rpcInvocation);
//                            } catch (Exception cause) {
//                                //针对自定义异常进行捕获，并且直接返回异常信息给到客户端，然后打印结果
//                                if (cause instanceof IRpcException) {
//                                    IRpcException rpcException = (IRpcException) cause;
//                                    RpcInvocation reqParam = rpcException.getRpcInvocation();
//                                    rpcInvocation.setE(rpcException);
//                                    byte[] body = SERVER_SERIALIZE_FACTORY.serialize(reqParam);
//                                    RpcProtocol respRpcProtocol = new RpcProtocol(body);
//                                    serverChannelReadData.getChannelHandlerContext().writeAndFlush(respRpcProtocol);
//                                    return;
//                                }
//                            }
//                            Object aimObject = PROVIDER_CLASS_MAP.get(rpcInvocation.getTargetServiceName());
//                            Method[] methods = aimObject.getClass().getDeclaredMethods();
//                            Object result = null;
//                            for (Method method : methods) {//遍历目标对象的所有方法
//                                //找到目标方法
//                                if (method.getName().equals(rpcInvocation.getTargetMethod())) {
//                                    // 通过反射找到目标对象，然后执行目标方法并返回对应值
//                                    if (method.getReturnType().equals(Void.TYPE)) {
//                                        try {
//                                            method.invoke(aimObject, rpcInvocation.getArgs());
//                                        } catch (Exception e) {
//                                            rpcInvocation.setE(e);
////                                            rpcInvocation.setMessage(e.getMessage());
////                                            rpcInvocation.setStackTrace(e.getStackTrace());
//                                        }
//                                    } else {
//                                        try {
//                                            result = method.invoke(aimObject, rpcInvocation.getArgs());
//                                        } catch (Exception e) {
//                                            rpcInvocation.setE(e);
//                                        }
//                                    }
//                                    break;
//                                }
//                            }
//                            rpcInvocation.setResponse(result);
//                            SERVER_AFTER_FILTER_CHAIN.doFilter(rpcInvocation);
//                            RpcProtocol respRpcProtocol = new RpcProtocol(SERVER_SERIALIZE_FACTORY.serialize(rpcInvocation));
//                            serverChannelReadData.getChannelHandlerContext().writeAndFlush(respRpcProtocol);
//
//                        }
//                    });
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        @Override
        public void run() {
            while (true) {
                try {
                    ServerChannelReadData serverChannelReadData = RPC_DATA_QUEUE.take();
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            RpcProtocol rpcProtocol = serverChannelReadData.getRpcProtocol();
                            // 将msg的content内容进一步转换为RpcInvocation获得具体信息
                            RpcInvocation rpcInvocation = SERVER_SERIALIZE_FACTORY.deserialize(rpcProtocol.getContent(), RpcInvocation.class);
                            System.out.println("rpcInvocation:" + rpcInvocation.getTargetServiceName());
                            System.out.println("serialize:" + SERVER_SERIALIZE_FACTORY);
                            // 执行请求过滤
                            try {
                                SERVER_BEFORE_FILTER_CHAIN.doFilter(rpcInvocation);
                            } catch (Exception e) {
                                if (e instanceof IRpcException) {
                                    IRpcException rpcException = (IRpcException) e;
                                    RpcInvocation repParam = rpcException.getRpcInvocation();
                                    rpcInvocation.setE(e);
                                    byte[] body = SERVER_SERIALIZE_FACTORY.serialize(repParam);
                                    RpcProtocol respRpcProtocol = new RpcProtocol(body);
                                    serverChannelReadData.getChannelHandlerContext().writeAndFlush(respRpcProtocol);
                                    return;
                                }
                            }
                            // 从服务提供者中获取目标服务
                            Object aimObject = PROVIDER_CLASS_MAP.get(rpcInvocation.getTargetServiceName());
                            // 获取目标服务的全部方法
                            Method[] methods = aimObject.getClass().getDeclaredMethods();
                            Object result = null;
                            // 寻找得到对应的方法并执行得到方法结果
                            for (Method method : methods) {
                                if (method.getName().equals(rpcInvocation.getTargetMethod())) {
                                    if (method.getReturnType().equals(Void.TYPE)) {
                                        try {
                                            method.invoke(aimObject, rpcInvocation.getArgs());
                                        } catch (Exception e) {
                                            rpcInvocation.setE(e);
                                        }
                                    } else {
                                        try {
                                            result = method.invoke(aimObject, rpcInvocation.getArgs());
                                        } catch (Exception e) {
                                            rpcInvocation.setE(e);
                                        }
                                    }
                                    break;
                                }
                            }
                            // 设置response
                            rpcInvocation.setResponse(result);
                            // 后置过滤器
                            SERVER_AFTER_FILTER_CHAIN.doFilter(rpcInvocation);
                            // 再次封装为RpcProtocol返回给客户端
                            RpcProtocol respRpcProtocol = new RpcProtocol(SERVER_SERIALIZE_FACTORY.serialize(rpcInvocation));
                            serverChannelReadData.getChannelHandlerContext().writeAndFlush(respRpcProtocol);
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
