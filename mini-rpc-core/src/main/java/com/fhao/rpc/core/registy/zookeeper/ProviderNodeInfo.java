package com.fhao.rpc.core.registy.zookeeper;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 20:43</p>
 * <p>description:   </p>
 */
public class ProviderNodeInfo {
    private String serviceName;

    private String address;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ProviderNodeInfo{" +
                "serviceName='" + serviceName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
