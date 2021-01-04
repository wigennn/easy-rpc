package com.redbyte.platform.zookeeper;

import com.redbyte.platform.ServiceRegistry;
import com.redbyte.platform.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwq
 */
@Slf4j
public class ZookeeperRegistry implements ServiceRegistry {

    private final ZkClient zkClient;

    public ZookeeperRegistry(String zkAddress) {
        zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECT_TIMEOUT);
    }

    @Override
    public void registry(String serviceName, String registryAddress) {
        // 创建 registry 节点（持久）
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            log.debug("create registry node: {}", registryPath);
        }
        // 创建 service 节点（持久）
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            log.debug("create service node: {}", servicePath);
        }
        // 创建 address 节点（临时）
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, registryAddress);
        log.debug("create address node: {}", addressNode);
    }
}
