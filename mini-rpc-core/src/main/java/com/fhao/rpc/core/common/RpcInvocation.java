package com.fhao.rpc.core.common;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: FHao
 * create time: 2023-04-26 15:33
 * description:
 */
public class RpcInvocation implements Serializable {
    private static final long serialVersionUID = -3611379458492006176L;
    //请求的目标方法，例如findUser
    private String targetMethod;
    //请求的目标服务名称，例如：com.sise.user.UserService
    private String targetServiceName;
    //请求参数信息
    private Object[] args;
    //主要是用于匹配请求和响应的一个关键值
    private String uuid;
    //接口响应的数据塞入这个字段中（如果是异步调用或者void类型，这里就为空）
    private Object response;
    //出现的错误信息
    private Exception e;

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }
    private String message;

    private String stackTrace;
    private int retry;

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    private Map<String, Object> attachments = new ConcurrentHashMap<>();

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    public String getTargetServiceName() {
        return targetServiceName;
    }

    public void setTargetServiceName(String targetServiceName) {
        this.targetServiceName = targetServiceName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

}
