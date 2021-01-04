package com.redbyte.platform.client;

import com.redbyte.platform.ServiceDiscovery;
import com.redbyte.platform.domain.RpcRequest;
import com.redbyte.platform.domain.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwq
 */
@Slf4j
public class RpcProxy {

    private String serviceAddress;
    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create(final Class<?> interfaceClass, final String serviceVersion) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParamterTypes(method.getParameterTypes());
                        request.setParamters(args);
                        request.setServiceVersion(serviceVersion);

                        if (serviceDiscovery != null) {
                            String serviceName = interfaceClass.getName();
                            if (serviceVersion != null) {
                                serviceName += "-" + serviceVersion;
                            }

                            serviceAddress = serviceDiscovery.discovery(serviceName);
                            log.info("discovery service:{} => {}", serviceName, serviceAddress);
                        }

                        if (serviceAddress == null) {
                            throw new RuntimeException("service address is empty");
                        }

                        String[] array = serviceAddress.split(":");
                        String host = array[0];
                        Integer port = Integer.parseInt(array[1]);
                        RpcClient client = new RpcClient(host, port);
                        long sendTime = System.currentTimeMillis();
                        RpcResponse response = client.send(request);
                        log.info("client send cost time: {}ms", System.currentTimeMillis() - sendTime);
                        if (response == null) {
                            throw new RuntimeException("response is null");
                        }
                        if (response.getError() != null) {
                            throw response.getError();
                        } else {
                            return response.getResult();
                        }

                    }
                });
    }
}
