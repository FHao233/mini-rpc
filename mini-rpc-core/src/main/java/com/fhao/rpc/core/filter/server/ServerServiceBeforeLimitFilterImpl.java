package com.fhao.rpc.core.filter.server;

import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.common.ServerServiceSemaphoreWrapper;
import com.fhao.rpc.core.common.annotations.SPI;
import com.fhao.rpc.core.common.exception.MaxServiceLimitRequestException;
import com.fhao.rpc.core.filter.IServerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

import static com.fhao.rpc.core.common.cache.CommonServerCache.SERVER_SERVICE_SEMAPHORE_MAP;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-21 21:42</p>
 * <p>description:   </p>
 */
@SPI("before")
public class ServerServiceBeforeLimitFilterImpl implements IServerFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerServiceBeforeLimitFilterImpl.class);

    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String serviceName = rpcInvocation.getTargetServiceName();
        ServerServiceSemaphoreWrapper serverServiceSemaphoreWrapper = SERVER_SERVICE_SEMAPHORE_MAP.get(serviceName);
        //从缓存中提取semaphore对象
        Semaphore semaphore = serverServiceSemaphoreWrapper.getSemaphore();
        boolean tryResult = semaphore.tryAcquire();
        if (!tryResult) {
            LOGGER.error("[ServerServiceBeforeLimitFilterImpl] {}'s max request is {},reject now", rpcInvocation.getTargetServiceName(), serverServiceSemaphoreWrapper.getMaxNums());
            MaxServiceLimitRequestException iRpcException = new MaxServiceLimitRequestException(rpcInvocation);
//            rpcInvocation.setE(iRpcException);
            throw iRpcException;
        }
    }
}
