package com.fhao.rpc.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static com.fhao.rpc.core.common.constants.RpcConstants.DEFAULT_DECODE_CHAR;

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
        byteBuf.writeBytes(DEFAULT_DECODE_CHAR.getBytes());
    }
}
