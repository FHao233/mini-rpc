package com.fhao.rpc.core.common.config;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 15:57</p>
 * <p>description:   </p>
 */
public class ClientConfig {
    private String applicationName;

    private String registerAddr;
    /**
     * 代理类型 example: jdk,javassist
     */
    private String proxyType;

    public String getRouterStrategy() {
        return routerStrategy;
    }

    public void setRouterStrategy(String routerStrategy) {
        this.routerStrategy = routerStrategy;
    }

    /**
     * 负载均衡策略 example:random,rotate
     */
    private String routerStrategy;
    public String getProxyType() {
        return proxyType;
    }

    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
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
}
