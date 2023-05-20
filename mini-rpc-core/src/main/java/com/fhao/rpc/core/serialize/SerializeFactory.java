package com.fhao.rpc.core.serialize;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 19:17</p>
 * <p>description:   </p>
 */
public interface SerializeFactory {

    /**
     * 序列化
     *
     * @param t
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T t);

    /**
     * 反序列化
     *
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> clazz);
}
