package com.fhao.rpc.core.common;

import io.netty.channel.ChannelFuture;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 13:42</p>
 * <p>description:   </p>
 */
//这个类保存了一个ChannelFuture对象，一个host和一个port
    //这个类的作用是封装一个ChannelFuture对象，一个host和一个port
public class ChannelFutureWrapper {
    private ChannelFuture channelFuture;

    private String host;

    private Integer port;

    private Integer weight;
    private String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getWeight() {
        return weight;
    }

    public ChannelFutureWrapper() {
    }

    public ChannelFutureWrapper(String host, Integer port, Integer weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
    }
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public void setChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
