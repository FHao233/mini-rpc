package com.fhao.rpc.core.common.config;

import java.io.IOException;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 15:40</p>
 * <p>description:   </p>
 */
public class PropertiesBootstrap {
    private volatile boolean configIsReady;

    public static final String REGISTER_TYPE = "irpc.registerType";
    public static final String SERVER_PORT = "irpc.serverPort";
    public static final String REGISTER_ADDRESS = "irpc.registerAddr";
    public static final String APPLICATION_NAME = "irpc.applicationName";
    public static final String PROXY_TYPE = "irpc.proxyType";
    public static final String ROUTER_STRATEGY = "irpc.routerStrategy";
    public static final String SERVER_SERIALIZE = "irpc.serverSerialize";
    public static final String CLIENT_SERIALIZE = "irpc.clientSerialize";

    public static ServerConfig loadServerConfigFromLocal() {
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("loadServerConfigFromLocal fail,e is {}", e);
        }
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerPort(PropertiesLoader.getPropertiesInteger(SERVER_PORT));
        serverConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        serverConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        serverConfig.setServerSerialize(PropertiesLoader.getPropertiesStr(SERVER_SERIALIZE));
        serverConfig.setRegisterType(PropertiesLoader.getPropertiesStr("irpc.registerType"));
        return serverConfig;
    }

    public static ClientConfig loadClientConfigFromLocal(){
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("loadClientConfigFromLocal fail,e is {}", e);
        }
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        clientConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        clientConfig.setProxyType(PropertiesLoader.getPropertiesStr(PROXY_TYPE));
        clientConfig.setRouterStrategy(PropertiesLoader.getPropertiesStr(ROUTER_STRATEGY));
        clientConfig.setClientSerialize(PropertiesLoader.getPropertiesStr(CLIENT_SERIALIZE));
        clientConfig.setRegisterType(PropertiesLoader.getPropertiesStr(REGISTER_TYPE));
        return clientConfig;
    }
}
