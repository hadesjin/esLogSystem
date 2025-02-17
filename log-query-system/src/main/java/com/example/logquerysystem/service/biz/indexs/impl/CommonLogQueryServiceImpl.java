package com.example.logquerysystem.service.biz.indexs.impl;

import com.example.logquerysystem.model.LogEntry;
import com.example.logquerysystem.service.biz.base.AbstractLogQueryService;
import com.example.logquerysystem.service.biz.indexs.intf.CommonLogQueryService;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

/**
 * 通用日志查询服务实现类
 * 基于LogEntry结构实现统一的日志查询功能
 */
@Service
public class CommonLogQueryServiceImpl extends AbstractLogQueryService<LogEntry> implements CommonLogQueryService {

    @Override
    protected LogEntry convertToLogEntry(SearchHit hit) {
        Map<String, Object> source = hit.getSourceAsMap();
        LogEntry logEntry = new LogEntry();
        
        // 设置基础字段
        logEntry.setId(hit.getId());
        logEntry.setTimestamp(source.get("@timestamp") != null ?  ZonedDateTime.parse(source.get("@timestamp").toString(), DateTimeFormatter.ISO_DATE_TIME) : null);
//        logEntry.setTid(source.get("tid") != null ? source.get("tid").toString() : null);
        logEntry.setAppName(source.get("appName") != null ? source.get("appName").toString() : null);
        logEntry.setLogLevel(source.get("logLevel") != null ? source.get("logLevel").toString() : null);
        logEntry.setLogMessage(source.get("logMessage") != null ? source.get("logMessage").toString() : null);
        
        // 设置额外字段
        logEntry.setCluster(source.get("cluster") != null ? source.get("cluster").toString() : null);
        logEntry.setCollectorTime(source.get("collectorTime") != null ? source.get("collectorTime").toString() : null);
        logEntry.setHostName(source.get("hostName") != null ? source.get("hostName").toString() : null);
        logEntry.setIdc(source.get("idc") != null ? source.get("idc").toString() : null);
        logEntry.setIp(source.get("ip") != null ? source.get("ip").toString() : null);
        logEntry.setLogTime(source.get("logTime") != null ? source.get("logTime").toString() : null);
        logEntry.setPath(source.get("path") != null ? source.get("path").toString() : null);
        
        return logEntry;
    }
}