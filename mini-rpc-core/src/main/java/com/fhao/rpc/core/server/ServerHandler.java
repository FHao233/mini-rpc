package com.fhao.rpc.core.server;

import com.fhao.rpc.core.common.RpcProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.fhao.rpc.core.common.cache.CommonServerCache.*;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 15:49</p>
 * <p>description:   </p>
 */
//服务端接收数据的Handler
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        ServerChannelReadData serverChannelReadData = new ServerChannelReadData();
        serverChannelReadData.setChannelHandlerContext(ctx);
        serverChannelReadData.setRpcProtocol((RpcProtocol) msg);
        SERVER_CHANNEL_DISPATCHER.add(serverChannelReadData);

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }
}
