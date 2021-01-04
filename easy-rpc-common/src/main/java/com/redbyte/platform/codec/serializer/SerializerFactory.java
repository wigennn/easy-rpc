package com.redbyte.platform.codec.serializer;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwq
 */
public class SerializerFactory {

    public static Serializer getSerializer() {
        return new FastJsonSerializer();
    }
}
