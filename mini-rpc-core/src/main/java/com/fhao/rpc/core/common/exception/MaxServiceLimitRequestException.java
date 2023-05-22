package com.fhao.rpc.core.common.exception;

import com.fhao.rpc.core.common.RpcInvocation;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-21 21:35</p>
 * <p>description:   </p>
 */
public class MaxServiceLimitRequestException extends IRpcException{
    public MaxServiceLimitRequestException(RpcInvocation rpcInvocation) {
        super(rpcInvocation);
    }
}
