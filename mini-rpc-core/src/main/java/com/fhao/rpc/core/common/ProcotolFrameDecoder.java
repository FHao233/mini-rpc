package com.fhao.rpc.core.common;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-21 19:48</p>
 * <p>description: 继承至LengthFieldBasedFrameDecoder，用来保证收到的消息是完整的</p>
 */
public class ProcotolFrameDecoder extends LengthFieldBasedFrameDecoder {
    public ProcotolFrameDecoder() {
        this(2048, 2, 4, 0, 0);
    }

    public ProcotolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
