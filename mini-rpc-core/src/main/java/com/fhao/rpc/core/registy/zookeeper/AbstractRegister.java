package com.fhao.rpc.core.registy.zookeeper;

import com.fhao.rpc.core.registy.RegistryService;
import com.fhao.rpc.core.registy.URL;

import java.util.List;

import static com.fhao.rpc.core.common.cache.CommonClientCache.SUBSCRIBE_SERVICE_LIST;
import static com.fhao.rpc.core.common.cache.CommonServerCache.PROVIDER_URL_SET;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 13:23</p>
 * <p>description:   </p>
 */
//抽象注册类
public abstract class AbstractRegister implements RegistryService {
    @Override
    //注册到集合中
    public void register(URL url) {
        PROVIDER_URL_SET.add(url);
    }

    @Override
    public void unRegister(URL url) {
        PROVIDER_URL_SET.remove(url);
    }

    @Override
    public void subscribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.add(url.getServiceName());
    }

    @Override
    public void doUnSubscribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.remove(url.getServiceName());
    }
    /**
     * 留给子类扩展
     *
     * @param url
     */
    public abstract void doAfterSubscribe(URL url);

    /**
     * 留给子类扩展
     *
     * @param url
     */
    public abstract void doBeforeSubscribe(URL url);

    /**
     * 留给子类扩展
     *
     * @param serviceName
     * @return
     */
    public abstract List<String> getProviderIps(String serviceName);



}
