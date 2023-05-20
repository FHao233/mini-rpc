package com.fhao.rpc.core.common.event;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 20:45</p>
 * <p>description:   </p>
 */
public class IRpcDestroyEvent implements IRpcEvent{
    private Object data;
    public IRpcDestroyEvent(Object data) {
        this.data = data;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public IRpcEvent setData(Object data) {
        this.data = data;
        return this;
    }
}
