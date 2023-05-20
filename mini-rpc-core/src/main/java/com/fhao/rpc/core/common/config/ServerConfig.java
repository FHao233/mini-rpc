package com.fhao.rpc.core.common.config;

/**
 * author: FHao
 * create time: 2023-04-26 15:13
 * description:
 */
public class ServerConfig {
    private Integer serverPort;

    private String registerAddr;
    private String applicationName;

    private String serverSerialize;

    public String getServerSerialize() {
        return serverSerialize;
    }

    public void setServerSerialize(String serverSerialize) {
        this.serverSerialize = serverSerialize;
    }

    public String getRegisterAddr() {
        return registerAddr;
    }

    public void setRegisterAddr(String registerAddr) {
        this.registerAddr = registerAddr;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }


}
