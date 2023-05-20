package com.fhao.rpc.core.common.event.listener;

import com.fhao.rpc.core.common.ChannelFutureWrapper;
import com.fhao.rpc.core.common.event.IRpcListener;
import com.fhao.rpc.core.common.event.IRpcNodeChangeEvent;
import com.fhao.rpc.core.registy.URL;
import com.fhao.rpc.core.registy.zookeeper.ProviderNodeInfo;

import java.util.List;

import static com.fhao.rpc.core.common.cache.CommonClientCache.CONNECT_MAP;
import static com.fhao.rpc.core.common.cache.CommonClientCache.IROUTER;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 14:08</p>
 * <p>description:   </p>
 */
public class ProviderNodeDataChangeListener implements IRpcListener<IRpcNodeChangeEvent> {
    @Override
    public void callBack(Object t) {
        ProviderNodeInfo providerNodeInfo = ((ProviderNodeInfo) t);
        List<ChannelFutureWrapper> channelFutureWrappers =  CONNECT_MAP.get(providerNodeInfo.getServiceName());
        for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers) {
            String address = channelFutureWrapper.getHost()+":"+channelFutureWrapper.getPort();
            if(address.equals(providerNodeInfo.getAddress())){
                //修改权重
                channelFutureWrapper.setWeight(providerNodeInfo.getWeight());
                URL url = new URL();
                url.setServiceName(providerNodeInfo.getServiceName());
                IROUTER.updateWeight(url);
                break;
            }
        }
    }
}
