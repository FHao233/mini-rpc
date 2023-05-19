package com.fhao.rpc.core.common.event.listener;

import com.fhao.rpc.core.client.ConnectionHandler;
import com.fhao.rpc.core.common.ChannelFutureWrapper;
import com.fhao.rpc.core.common.event.IRpcListener;
import com.fhao.rpc.core.common.event.IRpcUpdateEvent;
import com.fhao.rpc.core.common.event.data.URLChangeWrapper;
import com.fhao.rpc.core.common.utils.CommonUtils;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.fhao.rpc.core.common.cache.CommonClientCache.CONNECT_MAP;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 13:48</p>
 * <p>description:   </p>
 */
//这个监听器的作用是用来监听服务端的服务列表变化的
public class ServiceUpdateListener implements IRpcListener<IRpcUpdateEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUpdateListener.class);

    @Override
    public void callBack(Object t) { //这个回调函数的参数是一个IRpcUpdateEvent类型的对象
        //获取到字节点的数据信息
        URLChangeWrapper urlChangeWrapper = (URLChangeWrapper) t; //这个t是一个IRpcUpdateEvent类型的对象
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(urlChangeWrapper.getServiceName()); //这个CONNECT_MAP是一个Map<String, List<ChannelFutureWrapper>>类型的对象
        if (CommonUtils.isEmptyList(channelFutureWrappers)) {
            LOGGER.error("[ServiceUpdateListener] channelFutureWrappers is empty");
            return;
        } else {
            List<String> matchProviderUrl = urlChangeWrapper.getProviderUrl();//获取提供服务的地址，这个地址是一个List<String>类型的对象
            Set<String> finalUrl = new HashSet<>();//创建一个保存最终url的Set<String>类型的对象
            List<ChannelFutureWrapper> finalChannelFutureWrappers = new ArrayList<>();//创建一个保存最终channelFutureWrappers的List<ChannelFutureWrapper>类型的对象
            for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers) {//遍历map中的channelFutureWrappers
                String oldServerAddress = channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort();//获取到老的url
                //如果老的url没有，说明已经被移除了
                if (!matchProviderUrl.contains(oldServerAddress)) { //如果matchProviderUrl中不包含oldServerAddress，说明其已经下线了
                    continue;
                } else {
                    finalChannelFutureWrappers.add(channelFutureWrapper);//如果matchProviderUrl中包含oldServerAddress，说明其还在线，将其加入到finalChannelFutureWrappers中
                    finalUrl.add(oldServerAddress);//将其加入到finalUrl中
                }
            }
            //此时老的url已经被移除了，开始检查是否有新的url
            List<ChannelFutureWrapper> newChannelFutureWrapper = new ArrayList<>();
            for (String newProviderUrl : matchProviderUrl) {//遍历matchProviderUrl
                if (!finalUrl.contains(newProviderUrl)) {//如果finalUrl中不包含newProviderUrl，说明其是新的url
                    ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();//创建一个ChannelFutureWrapper类型的对象
                    String host = newProviderUrl.split(":")[0];//获取到host
                    Integer port = Integer.valueOf(newProviderUrl.split(":")[1]);//获取到port
                    channelFutureWrapper.setPort(port);
                    channelFutureWrapper.setHost(host);
                    ChannelFuture channelFuture = null;
                    try {
                        channelFuture = ConnectionHandler.createChannelFuture(host,port);//创建一个ChannelFuture类型的对象
                        channelFutureWrapper.setChannelFuture(channelFuture);
                        newChannelFutureWrapper.add(channelFutureWrapper);
                        finalUrl.add(newProviderUrl);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            finalChannelFutureWrappers.addAll(newChannelFutureWrapper);
            //最终更新服务在这里
            CONNECT_MAP.put(urlChangeWrapper.getServiceName(),finalChannelFutureWrappers);
        }
    }
}
