package com.fhao.rpc.core.filter.client;

import com.fhao.rpc.core.common.ChannelFutureWrapper;
import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.filter.IClientFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.fhao.rpc.core.common.cache.CommonClientCache.CLIENT_CONFIG;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 20:07</p>
 * <p>description:   </p>
 */
public class ClientLogFilterImpl implements IClientFilter {
    private static Logger logger = LoggerFactory.getLogger(ClientLogFilterImpl.class);

    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        rpcInvocation.getAttachments().put("c_app_name",CLIENT_CONFIG.getApplicationName());
        logger.info(rpcInvocation.getAttachments().get("c_app_name")+" do invoke -----> "+rpcInvocation.getTargetServiceName());
    }
}
