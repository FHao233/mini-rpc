package com.fhao.rpc.core.common.event.data;

import java.util.List;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 13:45</p>
 * <p>description:   </p>
 */
public class URLChangeWrapper {
    private String serviceName;

    private List<String> providerUrl;

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
