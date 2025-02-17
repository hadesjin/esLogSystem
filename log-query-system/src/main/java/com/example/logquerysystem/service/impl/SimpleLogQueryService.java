package com.example.logquerysystem.service.impl;

import com.example.logquerysystem.model.LogEntry;
import com.example.logquerysystem.model.LogQueryResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SimpleLogQueryService {

    @Resource(name = "BXElasticsearchClient")
    private RestHighLevelClient elasticsearchClientBX;

    /**
     * 查询指定索引的所有日志数据
     *
     * @param indexName 索引名称
     * @param page      当前页码（从1开始）
     * @param size      每页大小
     * @return 日志查询响应对象
     */
    public LogQueryResponse<LogEntry> queryAllLogs(String indexName, int page, int size) throws IOException {
        // 创建搜索请求
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        
        // 使用matchAll查询所有文档
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        
        // 设置分页
        searchSourceBuilder.from((page - 1) * size);
        searchSourceBuilder.size(size);
        
        searchRequest.source(searchSourceBuilder);

        // 执行搜索
        SearchResponse searchResponse = elasticsearchClientBX.search(searchRequest, RequestOptions.DEFAULT);

        // 解析结果
        List<LogEntry> logs = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            LogEntry logEntry = new LogEntry();
            
            // 设置基本字段
            logEntry.setId(hit.getId());
            logEntry.setAppName((String) sourceMap.get("appName"));
            logEntry.setTid((String) sourceMap.get("tid"));
            logEntry.setLogMessage((String) sourceMap.get("logMessage"));
            logEntry.setLogLevel((String) sourceMap.get("logLevel"));
            logEntry.setLogTime((String) sourceMap.get("logTime"));
            logEntry.setPath((String) sourceMap.get("path"));
            logEntry.setCluster((String) sourceMap.get("cluster"));
            logEntry.setCollectorTime((String) sourceMap.get("collectorTime"));
            logEntry.setHostName((String) sourceMap.get("hostName"));
            logEntry.setIdc((String) sourceMap.get("idc"));
            logEntry.setIp((String) sourceMap.get("ip"));
            
            // 设置扩展字段
            Map<String, Object> fields = (Map<String, Object>) sourceMap.get("fields");
            logEntry.setFields(fields);
            
            logs.add(logEntry);
        }

        // 构建响应对象
        LogQueryResponse<LogEntry> response = new LogQueryResponse<>();
        response.setLogs(logs);
        response.setTotal(searchResponse.getHits().getTotalHits().value);
        response.setPageSize(size);
        response.setPageNum(page);

        return response;
    }
}