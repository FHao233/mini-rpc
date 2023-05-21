package com.fhao.rpc.core.server;

import com.fhao.rpc.core.common.ProcotolFrameDecoder;
import com.fhao.rpc.core.common.RpcDecoder;
import com.fhao.rpc.core.common.RpcEncoder;
import com.fhao.rpc.core.common.RpcProtocolCodec;
import com.fhao.rpc.core.common.config.ServerConfig;
import com.fhao.rpc.core.common.event.IRpcListenerLoader;
import com.fhao.rpc.core.common.utils.CommonUtils;
import com.fhao.rpc.core.filter.IServerFilter;
import com.fhao.rpc.core.filter.server.ServerFilterChain;
import com.fhao.rpc.core.filter.server.ServerLogFilterImpl;
import com.fhao.rpc.core.filter.server.ServerTokenFilterImpl;
import com.fhao.rpc.core.registy.RegistryService;
import com.fhao.rpc.core.registy.URL;
import com.fhao.rpc.core.registy.zookeeper.ZookeeperRegister;
import com.fhao.rpc.core.serialize.SerializeFactory;
import com.fhao.rpc.core.serialize.fastjson.FastJsonSerializeFactory;
import com.fhao.rpc.core.serialize.jdk.JdkSerializeFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import com.fhao.rpc.core.common.config.PropertiesBootstrap;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;

import static com.fhao.rpc.core.common.cache.CommonClientCache.EXTENSION_LOADER;
import static com.fhao.rpc.core.common.cache.CommonServerCache.*;
import static com.fhao.rpc.core.common.constants.RpcConstants.FAST_JSON_SERIALIZE_TYPE;
import static com.fhao.rpc.core.common.constants.RpcConstants.JDK_SERIALIZE_TYPE;
import static com.fhao.rpc.core.spi.ExtensionLoader.EXTENSION_LOADER_CLASS_CACHE;

/**
 * author: FHao
 * create time: 2023-04-26 15:11
 * description:
 */
public class Server {
    Logger logger = LoggerFactory.getLogger(Server.class);
    private static EventLoopGroup bossGroup = null;
    private static EventLoopGroup workerGroup = null;
    RpcProtocolCodec RPC_PROTOCOL_CODEC = new RpcProtocolCodec();

    private static IRpcListenerLoader iRpcListenerLoader;
    private ServerConfig serverConfig;
    private RegistryService registryService;

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void startApplication() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_SNDBUF, 16 * 1024)
                .option(ChannelOption.SO_RCVBUF, 16 * 1024)
                .option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                System.out.println("初始化provider过程");
                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                ch.pipeline().addLast(RPC_PROTOCOL_CODEC);
//                ch.pipeline().addLast(new RpcDecoder());
//                ch.pipeline().addLast(new RpcEncoder());
                ch.pipeline().addLast(new ServerHandler());
            }

        });
        this.batchExportUrl();
        bootstrap.bind(serverConfig.getServerPort()).sync();
        SERVER_CHANNEL_DISPATCHER.startDataConsume();//开始准备接收请求的任务
        IS_STARTED = true;
    }

    public void registyService(Object serviceBean) {
        if (serviceBean.getClass().getInterfaces().length == 0) {
            throw new RuntimeException("service must had interfaces!");
        }
        Class[] classes = serviceBean.getClass().getInterfaces();
        if (classes.length > 1) {
            throw new RuntimeException("service must only had one interfaces!");
        }
        Class interfaceClass = classes[0];
        //需要注册的对象统一放在一个MAP集合中进行管理
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(), serviceBean);
    }

    /**
     * 暴露服务信息
     *
     * @param serviceWrapper
     */
    public void exportService(ServiceWrapper serviceWrapper) {
        Object serviceBean = serviceWrapper.getServiceObj();
        if (serviceBean.getClass().getInterfaces().length == 0) {
            throw new RuntimeException("service must had interfaces!");
        }
        Class[] classes = serviceBean.getClass().getInterfaces();
        if (classes.length > 1) {
            throw new RuntimeException("service must only had one interfaces!");
        }
        if (registryService == null) {
            registryService = new ZookeeperRegister(serverConfig.getRegisterAddr());
        }
        //默认选择该对象的第一个实现接口
        Class interfaceClass = classes[0];
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(), serviceBean);
        URL url = new URL();
        url.setServiceName(interfaceClass.getName());
        url.setApplicationName(serverConfig.getApplicationName());
        url.addParameter("host", CommonUtils.getIpAddress());
        url.addParameter("port", String.valueOf(serverConfig.getServerPort()));
        url.addParameter("group", String.valueOf(serviceWrapper.getGroup()));
        url.addParameter("limit", String.valueOf(serviceWrapper.getLimit()));
        PROVIDER_URL_SET.add(url);
        if (CommonUtils.isNotEmpty(serviceWrapper.getServiceToken())) {
            PROVIDER_SERVICE_WRAPPER_MAP.put(interfaceClass.getName(), serviceWrapper);
        }
    }

    public void batchExportUrl() { //这个函数的内部设计就是为了将服务端的具体服务都暴露到注册中心，方便客户端进行调用。
        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (URL url : PROVIDER_URL_SET) {
                    registryService.register(url);
                }
            }
        });
        task.start();
    }

    public void initServerConfig() throws Exception {
        ServerConfig serverConfig = PropertiesBootstrap.loadServerConfigFromLocal();
        this.setServerConfig(serverConfig);
        SERVER_CONFIG = serverConfig;
        //初始化线程池和队列的配置
        SERVER_CHANNEL_DISPATCHER.init(SERVER_CONFIG.getServerQueueSize(), SERVER_CONFIG.getServerBizThreadNums());
        //序列化技术初始化
        String serverSerialize = serverConfig.getServerSerialize();
        EXTENSION_LOADER.loadExtension(SerializeFactory.class);
        LinkedHashMap<String, Class> serializeFactoryClassMap = EXTENSION_LOADER_CLASS_CACHE.get(SerializeFactory.class.getName());
        Class serializeFactoryClass = serializeFactoryClassMap.get(serverSerialize);
        logger.debug("the client serialize type is {}", serverSerialize);
        if (serializeFactoryClass == null) {
            throw new RuntimeException("no match serialize type for " + serverSerialize);
        }
        SERVER_SERIALIZE_FACTORY = (SerializeFactory) serializeFactoryClass.getDeclaredConstructor().newInstance();
        //过滤链技术初始化
        EXTENSION_LOADER.loadExtension(IServerFilter.class);
        LinkedHashMap<String, Class> iServerFilterClassMap = EXTENSION_LOADER_CLASS_CACHE.get(IServerFilter.class.getName());
        ServerFilterChain serverFilterChain = new ServerFilterChain();
        for (String iServerFilterKey : iServerFilterClassMap.keySet()) {
            Class iServerFilterClass = iServerFilterClassMap.get(iServerFilterKey);
            if (iServerFilterClass == null) {
                throw new RuntimeException("no match iServerFilter type for " + iServerFilterKey);
            }
            serverFilterChain.addServerFilter((IServerFilter) iServerFilterClass.getDeclaredConstructor().newInstance());
        }
        SERVER_FILTER_CHAIN = serverFilterChain;
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.initServerConfig();
        iRpcListenerLoader = new IRpcListenerLoader();
        iRpcListenerLoader.init();
        ServiceWrapper dataServiceServiceWrapper = new ServiceWrapper(new DataServiceImpl(), "dev");
        dataServiceServiceWrapper.setServiceToken("token-a");
        dataServiceServiceWrapper.setLimit(2);
        ServiceWrapper userServiceServiceWrapper = new ServiceWrapper(new UserServiceImpl(), "dev");
        userServiceServiceWrapper.setServiceToken("token-b");
        userServiceServiceWrapper.setLimit(2);
        server.exportService(dataServiceServiceWrapper);
        server.exportService(userServiceServiceWrapper);
        ApplicationShutdownHook.registryShutdownHook();
        server.startApplication();
    }
}