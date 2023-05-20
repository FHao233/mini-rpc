package com.fhao.rpc.core.common.cache;

import com.fhao.rpc.core.common.ChannelFutureWrapper;
import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.common.config.ClientConfig;
import com.fhao.rpc.core.registy.URL;
import com.fhao.rpc.core.router.ChannelFuturePollingRef;
import com.fhao.rpc.core.router.IRouter;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 16:03</p>
 * <p>description: 公用缓存 存储请求队列等公共信息  </p>
 */
public class CommonClientCache {
    //阻塞队列，存储待发送的RPC调用
    public static BlockingQueue<RpcInvocation> SEND_QUEUE = new ArrayBlockingQueue<>(100);
    //ConcurrentHashMap，用于存储RPC调用的响应结果
    public static Map<String,Object> RESP_MAP = new ConcurrentHashMap<>();
    public static ClientConfig CLIENT_CONFIG;
    //provider名称 --> 该服务有哪些集群URL
    public static List<URL> SUBSCRIBE_SERVICE_LIST = new ArrayList<>();//是一个服务名称列表，用于存储客户端订阅的服务名称。
    public static Map<String, List<URL>> URL_MAP = new ConcurrentHashMap<>();//一个字符串到URL列表的映射，用于存储服务提供者的URL地址。
    public static Set<String> SERVER_ADDRESS = new HashSet<>();//SERVER_ADDRESS是一个字符串集合，用于存储服务提供者的地址。
    //每次进行远程调用的时候都是从这里面去选择服务提供者
    public static Map<String, List<ChannelFutureWrapper>> CONNECT_MAP = new ConcurrentHashMap<>();//这个map保存了服务提供者的连接信息。
    //CONNECT_MAP是一个字符串到ChannelFutureWrapper列表的映射，用于存储服务提供者的连接信息。

    public static Map<String, ChannelFutureWrapper[]> SERVICE_ROUTER_MAP = new ConcurrentHashMap<>();
    public static ChannelFuturePollingRef CHANNEL_FUTURE_POLLING_REF = new ChannelFuturePollingRef();
    public static IRouter IROUTER;
}
