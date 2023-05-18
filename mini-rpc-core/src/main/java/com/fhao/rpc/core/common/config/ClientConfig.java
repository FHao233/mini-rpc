package com.fhao.rpc.core.common.config;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 15:57</p>
 * <p>description:   </p>
 */
public class ClientConfig {
    private Integer port;

    private String serverAddr;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }
}
