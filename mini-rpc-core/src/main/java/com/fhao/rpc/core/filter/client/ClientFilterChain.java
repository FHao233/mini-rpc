package com.fhao.rpc.core.filter.client;

import com.fhao.rpc.core.common.ChannelFutureWrapper;
import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.filter.IClientFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 20:05</p>
 * <p>description:   </p>
 */
public class ClientFilterChain {
    private static List<IClientFilter> iClientFilterList = new ArrayList<>();
    public void addClientFilter(IClientFilter iClientFilter) {
        iClientFilterList.add(iClientFilter);
    }

    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        for (IClientFilter iClientFilter : iClientFilterList) {
            iClientFilter.doFilter(src, rpcInvocation);
        }
    }
}
