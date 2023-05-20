package com.fhao.rpc.core.router;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 13:34</p>
 * <p>description:   </p>
 */
public class Selector {
    /**
     * 服务命名
     * eg: com.sise.test.DataService
     */
    private String providerServiceName;
    public String getProviderServiceName() {
        return providerServiceName;
    }

    public void setProviderServiceName(String providerServiceName) {
        this.providerServiceName = providerServiceName;
    }
}
