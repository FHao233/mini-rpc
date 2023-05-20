package com.fhao.rpc.core.client;

import com.alibaba.fastjson.JSON;
import com.fhao.rpc.core.common.*;
import com.fhao.rpc.core.common.config.ClientConfig;
import com.fhao.rpc.core.common.config.PropertiesBootstrap;
import com.fhao.rpc.core.common.event.IRpcListenerLoader;
import com.fhao.rpc.core.common.utils.CommonUtils;
import com.fhao.rpc.core.proxy.javassist.JavassistProxyFactory;
import com.fhao.rpc.core.proxy.jdk.JDKProxyFactory;
import com.fhao.rpc.core.registy.URL;
import com.fhao.rpc.core.registy.zookeeper.AbstractRegister;
import com.fhao.rpc.core.registy.zookeeper.ZookeeperRegister;
import com.fhao.rpc.core.router.RandomRouterImpl;
import com.fhao.rpc.core.router.RotateRouterImpl;
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

import java.util.List;

import static com.fhao.rpc.core.common.cache.CommonClientCache.SEND_QUEUE;
import static com.fhao.rpc.core.common.cache.CommonClientCache.SUBSCRIBE_SERVICE_LIST;
import static com.fhao.rpc.core.common.cache.CommonClientCache.IROUTER;
import static com.fhao.rpc.core.common.constants.RpcConstants.*;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 15:56</p>
 * <p>description:   </p>
 */
public class Client {
    private Logger logger = LoggerFactory.getLogger(Client.class);
    LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    public static EventLoopGroup clientGroup = new NioEventLoopGroup();
    private ClientConfig clientConfig;

    private AbstractRegister abstractRegister;

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
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(RPC_PROTOCOL_CODEC);
                ch.pipeline().addLast(new ClientHandler());
            }
        });
        iRpcListenerLoader = new IRpcListenerLoader();
        iRpcListenerLoader.init();
        this.clientConfig = PropertiesBootstrap.loadClientConfigFromLocal();
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
     * @param serviceBean
     */
    public void doSubscribeService(Class serviceBean) {
        if (abstractRegister == null) {
            abstractRegister = new ZookeeperRegister(clientConfig.getRegisterAddr());
        }
        URL url = new URL();
        url.setApplicationName(clientConfig.getApplicationName());
        url.setServiceName(serviceBean.getName());
        url.addParameter("host", CommonUtils.getIpAddress());
        abstractRegister.subscribe(url);
    }

    /**
     * todo
     * 后续可以考虑加入spi
     */
    private void initClientConfig() {
        //初始化路由策略
        String routerStrategy = clientConfig.getRouterStrategy();
        if (RANDOM_ROUTER_TYPE.equals(routerStrategy)) {
            IROUTER = new RandomRouterImpl();
        } else if (ROTATE_ROUTER_TYPE.equals(routerStrategy)) {
            IROUTER = new RotateRouterImpl();
        }
    }

    /**
     * 开始和各个provider建立连接
     */
    public void doConnectServer() {
        for (URL providerURL : SUBSCRIBE_SERVICE_LIST) {
            List<String> providerIps = abstractRegister.getProviderIps(providerURL.getServiceName());
            for (String providerIp : providerIps) {
                try {
                    ConnectionHandler.connect(providerURL.getServiceName(), providerIp);//建立连接
                } catch (InterruptedException e) {
                    logger.error("[doConnectServer] connect fail ", e);
                }
            }
            URL url = new URL();
            url.addParameter("servicePath",providerURL.getServiceName()+"/provider");
            url.addParameter("providerIps", JSON.toJSONString(providerIps));
            //客户端在此新增一个订阅的功能
            abstractRegister.doAfterSubscribe(url);
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
                    String json = JSON.toJSONString(data); //序列化
                    RpcProtocol rpcProtocol = new RpcProtocol(json.getBytes());//封装成rpc协议
                    ChannelFuture channelFuture = ConnectionHandler.getChannelFuture(data.getTargetServiceName());//获取channel
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
        DataService dataService = rpcReference.get(DataService.class);//获取代理对象
        client.doSubscribeService(DataService.class); //订阅服务
        ConnectionHandler.setBootstrap(client.getBootstrap()); //设置bootstrap

        client.doConnectServer();//建立连接
        client.startClient();
        for (int i = 0; i < 100; i++) {
            try {
                String result = dataService.sendData("test");
                System.out.println(result);
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
//        }
    }
}
