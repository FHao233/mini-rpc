package com.fhao.rpc.core.common.event;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 13:46</p>
 * <p>description:   </p>
 */
//我们需要定义一个抽象的事件，该事件会用于装载需要传递的数据信息：
public interface  IRpcEvent {

    Object getData();

    IRpcEvent setData(Object data);
}
