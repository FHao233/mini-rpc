package com.fhao.rpc.core.filter.server;

import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.filter.IServerFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-21 21:39</p>
 * <p>description:   </p>
 */
public class ServerAfterFilterChain {
    private static List<IServerFilter> iServerFilters = new ArrayList<>();

    public void addServerFilter(IServerFilter iServerFilter) {
        iServerFilters.add(iServerFilter);
    }

    public void doFilter(RpcInvocation rpcInvocation) {
        for (IServerFilter iServerFilter : iServerFilters) {
            iServerFilter.doFilter(rpcInvocation);
        }
    }
}
