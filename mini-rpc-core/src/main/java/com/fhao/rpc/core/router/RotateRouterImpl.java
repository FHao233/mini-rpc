package com.fhao.rpc.core.router;

import com.fhao.rpc.core.common.ChannelFutureWrapper;
import com.fhao.rpc.core.registy.URL;

import java.util.List;

import static com.fhao.rpc.core.common.cache.CommonClientCache.*;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 14:16</p>
 * <p>description:   </p>
 */
public class RotateRouterImpl implements IRouter{
    @Override
    public void refreshRouterArr(Selector selector) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(selector.getProviderServiceName());
        ChannelFutureWrapper[] arr = new ChannelFutureWrapper[channelFutureWrappers.size()];
        for (int i=0;i<channelFutureWrappers.size();i++) {
            arr[i]=channelFutureWrappers.get(i);
        }
        SERVICE_ROUTER_MAP.put(selector.getProviderServiceName(),arr);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrapper(selector.getChannelFutureWrappers());
    }

    @Override
    public void updateWeight(URL url) {

    }
}
