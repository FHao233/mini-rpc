package com.fhao.rpc.core.common.exception;

import com.fhao.rpc.core.common.RpcInvocation;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-21 21:34</p>
 * <p>description:   </p>
 */
public class IRpcException extends RuntimeException{
    private RpcInvocation rpcInvocation;
    public RpcInvocation getRpcInvocation() {
        return rpcInvocation;
    }

    public void setRpcInvocation(RpcInvocation rpcInvocation) {
        this.rpcInvocation = rpcInvocation;
    }

    public IRpcException(RpcInvocation rpcInvocation) {
        this.rpcInvocation = rpcInvocation;
    }
}
