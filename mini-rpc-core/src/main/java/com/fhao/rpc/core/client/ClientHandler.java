package com.fhao.rpc.core.client;

import com.alibaba.fastjson.JSON;
import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.common.RpcProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import static com.fhao.rpc.core.common.cache.CommonClientCache.RESP_MAP;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 16:02</p>
 * <p>description:   </p>
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcProtocol rpcProtocol = (RpcProtocol) msg;//这里的msg就是RpcProtocol对象
        byte[] reqContent = rpcProtocol.getContent();//获取RpcProtocol对象的内容
        String json = new String(reqContent,0,reqContent.length);//将内容转换成json
        RpcInvocation rpcInvocation = JSON.parseObject(json,RpcInvocation.class);//将json转换成RpcInvocation对象
        if(!RESP_MAP.containsKey(rpcInvocation.getUuid())){//如果客户端缓存中没有对应的请求对象，则抛出异常
            throw new IllegalArgumentException("server response is error!");
        }
        RESP_MAP.put(rpcInvocation.getUuid(),rpcInvocation);//将RpcInvocation对象存入客户端缓存
        ReferenceCountUtil.release(msg);//释放资源
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if(channel.isActive()){
            ctx.close();
        }
    }
}
