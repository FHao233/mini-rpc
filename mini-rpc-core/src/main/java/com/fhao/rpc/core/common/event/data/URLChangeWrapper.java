package com.fhao.rpc.core.common.event.data;

import java.util.List;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 13:45</p>
 * <p>description:   </p>
 */

    //这个类是用来封装一个服务的名称和这个服务的提供者的URL的
public class URLChangeWrapper {
    private String serviceName;//服务的名称

    private List<String> providerUrl;//服务的提供者的URL

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<String> getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(List<String> providerUrl) {
        this.providerUrl = providerUrl;
    }

    @Override
    public String toString() {
        return "URLChangeWrapper{" +
                "serviceName='" + serviceName + '\'' +
                ", providerUrl=" + providerUrl +
                '}';
    }
}
