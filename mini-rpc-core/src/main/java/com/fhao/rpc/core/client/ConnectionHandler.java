package com.fhao.rpc.core.client;

import com.fhao.rpc.core.common.ChannelFutureWrapper;
import com.fhao.rpc.core.common.utils.CommonUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.fhao.rpc.core.common.cache.CommonClientCache.CONNECT_MAP;
import static com.fhao.rpc.core.common.cache.CommonClientCache.SERVER_ADDRESS;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 13:50</p>
 * <p>description:   </p>
 */
public class ConnectionHandler {
    /**
     * 核心的连接处理器
     * 专门用于负责和服务端构建连接通信
     */
    private static Bootstrap bootstrap;

    public static void setBootstrap(Bootstrap bootstrap) {
        ConnectionHandler.bootstrap = bootstrap;
    }

    /**
     * 构建单个连接通道 元操作，既要处理连接，还要统一将连接进行内存存储管理
     *
     * @param providerIp
     * @return
     * @throws InterruptedException
     */
    public static void connect(String providerServiceName, String providerIp) throws InterruptedException {
        if (bootstrap == null) {
            throw new RuntimeException("bootstrap can not be null");
        }
        //格式错误类型的信息
        if(!providerIp.contains(":")){
            return;
        }
        String[] providerAddress = providerIp.split(":");//这个providerAddress里面有两个元素，一个是ip，一个是port
        String ip = providerAddress[0];//ip
        Integer port = Integer.parseInt(providerAddress[1]);
        //到底这个channelFuture里面是什么
        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();//返回的是一个ChannelFuture对象
        ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();
        channelFutureWrapper.setChannelFuture(channelFuture);
        channelFutureWrapper.setHost(ip);
        channelFutureWrapper.setPort(port);
        SERVER_ADDRESS.add(providerIp);
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(providerServiceName);//获取到这个seviceName对应的ChannelFutureWrapper列表
        if (CommonUtils.isEmptyList(channelFutureWrappers)) {//如果这个列表是空的，那么就新建一个列表
            channelFutureWrappers = new ArrayList<>();
        }
        channelFutureWrappers.add(channelFutureWrapper);//将新的服务提供者的ChannelFutureWrapper对象加入到这个列表中
        CONNECT_MAP.put(providerServiceName, channelFutureWrappers);
    }

    /**
     * 构建ChannelFuture
     * @param ip
     * @param port
     * @return
     * @throws InterruptedException
     */
    public static ChannelFuture createChannelFuture(String ip,Integer port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        return channelFuture;
    }

    /**
     * 断开连接
     *
     * @param providerServiceName
     * @param providerIp
     */
    public static void disConnect(String providerServiceName, String providerIp) {
        SERVER_ADDRESS.remove(providerIp);
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(providerServiceName);
        if (CommonUtils.isNotEmptyList(channelFutureWrappers)) {
            Iterator<ChannelFutureWrapper> iterator = channelFutureWrappers.iterator();
            while (iterator.hasNext()) {
                ChannelFutureWrapper channelFutureWrapper = iterator.next();
                if (providerIp.equals(channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort())) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 默认走随机策略获取ChannelFuture
     * @param providerServiceName
     * @return
     */
    public static ChannelFuture getChannelFuture(String providerServiceName) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(providerServiceName);
        if (CommonUtils.isEmptyList(channelFutureWrappers)) {
            throw new RuntimeException("no provider exist for " + providerServiceName);
        }
        ChannelFuture channelFuture = channelFutureWrappers.get(new Random().nextInt(channelFutureWrappers.size())).getChannelFuture();
        return channelFuture;
    }
}
