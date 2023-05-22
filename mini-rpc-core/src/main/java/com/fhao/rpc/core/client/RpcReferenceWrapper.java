package com.fhao.rpc.core.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 20:24</p>
 * <p>description: rpc远程调用包装类  </p>
 */
public class RpcReferenceWrapper <T>{
    private Class<T> aimClass;
    private Map<String,Object> attachments = new ConcurrentHashMap<>();

    public Class<T> getAimClass() {
        return aimClass;
    }

    public int getRetry(){
        if(attachments.get("retry")==null){
            return 0;
        }else {
            return (int) attachments.get("retry");
        }
    }

    public void setRetry(int retry){
        this.attachments.put("retry",retry);
    }


    public void setAimClass(Class<T> aimClass) {
        this.aimClass = aimClass;
    }

    public boolean isAsync(){
        return Boolean.parseBoolean(String.valueOf(attachments.get("async")));
    }

    public void setAsync(boolean async){
        this.attachments.put("async",async);
    }

    public String getTimeOUt() {

        return String.valueOf(attachments.get("timeOut"));
    }
    public void setTimeOut(int timeOut) {
        attachments.put("timeOut", timeOut);
    }

    public String getUrl(){
        return String.valueOf(attachments.get("url"));
    }

    public void setUrl(String url){
        attachments.put("url",url);
    }

    public String getServiceToken(){
        return String.valueOf(attachments.get("serviceToken"));
    }

    public void setServiceToken(String serviceToken){
        attachments.put("serviceToken",serviceToken);
    }

    public String getGroup(){
        return String.valueOf(attachments.get("group"));
    }

    public void setGroup(String group){
        attachments.put("group",group);
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
    }
}
