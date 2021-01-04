package com.redbyte.platform.server;

import com.redbyte.platform.domain.RpcRequest;
import com.redbyte.platform.domain.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwq
 */
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final Map<String, Object> handlerMap;

    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());

        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Exception e) {
            log.error("handle request error!", e);
            response.setError(e);
        }

        //写入rpc对象并自动关闭连接
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(RpcRequest request) throws Exception {
        String serviceName = request.getClassName();
        String version = request.getServiceVersion();
        if (version != null) {
            serviceName += "-" + version;
        }
        Object serviceBean = handlerMap.get(serviceName);
        if (serviceBean == null) {
            throw new RuntimeException("can not find service bean by key" + serviceName);
        }
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] paramterTypes = request.getParamterTypes();
        Object[] paramters = request.getParamters();

        // jdk 反射
/*        Method method = serviceClass.getMethod(methodName, paramterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, paramters);*/

        // cglib 反射调用
        FastClass fastClass = FastClass.create(serviceClass);
        FastMethod fastMethod = fastClass.getMethod(methodName, paramterTypes);
        return fastMethod.invoke(serviceBean, paramters);
    }
}
