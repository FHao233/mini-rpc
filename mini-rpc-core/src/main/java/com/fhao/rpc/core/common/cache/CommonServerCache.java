package com.fhao.rpc.core.common.cache;

import com.fhao.rpc.core.common.config.ServerConfig;
import com.fhao.rpc.core.dispatcher.ServerChannelDispatcher;
import com.fhao.rpc.core.filter.server.ServerFilterChain;
import com.fhao.rpc.core.registy.RegistryService;
import com.fhao.rpc.core.registy.URL;
import com.fhao.rpc.core.serialize.SerializeFactory;
import com.fhao.rpc.core.server.ServiceWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 15:52</p>
 * <p>description:   </p>
 */
public class CommonServerCache {
    //用于存储提供者类的名称和实例
    public static final Map<String,Object> PROVIDER_CLASS_MAP = new HashMap<>();
    //用于存储提供者的URL地址
    public static final Set<URL> PROVIDER_URL_SET = new HashSet<>();

    public static RegistryService REGISTRY_SERVICE;
    public static SerializeFactory SERVER_SERIALIZE_FACTORY;

    public static ServerFilterChain SERVER_FILTER_CHAIN;
    public static final Map<String, ServiceWrapper>
            PROVIDER_SERVICE_WRAPPER_MAP = new ConcurrentHashMap<>();

    public static ServerConfig SERVER_CONFIG;
    public static Boolean IS_STARTED = false;

    public static ServerChannelDispatcher SERVER_CHANNEL_DISPATCHER = new ServerChannelDispatcher();

}
