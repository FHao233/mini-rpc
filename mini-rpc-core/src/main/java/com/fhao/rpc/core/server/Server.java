package com.fhao.rpc.core.server;

import com.fhao.rpc.core.common.RpcProtocolCodec;
import com.fhao.rpc.core.common.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import static com.fhao.rpc.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;
/**
 * author: FHao
 * create time: 2023-04-26 15:11
 * description:
 */
public class Server {
    private static EventLoopGroup bossGroup = null;
    private static EventLoopGroup workerGroup = null;
    RpcProtocolCodec RPC_PROTOCOL_CODEC = new RpcProtocolCodec();

    private ServerConfig serverConfig;
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
//                ch.pipeline().addLast(new RpcEncoder());
//                ch.pipeline().addLast(new RpcDecoder());
                ch.pipeline().addLast(RPC_PROTOCOL_CODEC);
                ch.pipeline().addLast(new ServerHandler());
            }

        });
        bootstrap.bind(serverConfig.getPort()).sync();
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
    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(9090);
        server.setServerConfig(serverConfig);
        server.registyService(new DataServiceImpl());
        server.startApplication();
    }
}