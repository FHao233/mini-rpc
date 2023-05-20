package com.fhao.rpc.core.filter.server;

import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.common.utils.CommonUtils;
import com.fhao.rpc.core.filter.IServerFilter;
import com.fhao.rpc.core.server.ServiceWrapper;

import static com.fhao.rpc.core.common.cache.CommonServerCache.PROVIDER_SERVICE_WRAPPER_MAP;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 20:14</p>
 * <p>description:   </p>
 */
public class ServerTokenFilterImpl implements IServerFilter {
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String token = String.valueOf(rpcInvocation.getAttachments().get("serviceToken"));
        ServiceWrapper serviceWrapper = PROVIDER_SERVICE_WRAPPER_MAP.get(rpcInvocation.getTargetServiceName());
        String matchToken = String.valueOf(serviceWrapper.getServiceToken());
        if (CommonUtils.isEmpty(matchToken)) {
            return;
        }
        if (!CommonUtils.isEmpty(token) && token.equals(matchToken)) {
            return;
        }
        throw new RuntimeException("token is " + token + " , verify result is false!");
    }
}
