package com.redbyte.platform.zookeeper;

import com.redbyte.platform.ServiceDiscovery;
import com.redbyte.platform.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwq
 */
@Slf4j
public class ZookeeperDiscovery implements ServiceDiscovery {

    private String zkAddress;

    public ZookeeperDiscovery(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    @Override
    public String discovery(String serviceName) {
        // 创建 ZooKeeper 客户端
        ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECT_TIMEOUT);
        log.debug("connect zookeeper");
        try {
            // 获取 service 节点
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
            }
            List<String> addressList = zkClient.getChildren(servicePath);
            if (addressList == null || addressList.size() <= 0) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }
            // 获取 address 节点
            String address;
            int size = addressList.size();
            if (size == 1) {
                // 若只有一个地址，则获取该地址
                address = addressList.get(0);
                log.debug("get only address node: {}", address);
            } else {
                // 若存在多个地址，则随机获取一个地址
                address = addressList.get(ThreadLocalRandom.current().nextInt(size));
                log.debug("get random address node: {}", address);
            }
            // 获取 address 节点的值
            String addressPath = servicePath + "/" + address;
            return zkClient.readData(addressPath);
        } finally {
            zkClient.close();
        }
    }
}
