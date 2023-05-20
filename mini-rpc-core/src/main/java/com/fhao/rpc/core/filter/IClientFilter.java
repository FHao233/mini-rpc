package com.fhao.rpc.core.filter;

import com.fhao.rpc.core.common.ChannelFutureWrapper;
import com.fhao.rpc.core.common.RpcInvocation;

import java.util.List;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 20:02</p>
 * <p>description:   </p>
 */
public interface IClientFilter extends IFilter {

    /**
     * 执行过滤链
     *
     * @param src
     * @param rpcInvocation
     * @return
     */
    void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation);
}
