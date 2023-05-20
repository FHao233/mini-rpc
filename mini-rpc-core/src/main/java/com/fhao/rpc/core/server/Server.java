package com.fhao.rpc.core.server;

import com.fhao.rpc.core.common.RpcProtocolCodec;
import com.fhao.rpc.core.common.config.ServerConfig;
import com.fhao.rpc.core.common.utils.CommonUtils;
import com.fhao.rpc.core.registy.RegistryService;
import com.fhao.rpc.core.registy.URL;
import com.fhao.rpc.core.registy.zookeeper.ZookeeperRegister;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.fhao.rpc.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;
import static com.fhao.rpc.core.common.cache.CommonServerCache.PROVIDER_URL_SET;
import static com.fhao.rpc.core.common.cache.CommonServerCache.SERVER_SERIALIZE_FACTORY;
import static com.fhao.rpc.core.common.constants.RpcConstants.FAST_JSON_SERIALIZE_TYPE;
import static com.fhao.rpc.core.common.constants.RpcConstants.JDK_SERIALIZE_TYPE;

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
                ch.pipeline().addLast(RPC_PROTOCOL_CODEC);
                ch.pipeline().addLast(new ServerHandler());
            }

        });
        this.batchExportUrl();

        bootstrap.bind(serverConfig.getServerPort()).sync();
}
    public void registyService(Object serviceBean){
        if(serviceBean.getClass().getInterfaces().length==0){
            throw new RuntimeException("service must had interfaces!");
        }
        Class[] classes = serviceBean.getClass().getInterfaces();
        if(classes.length>1){
            throw new RuntimeException("service must only had one interfaces!");
        }
        Class interfaceClass = classes[0];
        //需要注册的对象统一放在一个MAP集合中进行管理
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(), serviceBean);
    }

    /**
     * 暴露服务信息
     *
     * @param serviceBean
     */
    public void exportService(Object serviceBean) {
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
        PROVIDER_URL_SET.add(url);
    }

    public void batchExportUrl(){ //这个函数的内部设计就是为了将服务端的具体服务都暴露到注册中心，方便客户端进行调用。
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
    public void initServerConfig() {
        ServerConfig serverConfig = PropertiesBootstrap.loadServerConfigFromLocal();
        this.setServerConfig(serverConfig);
        String serverSerialize = serverConfig.getServerSerialize();
        switch (serverSerialize){
            case JDK_SERIALIZE_TYPE:
                SERVER_SERIALIZE_FACTORY = new JdkSerializeFactory();
                break;
            case FAST_JSON_SERIALIZE_TYPE:
                SERVER_SERIALIZE_FACTORY = new FastJsonSerializeFactory();
                break;
            default:
                throw new RuntimeException("no match serialize type for " + serverSerialize);
        }
        logger.debug("the server serialize type is {}", serverSerialize);
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        server.initServerConfig();
        server.exportService(new DataServiceImpl());
        server.startApplication();
    }
}