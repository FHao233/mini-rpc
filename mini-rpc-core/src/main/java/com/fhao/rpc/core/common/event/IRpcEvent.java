package com.fhao.rpc.core.common.event;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 13:46</p>
 * <p>description:   </p>
 */
public interface  IRpcEvent {

    Object getData();

    IRpcEvent setData(Object data);
}
