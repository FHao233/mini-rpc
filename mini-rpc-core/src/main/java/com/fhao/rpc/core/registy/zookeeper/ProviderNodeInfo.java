package com.fhao.rpc.core.registy.zookeeper;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 20:43</p>
 * <p>description:   </p>
 */
public class ProviderNodeInfo {
    private String applicationName;
    private String serviceName;

    private String address;

    private String registryTime;

    private Integer weight;
    private String group;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getRegistryTime() {
        return registryTime;
    }

    public void setRegistryTime(String registryTime) {
        this.registryTime = registryTime;
    }



    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

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
                "applicationName='" + applicationName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", address='" + address + '\'' +
                ", registryTime='" + registryTime + '\'' +
                ", weight=" + weight +
                ", group='" + group + '\'' +
                '}';
    }
}
