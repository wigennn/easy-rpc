package com.redbyte.platform.codec;

import com.redbyte.platform.codec.serializer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwq
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] data = new byte[1024];
        byteBuf.readBytes(data);
        list.add(SerializerFactory.getSerializer().deserialize(data, genericClass));
    }
}
