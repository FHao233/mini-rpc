package com.fhao.rpc.core.filter;

import com.fhao.rpc.core.common.RpcInvocation;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 20:03</p>
 * <p>description:   </p>
 */
public interface IServerFilter extends IFilter{
    /**
     * 执行核心过滤逻辑
     *
     * @param rpcInvocation
     */
    void doFilter(RpcInvocation rpcInvocation);
}
