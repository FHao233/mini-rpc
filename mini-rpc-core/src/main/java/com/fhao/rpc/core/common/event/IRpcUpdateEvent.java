package com.fhao.rpc.core.common.event;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 13:46</p>
 * <p>description:   </p>
 */
//这个接口的作用是什么？？？
    //这个类的作用是定义一个事件，这个事件的数据是一个Object类型的对象
public class IRpcUpdateEvent implements IRpcEvent{
    private Object data;

    public IRpcUpdateEvent(Object data) {
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
