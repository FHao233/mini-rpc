package com.fhao.rpc.core.filter.client;

import com.fhao.rpc.core.common.ChannelFutureWrapper;
import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.common.utils.CommonUtils;
import com.fhao.rpc.core.filter.IClientFilter;

import java.util.List;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 20:09</p>
 * <p>description:   </p>
 */
public class GroupFilterImpl implements IClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        String group = String.valueOf(rpcInvocation.getAttachments().get("group"));
        for (ChannelFutureWrapper channelFutureWrapper : src) {
            if (!channelFutureWrapper.getGroup().equals(group)) {
                src.remove(channelFutureWrapper);
            }
        }
        if (CommonUtils.isEmptyList(src)) {
            throw new RuntimeException("no provider match for group " + group);
        }
    }
}
