package com.fhao.rpc.core.router;

import com.fhao.rpc.core.common.ChannelFutureWrapper;

import java.util.concurrent.atomic.AtomicLong;

import static com.fhao.rpc.core.common.cache.CommonClientCache.SERVICE_ROUTER_MAP;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 13:40</p>
 * <p>description:   </p>
 */
public class ChannelFuturePollingRef {
    private AtomicLong referenceTimes = new AtomicLong(0);
    public ChannelFutureWrapper getChannelFutureWrapper(String serviceName){
        ChannelFutureWrapper[] arr = SERVICE_ROUTER_MAP.get(serviceName);
        long i = referenceTimes.getAndIncrement();
        int index = (int) (i % arr.length);
        return arr[index];
    }
}
