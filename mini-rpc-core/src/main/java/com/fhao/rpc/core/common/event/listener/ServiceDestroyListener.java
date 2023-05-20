package com.fhao.rpc.core.common.event.listener;

import com.fhao.rpc.core.common.event.IRpcDestroyEvent;
import com.fhao.rpc.core.common.event.IRpcListener;
import com.fhao.rpc.core.registy.RegistryService;
import com.fhao.rpc.core.registy.URL;

import static com.fhao.rpc.core.common.cache.CommonServerCache.PROVIDER_URL_SET;
import static com.fhao.rpc.core.common.cache.CommonServerCache.REGISTRY_SERVICE;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 20:44</p>
 * <p>description:   </p>
 */
public class ServiceDestroyListener implements IRpcListener<IRpcDestroyEvent> {
    @Override
    public void callBack(Object t) {
        for (URL url : PROVIDER_URL_SET) {
            REGISTRY_SERVICE.unRegister(url);
        }
    }
}
