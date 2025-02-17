package com.example.logquerysystem.service.biz.base;

import com.example.logquerysystem.model.LogQueryRequest;
import com.example.logquerysystem.model.LogQueryResponse;
import java.io.IOException;
import java.util.List;

public interface LogQueryService<T> {
    /**
     * 获取所有可用的索引列表
     * @param env 环境参数，用于选择ES客户端
     * @return 索引列表
     * @throws IOException 当ES操作发生IO异常时抛出
     */
    List<String> getAllIndices(String env) throws IOException;

    /**
     * 查询日志
     * @param request 查询请求参数
     * @return 查询结果
     */
    LogQueryResponse<T> queryLogs(LogQueryRequest request);

    /**
     * 获取最近的日志
     * @param size 获取数量
     * @return 最近的日志列表
     */
    LogQueryResponse<T> getRecentLogs(int size);


}