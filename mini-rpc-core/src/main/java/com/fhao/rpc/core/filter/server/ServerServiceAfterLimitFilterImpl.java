package com.fhao.rpc.core.filter.server;

import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.common.ServerServiceSemaphoreWrapper;
import com.fhao.rpc.core.common.annotations.SPI;
import com.fhao.rpc.core.filter.IServerFilter;

import static com.fhao.rpc.core.common.cache.CommonServerCache.SERVER_SERVICE_SEMAPHORE_MAP;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-21 21:40</p>
 * <p>description:   </p>
 */
@SPI("after")
public class ServerServiceAfterLimitFilterImpl implements IServerFilter {
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String serviceName = rpcInvocation.getTargetServiceName();
        ServerServiceSemaphoreWrapper serverServiceSemaphoreWrapper = SERVER_SERVICE_SEMAPHORE_MAP.get(serviceName);
        serverServiceSemaphoreWrapper.getSemaphore().release();
    }
}
