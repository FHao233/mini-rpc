package com.fhao.rpc.core.common;

import java.io.Serializable;
import java.util.Arrays;

import static com.fhao.rpc.core.common.constants.RpcConstants.MAGIC_NUMBER;

/**
 * author: FHao
 * create time: 2023-04-26 15:29
 * description:
 */
public class RpcProtocol implements Serializable {
    private static final long serialVersionUID = 5359096060555795690L;
    private short magicNumber = MAGIC_NUMBER;  //魔法数，主要是在做服务通讯的时候定义的一个安全检测，确认当前请求的协议是否合法。
    private int contentLength;
    private byte[] content;
    public RpcProtocol(byte[] content) {
        this.contentLength = content.length;
        this.content = content;
    }
    public short getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(short magicNumber) {
        this.magicNumber = magicNumber;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RpcProtocol{" +
                "magicNumber=" + magicNumber +
                ", contentLength=" + contentLength +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}

