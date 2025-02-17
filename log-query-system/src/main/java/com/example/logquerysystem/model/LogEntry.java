package com.example.logquerysystem.model;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

/**
 * 日志条目类
 * 用于存储单条日志的详细信息
 */
@Data
public class LogEntry {
    /**
     * 日志ID
     * 唯一标识一条日志记录
     */
    private String id;

    /**
     * 应用名称
     * 产生日志的应用名称
     */
    private String appName;

    /**
     * 链路追踪ID
     * 用于分布式追踪的唯一标识符
     */
    private String tid;

    /**
     * 时间戳
     * 日志产生的时间
     */
    private ZonedDateTime timestamp;

    /**
     * 日志内容
     * 完整的日志消息文本
     */
    private String logMessage;

    /**
     * 日志级别
     * 如：INFO、WARN、ERROR等
     */
    private String logLevel;

    /**
     * 日志时间
     * 格式化的日志产生时间，便于阅读和排序
     */
    private String logTime;

    /**
     * 日志路径
     * 产生日志的完整文件或目录路径
     */
    private String path;

    /**
     * 集群名称
     * 应用所属的集群名称
     */
    private String cluster;

    /**
     * 采集时间
     * 日志被收集的时间，可能与产生时间不同步
     */
    private String collectorTime;

    /**
     * 主机名
     * 产生日志的服务器主机名
     */
    private String hostName;

    /**
     * 数据中心名称
     * 服务器所在的数据中心或机房标识
     */
    private String idc;

    /**
     * IP地址
     * 产生日志的服务器IP地址
     */
    private String ip;

    /**
     * 扩展字段
     * 存储日志的额外属性信息，key为字段名，value为字段值
     */
    private Map<String, Object> fields;
}