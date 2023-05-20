package com.fhao.rpc.core.server;

import com.fhao.rpc.core.common.event.IRpcDestroyEvent;
import com.fhao.rpc.core.common.event.IRpcListenerLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 20:51</p>
 * <p>description: 监听java进程被关闭  </p>
 */
public class ApplicationShutdownHook {
    public static Logger LOGGER = LoggerFactory.getLogger(ApplicationShutdownHook.class);
    public static void registryShutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("[registryShutdownHook] ==== ");
                IRpcListenerLoader.sendSyncEvent(new IRpcDestroyEvent("destroy"));
                System.out.println("destory");
            }
        }));
    }
}
