package com.fhao.rpc.core.filter.client;

import com.fhao.rpc.core.common.ChannelFutureWrapper;
import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.common.utils.CommonUtils;
import com.fhao.rpc.core.filter.IClientFilter;

import java.util.Iterator;
import java.util.List;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 20:11</p>
 * <p>description:   </p>
 */
public class DirectInvokeFilterImpl implements IClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        String url = (String) rpcInvocation.getAttachments().get("url");
        if(CommonUtils.isEmpty(url)){
            return;
        }
        Iterator<ChannelFutureWrapper> channelFutureWrapperIterator = src.iterator();
        while (channelFutureWrapperIterator.hasNext()){
            ChannelFutureWrapper channelFutureWrapper = channelFutureWrapperIterator.next();
            if(!(channelFutureWrapper.getHost()+":"+channelFutureWrapper.getPort()).equals(url)){
                channelFutureWrapperIterator.remove();
            }
        }
        if(CommonUtils.isEmptyList(src)){
            throw new RuntimeException("no match provider url for "+ url);
        }
    }
}
