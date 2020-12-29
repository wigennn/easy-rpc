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
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 7306247058304261063L;

    /**
     * 请求id
     */
    private String requestId;
    /**
     * 响应结果
     */
    private Object result;
    /**
     * 响应异常
     */
    private Throwable error;
}
