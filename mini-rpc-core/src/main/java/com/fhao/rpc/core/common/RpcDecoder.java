package com.fhao.rpc.core.common;

import com.fhao.rpc.core.common.RpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static com.fhao.rpc.core.common.constants.RpcConstants.MAGIC_NUMBER;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 15:39</p>
 * <p>description: RPC解码器  </p>
 */
public class RpcDecoder extends ByteToMessageDecoder {
    /**
     * 协议的开头部分的标准长度
     */
    public final int BASE_LENGTH = 2 + 4;

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
