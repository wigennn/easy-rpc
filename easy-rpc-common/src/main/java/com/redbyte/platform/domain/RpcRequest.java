package com.redbyte.platform.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author wangwq
 */
@Data
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 2556367043682816143L;

    /**
     * 请求id
     */
    private String requestId;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] paramterTypes;
    /**
     * 参数
     */
    private Object[] paramters;

    private String serviceVersion;
}
