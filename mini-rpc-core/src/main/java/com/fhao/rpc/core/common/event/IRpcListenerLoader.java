package com.fhao.rpc.core.common.event;

import com.fhao.rpc.core.common.event.listener.ServiceUpdateListener;
import com.fhao.rpc.core.common.utils.CommonUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 13:47</p>
 * <p>description:   </p>
 */
//这个类的作用是将IRpcListener的实现类注册到iRpcListenerList中，然后通过sendEvent方法将事件发送给iRpcListenerList中的所有IRpcListener的实现类
public class IRpcListenerLoader {
    private static List<IRpcListener> iRpcListenerList = new ArrayList<>();//这个list中存放的是IRpcListener的实现类

    private static ExecutorService eventThreadPool = Executors.newFixedThreadPool(2);//创建一个线程池

    public static void registerListener(IRpcListener iRpcListener) {
        iRpcListenerList.add(iRpcListener);
    }//将IRpcListener的实现类注册到iRpcListenerList中

    public void init() {
        registerListener(new ServiceUpdateListener());
    }//将IRpcListener的实现类注册到iRpcListenerList中

    /**
     * 获取接口上的泛型T
     *
     * @param o  接口
     */
    public static Class<?> getInterfaceT(Object o) {
        Type[] types = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) types[0];
        Type type = parameterizedType.getActualTypeArguments()[0];
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        return null;
    }

    public static void sendEvent(IRpcEvent iRpcEvent) {
        if(CommonUtils.isEmptyList(iRpcListenerList)){
            return;
        }
        for (IRpcListener<?> iRpcListener : iRpcListenerList) {
            Class<?> type = getInterfaceT(iRpcListener);
            if(type.equals(iRpcEvent.getClass())){
                eventThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            iRpcListener.callBack(iRpcEvent.getData());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

}
