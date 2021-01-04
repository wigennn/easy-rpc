package com.redbyte.platform.codec.serializer;

import com.alibaba.fastjson.JSON;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwq
 */
public class FastJsonSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) {
        String json = JSON.toJSONString(obj);
        return json.getBytes();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSON.parseObject(data, clazz);
    }
}
