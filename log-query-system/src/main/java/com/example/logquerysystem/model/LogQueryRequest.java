package com.example.logquerysystem.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * 日志查询请求对象
 * 封装了日志查询的各种查询条件和分页参数
 */
@Data
public class LogQueryRequest {

    /**
     * 环境标识
     * 用于指定查询的机房环境（bx或wgq）
     */
    private String env = "bx";

    /**
     * 链路追踪ID
     * 用于分布式追踪的唯一标识符
     */
    private String tid;
    /**
     * 服务名称
     * 产生日志的服务或应用名称
     */
    private String serviceName;

    /**
     * and 日志关键词列表
     */
    private List<String> MessageAndList;

    /**
     * or 日志关键词列表
     */
    private List<String> MessageOrList;

    /**
     * 开始时间
     * 日志查询的时间范围起点
     */
    private ZonedDateTime startTime;

    /**
     * 结束时间
     * 日志查询的时间范围终点
     */
    private ZonedDateTime endTime;

    /**
     * 日志级别
     * 如：INFO、WARN、ERROR等
     */
    private String logLevel;



    /**
     * 每页记录数
     * 默认值为20
     */
    private int pageSize = 20;

    /**
     * 当前页码
     * 默认值为1
     */
    private int pageNum = 1;

    /**
     * 排序字段
     * 默认按时间戳（@timestamp）排序
     */
    private String sortField = "@timestamp";

    /**
     * 排序方式
     * 默认降序（desc）
     */
    private String sortOrder = "desc";
}