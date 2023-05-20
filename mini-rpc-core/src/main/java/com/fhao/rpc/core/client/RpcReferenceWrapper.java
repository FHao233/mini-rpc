package com.fhao.rpc.core.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 20:24</p>
 * <p>description:   </p>
 */
public class RpcReferenceWrapper <T>{
    private Class<T> aimClass;
    private Map<String,Object> attatchments = new ConcurrentHashMap<>();

    public Class<T> getAimClass() {
        return aimClass;
    }

    public void setAimClass(Class<T> aimClass) {
        this.aimClass = aimClass;
    }

    public boolean isAsync(){
        return Boolean.parseBoolean(String.valueOf(attatchments.get("async")));
    }

    public void setAsync(boolean async){
        this.attatchments.put("async",async);
    }

    public String getUrl(){
        return String.valueOf(attatchments.get("url"));
    }

    public void setUrl(String url){
        attatchments.put("url",url);
    }

    public String getServiceToken(){
        return String.valueOf(attatchments.get("serviceToken"));
    }

    public void setServiceToken(String serviceToken){
        attatchments.put("serviceToken",serviceToken);
    }

    public String getGroup(){
        return String.valueOf(attatchments.get("group"));
    }

    public void setGroup(String group){
        attatchments.put("group",group);
    }

    public Map<String, Object> getAttatchments() {
        return attatchments;
    }

    public void setAttatchments(Map<String, Object> attatchments) {
        this.attatchments = attatchments;
    }
}