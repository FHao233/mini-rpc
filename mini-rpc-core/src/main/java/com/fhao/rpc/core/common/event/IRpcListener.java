package com.fhao.rpc.core.common.event;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 13:47</p>
 * <p>description:   </p>
 */
public interface IRpcListener<T> {

    void callBack(Object t);

}