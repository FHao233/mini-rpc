package com.fhao.rpc.core.common.config;

import java.io.IOException;

import static com.fhao.rpc.core.common.constants.RpcConstants.*;

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
    public static final String CLIENT_DEFAULT_TIME_OUT = "irpc.client.default.timeout";
    public static final String SERVER_BIZ_THREAD_NUMS = "irpc.server.biz.thread.nums";
    public static final String SERVER_QUEUE_SIZE = "irpc.server.queue.size";

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
        serverConfig.setServerSerialize(PropertiesLoader.getPropertiesStrDefault(SERVER_SERIALIZE,JDK_SERIALIZE_TYPE));
        serverConfig.setRegisterType(PropertiesLoader.getPropertiesStr(REGISTER_TYPE));
        serverConfig.setServerBizThreadNums(PropertiesLoader.getPropertiesIntegerDefault(SERVER_BIZ_THREAD_NUMS,DEFAULT_THREAD_NUMS));
        serverConfig.setServerQueueSize(PropertiesLoader.getPropertiesIntegerDefault(SERVER_QUEUE_SIZE,DEFAULT_QUEUE_SIZE));
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
        clientConfig.setRegisterType(PropertiesLoader.getPropertiesStr(REGISTER_TYPE));
        clientConfig.setProxyType(PropertiesLoader.getPropertiesStrDefault(PROXY_TYPE,JDK_PROXY_TYPE));
        clientConfig.setRouterStrategy(PropertiesLoader.getPropertiesStrDefault(ROUTER_STRATEGY,RANDOM_ROUTER_TYPE));
        clientConfig.setClientSerialize(PropertiesLoader.getPropertiesStrDefault(CLIENT_SERIALIZE,JDK_SERIALIZE_TYPE));
        clientConfig.setTimeOut(PropertiesLoader.getPropertiesIntegerDefault(CLIENT_DEFAULT_TIME_OUT,DEFAULT_TIMEOUT));
        return clientConfig;
    }
}
