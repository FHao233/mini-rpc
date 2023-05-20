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
 * <p>description:   </p>
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
            //防止收到一些体积过大的数据包
            if (byteBuf.readableBytes() > 1000) {
                //这里需要重置下读索引，否则会导致内存泄漏
                byteBuf.skipBytes(byteBuf.readableBytes());
            }
            int beginReader;//记录包头开始的index
            while (true) {
                beginReader = byteBuf.readerIndex();//记录包头开始的index
                byteBuf.markReaderIndex();//标记包头开始的index
                //这里对应了RpcProtocol的魔数
                if (byteBuf.readShort() == MAGIC_NUMBER) {
                    break;
                } else {
                    // 不是魔数开头，说明是非法的客户端发来的数据包
                    ctx.close();
                    return;
                }
            }
            //这里对应了RpcProtocol对象的contentLength字段
            int length = byteBuf.readInt();
            //说明剩余的数据包不是完整的，这里需要重置下读索引，等待下一次的数据包到来
            // ，这里需要注意的是，这里的读索引是从上面的while循环中标记的
            if (byteBuf.readableBytes() < length) {
                byteBuf.readerIndex(beginReader);
                return;
            }
            //这里其实就是实际的RpcProtocol对象的content字段
            byte[] data = new byte[length];
            byteBuf.readBytes(data);
            RpcProtocol rpcProtocol = new RpcProtocol(data);
            out.add(rpcProtocol);
        }
    }
}
