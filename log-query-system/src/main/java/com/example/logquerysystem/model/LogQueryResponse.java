package com.example.logquerysystem.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 日志查询响应对象
 * 封装了日志查询的结果数据，包括日志列表、分页信息和聚合统计信息
 */
@Data
public class LogQueryResponse<T> {
    /**
     * 日志条目列表
     * 当前页的日志记录集合
     */
    private List<T> logs;

    /**
     * 总记录数
     * 符合查询条件的日志总数
     */
    private long total;

    /**
     * 每页记录数
     * 当前查询结果的分页大小
     */
    private int pageSize;

    /**
     * 当前页码
     * 当前查询结果的页码
     */
    private int pageNum;

    /**
     * 聚合统计信息
     * 用于存储各维度的统计数据，如日志级别分布、服务调用次数等
     * 外层Map的key为聚合维度，内层Map为该维度下的具体统计值
     */
    private Map<String, Map<String, Long>> aggregations;
}