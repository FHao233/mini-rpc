package com.fhao.rpc.core.common.cache;

import com.fhao.rpc.core.common.RpcInvocation;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 16:03</p>
 * <p>description: 公用缓存 存储请求队列等公共信息  </p>
 */
public class CommonClientCache {
    public static BlockingQueue<RpcInvocation> SEND_QUEUE = new ArrayBlockingQueue(100);
    public static Map<String,Object> RESP_MAP = new ConcurrentHashMap<>();


}
