package com.redbyte.platform.server;

import com.redbyte.platform.ServiceRegistry;
import com.redbyte.platform.annotation.EasyRpcService;
import com.redbyte.platform.codec.RpcDecoder;
import com.redbyte.platform.codec.RpcEncoder;
import com.redbyte.platform.domain.RpcRequest;
import com.redbyte.platform.domain.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwq
 */
@Slf4j
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private Map<String, Object> handlerMap = new HashMap<>();
    private String serviceAddress;
    private ServiceRegistry serviceRegistry;

    public RpcServer(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(EasyRpcService.class);
        if (serviceBeanMap != null) {
            for (Object serviceBean : serviceBeanMap.values()) {
                EasyRpcService rpcService = serviceBean.getClass().getAnnotation(EasyRpcService.class);
                String serviceName = rpcService.value().getName();
                String serviceVersion = rpcService.version();
                if (serviceVersion != null) {
                    serviceName += "-" + serviceVersion;
                }
                handlerMap.put(serviceName, serviceBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new RpcEncoder(RpcResponse.class));
                            pipeline.addLast(new RpcDecoder(RpcRequest.class));
                            pipeline.addLast(new RpcServerHandler(handlerMap));
                        }
                    });

            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] array = serviceAddress.split(":");
            String host = array[0];
            Integer port = Integer.parseInt(array[1]);
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            if (serviceRegistry != null) {
                for (String interfaceName : handlerMap.keySet()) {
                    serviceRegistry.registry(interfaceName, serviceAddress);
                    log.info("register service:{} => {}", interfaceName, serviceAddress);
                }
            }
            log.info("server start on port:{}", port);
            future.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
