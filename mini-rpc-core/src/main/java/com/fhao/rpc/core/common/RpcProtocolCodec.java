package com.fhao.rpc.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

import static com.fhao.rpc.core.common.constants.RpcConstants.MAGIC_NUMBER;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 10:47</p>
 * <p>description: 必须和 LengthFieldBasedFrameDecoder 一起使用，确保接到的 ByteBuf 消息是完整的  </p>
 */
@ChannelHandler.Sharable
public class RpcProtocolCodec extends MessageToMessageCodec<ByteBuf, RpcProtocol> {
    public final int BASE_LENGTH = 2 + 4;

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol msg, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeShort(msg.getMagicNumber());
        byteBuf.writeInt(msg.getContentLength());
        byteBuf.writeBytes(msg.getContent());
        out.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        if (byteBuf.readableBytes() >= BASE_LENGTH) {
            //防止收到一些体积过大的数据包 目前限制在1000大小，后期版本这里是可配置模式
            if (!(byteBuf.readShort() == MAGIC_NUMBER)) {
                ctx.close();
                return;
            }
            int length = byteBuf.readInt();
            //说明剩余的数据包不是完整的，这里需要重置下读索引
            if (byteBuf.readableBytes() < length) {
                //数据包有异常
                ctx.close();
                return;
            }
            byte[] data = new byte[length];
            byteBuf.readBytes(data);
            RpcProtocol rpcProtocol = new RpcProtocol(data);
            out.add(rpcProtocol);
        }
    }
}
