package com.fhao.rpc.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <p>author: FHao</p>
 * create time: 2023-04-26 15:35
 * description: RPC编码器
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol>{
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeShort(msg.getMagicNumber());
        byteBuf.writeInt(msg.getContentLength());
        byteBuf.writeBytes(msg.getContent());
    }
}
