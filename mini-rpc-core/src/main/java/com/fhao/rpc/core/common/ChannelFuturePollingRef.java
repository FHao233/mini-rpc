package com.fhao.rpc.core.common;


import java.util.concurrent.atomic.AtomicLong;


/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 13:40</p>
 * <p>description:   </p>
 */
public class ChannelFuturePollingRef {
    private AtomicLong referenceTimes = new AtomicLong(0);
    public ChannelFutureWrapper getChannelFutureWrapper(ChannelFutureWrapper[] arr){
        long i = referenceTimes.getAndIncrement();
        int index = (int) (i % arr.length);
        return arr[index];
    }
}
