package com.redbyte.platform.codec.serializer;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwq
 */
public interface Serializer {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data, Class<T> clazz);
}
