package com.fhao.rpc.core.common;

import io.netty.channel.ChannelFuture;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 13:42</p>
 * <p>description:   </p>
 */
public class ChannelFutureWrapper {
    private ChannelFuture channelFuture;

    private String host;

    private Integer port;

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