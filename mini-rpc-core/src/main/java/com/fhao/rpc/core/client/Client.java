package com.fhao.rpc.core.client;

import com.alibaba.fastjson.JSON;
import com.fhao.rpc.core.common.*;
import com.fhao.rpc.core.common.config.ClientConfig;
import com.fhao.rpc.core.common.config.PropertiesBootstrap;
import com.fhao.rpc.core.common.event.IRpcListenerLoader;
import com.fhao.rpc.core.common.utils.CommonUtils;
import com.fhao.rpc.core.filter.IClientFilter;
import com.fhao.rpc.core.filter.client.ClientFilterChain;
import com.fhao.rpc.core.filter.client.ClientLogFilterImpl;
import com.fhao.rpc.core.filter.client.DirectInvokeFilterImpl;
import com.fhao.rpc.core.filter.client.GroupFilterImpl;
import com.fhao.rpc.core.proxy.javassist.JavassistProxyFactory;
import com.fhao.rpc.core.proxy.jdk.JDKProxyFactory;
import com.fhao.rpc.core.registy.RegistryService;
import com.fhao.rpc.core.registy.URL;
import com.fhao.rpc.core.registy.zookeeper.AbstractRegister;
import com.fhao.rpc.core.registy.zookeeper.ZookeeperRegister;
import com.fhao.rpc.core.router.IRouter;
import com.fhao.rpc.core.router.RandomRouterImpl;
import com.fhao.rpc.core.router.RotateRouterImpl;
import com.fhao.rpc.core.serialize.SerializeFactory;
import com.fhao.rpc.core.serialize.fastjson.FastJsonSerializeFactory;
import com.fhao.rpc.core.serialize.jdk.JdkSerializeFactory;
import com.fhao.rpc.interfaces.DataService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.fhao.rpc.core.common.cache.CommonClientCache.*;
import static com.fhao.rpc.core.common.constants.RpcConstants.*;
import static com.fhao.rpc.core.spi.ExtensionLoader.EXTENSION_LOADER_CLASS_CACHE;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 15:56</p>
 * <p>description:   </p>
 */
public class Client {
    private Logger logger = LoggerFactory.getLogger(Client.class);
    LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
//    public static EventLoopGroup clientGroup = new NioEventLoopGroup();
    private ClientConfig clientConfig;

    private AbstractRegister ABSTRACT_REGISTER;

    private IRpcListenerLoader iRpcListenerLoader;

    private Bootstrap bootstrap = new Bootstrap();

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    RpcProtocolCodec RPC_PROTOCOL_CODEC = new RpcProtocolCodec();

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public RpcReference initClientApplication() {
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(RPC_PROTOCOL_CODEC);
//                ch.pipeline().addLast(new RpcDecoder());
//                ch.pipeline().addLast(new RpcEncoder());
                ch.pipeline().addLast(new ClientHandler());
            }
        });
        iRpcListenerLoader = new IRpcListenerLoader();
        iRpcListenerLoader.init();
        this.clientConfig = PropertiesBootstrap.loadClientConfigFromLocal();
        CLIENT_CONFIG = this.clientConfig;
        RpcReference rpcReference;
        if (JAVASSIST_PROXY_TYPE.equals(clientConfig.getProxyType())) {
            rpcReference = new RpcReference(new JavassistProxyFactory());
        } else {
            rpcReference = new RpcReference(new JDKProxyFactory());
        }
        return rpcReference;
    }

    /**
     * 启动服务之前需要预先订阅对应的dubbo服务
     *
     * @param serviceBean
     */
    public void doSubscribeService(Class serviceBean) {
        if (ABSTRACT_REGISTER == null) {
//            ABSTRACT_REGISTER = new ZookeeperRegister(clientConfig.getRegisterAddr());
            try {
                EXTENSION_LOADER.loadExtension(RegistryService.class);
                Map<String, Class> registerMap = EXTENSION_LOADER_CLASS_CACHE.get(RegistryService.class.getName());
                Class registerClass =  registerMap.get(clientConfig.getRegisterType());
                ABSTRACT_REGISTER = (AbstractRegister) registerClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("registryServiceType unKnow,error is ", e);
            }

        }
        URL url = new URL();
        url.setApplicationName(clientConfig.getApplicationName());
        url.setServiceName(serviceBean.getName());
        url.addParameter("host", CommonUtils.getIpAddress());
        Map<String, String> result = ABSTRACT_REGISTER.getServiceWeightMap(serviceBean.getName());
        URL_MAP.put(serviceBean.getName(), result);
        ABSTRACT_REGISTER.subscribe(url);
    }

    /**
     * todo
     * 后续可以考虑加入spi
     */
    private void initClientConfig() throws Exception {
        //初始化路由策略
        EXTENSION_LOADER.loadExtension(IRouter.class);
        String routerStrategy = clientConfig.getRouterStrategy();
        LinkedHashMap<String, Class> iRouterMap = EXTENSION_LOADER_CLASS_CACHE.get(IRouter.class.getName());
        Class iRouterClass = iRouterMap.get(routerStrategy);
        if (iRouterClass == null) {
            throw new RuntimeException("no match routerStrategy for " + routerStrategy);
        }
        IROUTER = (IRouter) iRouterClass.getDeclaredConstructor().newInstance();
        //初始化序列化框架
        EXTENSION_LOADER.loadExtension(SerializeFactory.class);
        String clientSerialize = clientConfig.getClientSerialize();
        LinkedHashMap<String, Class> serializerMap = EXTENSION_LOADER_CLASS_CACHE.get(SerializeFactory.class.getName());
        Class serializerClass = serializerMap.get(clientSerialize);
        if (serializerClass == null) {
            throw new RuntimeException("no match serialize type for " + clientSerialize);
        }
        CLIENT_SERIALIZE_FACTORY = (SerializeFactory) serializerClass.getDeclaredConstructor().newInstance();
        logger.debug("the client serialize type is {}", clientSerialize);
        //初始化过滤链
        EXTENSION_LOADER.loadExtension(IClientFilter.class);
        ClientFilterChain clientFilterChain = new ClientFilterChain();
        LinkedHashMap<String, Class> iClientMap = EXTENSION_LOADER_CLASS_CACHE.get(IClientFilter.class.getName());
        for (String implClassName : iClientMap.keySet()) {
            Class iClientFilterClass = iClientMap.get(implClassName);
            if (iClientFilterClass == null) {
                throw new RuntimeException("no match iClientFilter for " + iClientFilterClass);
            }
            clientFilterChain.addClientFilter((IClientFilter) iClientFilterClass.getDeclaredConstructor().newInstance());
        }
        CLIENT_FILTER_CHAIN = clientFilterChain;
    }

    /**
     * 开始和各个provider建立连接
     */
    public void doConnectServer() {
        for (URL providerURL : SUBSCRIBE_SERVICE_LIST) {
            List<String> providerIps = ABSTRACT_REGISTER.getProviderIps(providerURL.getServiceName());
            for (String providerIp : providerIps) {
                try {
                    ConnectionHandler.connect(providerURL.getServiceName(), providerIp);//建立连接
                } catch (InterruptedException e) {
                    logger.error("[doConnectServer] connect fail ", e);
                }
            }
            URL url = new URL();
            url.addParameter("servicePath", providerURL.getServiceName() + "/provider");
            url.addParameter("providerIps", JSON.toJSONString(providerIps));
            //客户端在此新增一个订阅的功能
            ABSTRACT_REGISTER.doAfterSubscribe(url);
        }
    }

    /**
     * 开启发送线程
     *
     * @param
     */
    public void startClient() {
        Thread asyncSendJob = new Thread(new AsyncSendJob());
        asyncSendJob.start();
    }

    class AsyncSendJob implements Runnable {

        public AsyncSendJob() {
        }

        @Override
        public void run() {
            while (true) {
                try {
                    //阻塞模式
                    RpcInvocation data = SEND_QUEUE.take(); //从队列中取出数据
                    RpcProtocol rpcProtocol = new RpcProtocol(CLIENT_SERIALIZE_FACTORY.serialize(data));//封装成rpc协议
                    ChannelFuture channelFuture = ConnectionHandler.getChannelFuture(data);//获取channel
                    channelFuture.channel().writeAndFlush(rpcProtocol);//发送数据
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        Client client = new Client();
        RpcReference rpcReference = client.initClientApplication();
        client.initClientConfig();
        RpcReferenceWrapper<DataService> rpcReferenceWrapper = new RpcReferenceWrapper<>();
        rpcReferenceWrapper.setAimClass(DataService.class);
        rpcReferenceWrapper.setGroup("dev");
        rpcReferenceWrapper.setServiceToken("token-a");
        rpcReferenceWrapper.setTimeOut(3000);
        rpcReferenceWrapper.setAsync(false);
        DataService dataService = rpcReference.get(rpcReferenceWrapper);//获取代理对象
        client.doSubscribeService(DataService.class); //订阅服务
        ConnectionHandler.setBootstrap(client.getBootstrap()); //设置bootstrap

        client.doConnectServer();//建立连接
        client.startClient();
        for (int i = 0; i < 100; i++) {
            try {
                String result = dataService.sendData("test");
                System.out.println(result);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        }
    }
}
