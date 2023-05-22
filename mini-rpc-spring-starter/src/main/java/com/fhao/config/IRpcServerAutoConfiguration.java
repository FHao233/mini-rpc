package com.fhao.config;

import com.fhao.common.IRpcService;
import com.fhao.rpc.core.common.event.IRpcListenerLoader;
import com.fhao.rpc.core.server.ApplicationShutdownHook;
import com.fhao.rpc.core.server.Server;
import com.fhao.rpc.core.server.ServiceWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;


/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-22 14:49</p>
 * <p>description:   </p>
 */
public class IRpcServerAutoConfiguration implements InitializingBean, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(IRpcServerAutoConfiguration.class);
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Server server = null;
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(IRpcService.class);
        if (beanMap.size() == 0) {
            //说明当前应用内部不需要对外暴露服务，无需执行下边多余的逻辑
            return;
        }
        //输出banner图案
        printBanner();
        long begin = System.currentTimeMillis();
        server = new Server();
        server.initServerConfig();
        IRpcListenerLoader iRpcListenerLoader = new IRpcListenerLoader();
        iRpcListenerLoader.init();
        for (String beanName : beanMap.keySet()) {
            Object bean = beanMap.get(beanName);
            IRpcService iRpcService = bean.getClass().getAnnotation(IRpcService.class);
            ServiceWrapper dataServiceServiceWrapper = new ServiceWrapper(bean, iRpcService.group());
            dataServiceServiceWrapper.setServiceToken(iRpcService.serviceToken());
            dataServiceServiceWrapper.setLimit(iRpcService.limit());
            server.exportService(dataServiceServiceWrapper);
            LOGGER.info(">>>>>>>>>>>>>>> [irpc] {} export success! >>>>>>>>>>>>>>> ", beanName);
        }
        long end = System.currentTimeMillis();
        ApplicationShutdownHook.registryShutdownHook();
        server.startApplication();
        LOGGER.info(" ================== [{}] started success in {}s ================== ", server.getServerConfig().getApplicationName(), ((double) end - (double) begin) / 1000);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void printBanner() {
        System.out.println();
        System.out.println("==============================================");
        System.out.println("|||---------- IRpc Starting Now! ----------|||");
        System.out.println("==============================================");
        System.out.println("version: 1.0.0");
        System.out.println();
    }
}
