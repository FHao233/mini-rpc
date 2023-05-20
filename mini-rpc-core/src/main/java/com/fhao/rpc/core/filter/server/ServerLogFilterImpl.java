package com.fhao.rpc.core.filter.server;

import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.filter.IServerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 20:13</p>
 * <p>description:   </p>
 */
public class ServerLogFilterImpl implements IServerFilter {
    private static Logger logger = LoggerFactory.getLogger(ServerLogFilterImpl.class);
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        logger.info(rpcInvocation.getAttachments().get("c_app_name") + " do invoke -----> " + rpcInvocation.getTargetServiceName() + "#" + rpcInvocation.getTargetMethod());
    }
}
