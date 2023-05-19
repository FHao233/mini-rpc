package com.fhao.rpc.core.server;

import com.alibaba.fastjson.JSON;
import com.fhao.rpc.core.common.RpcInvocation;
import com.fhao.rpc.core.common.RpcProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.fhao.rpc.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 15:49</p>
 * <p>description:   </p>
 */
//服务端接收数据的Handler
public class ServerHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InvocationTargetException, IllegalAccessException {
        //服务端接收数据的时候统一以RpcProtocol协议的格式接收，具体的发送逻辑见文章下方客户端发送部分
        RpcProtocol rpcProtocol = (RpcProtocol) msg;
        //这里的CONTENT_LENGTH_FIELD_OFFSET和CONTENT_LENGTH_FIELD_LENGTH对应的是RpcProtocol对象的contentLength字段
        String json = new String(rpcProtocol.getContent(), 0, rpcProtocol.getContentLength());
        //将json转换成RpcInvocation对象
        RpcInvocation rpcInvocation = JSON.parseObject(json, RpcInvocation.class);
        //这里的PROVIDER_CLASS_MAP就是一开始预先在启动时候存储的Bean集合
        //根据目标服务名称找到对应的目标对象
        Object aimObject = PROVIDER_CLASS_MAP.get(rpcInvocation.getTargetServiceName());
        //通过反射找到目标对象的目标方法
        Method[] methods = aimObject.getClass().getDeclaredMethods();
        //这里的rpcInvocation.getTargetMethod()就是客户端发送过来的目标方法名称
        Object result = null;
        for (Method method : methods) {//遍历目标对象的所有方法
            //找到目标方法
            if(method.getName().equals(rpcInvocation.getTargetMethod())){
                // 通过反射找到目标对象，然后执行目标方法并返回对应值
                if (method.getReturnType().equals(Void.TYPE)) {
                    method.invoke(aimObject, rpcInvocation.getArgs());
                }else {
                    result = method.invoke(aimObject, rpcInvocation.getArgs());
                }
                break;
            }
        }
        rpcInvocation.setResponse(result);
        RpcProtocol respRpcProtocol = new RpcProtocol(JSON.toJSONString(rpcInvocation).getBytes());
        ctx.writeAndFlush(respRpcProtocol);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }
}
