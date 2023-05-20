package com.fhao.rpc.core.serialize.fastjson;

import com.alibaba.fastjson.JSON;
import com.fhao.rpc.core.serialize.SerializeFactory;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-20 19:19</p>
 * <p>description:   </p>
 */
public class FastJsonSerializeFactory implements SerializeFactory {
    @Override
    public <T> byte[] serialize(T t) {
        String jsonStr = JSON.toJSONString(t);
        return jsonStr.getBytes();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSON.parseObject(data,clazz);
    }
}
