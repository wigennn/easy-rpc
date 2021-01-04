package com.redbyte.platform.codec;

import com.redbyte.platform.codec.serializer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwq
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    public RpcEncoder(Class<?> clazz) {
        this.genericClass = clazz;
    }


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        byte[] data = SerializerFactory.getSerializer().serialize(object);
        byteBuf.writeBytes(data);
    }
}
