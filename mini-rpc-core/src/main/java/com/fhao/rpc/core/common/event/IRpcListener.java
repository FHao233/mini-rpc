package com.fhao.rpc.core.common.event;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 13:47</p>
 * <p>description:   </p>
 */
//这个接口的作用是什么？？？
    //这个接口的作用是定义一个回调函数，这个回调函数的参数是一个Object类型的对象
public interface IRpcListener<T> {

    void callBack(Object t);

}