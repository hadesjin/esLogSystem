package com.example.logquerysystem.controller.base;

import com.alibaba.fastjson.JSON;
import com.example.logquerysystem.model.LogEntry;
import com.example.logquerysystem.model.LogQueryRequest;
import com.example.logquerysystem.model.LogQueryResponse;
import com.example.logquerysystem.service.biz.base.LogQueryService;
import com.example.logquerysystem.service.biz.indexs.intf.CommonLogQueryService;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * 日志查询控制器
 * 提供统一的日志查询RESTful接口
 */
@RestController
@RequestMapping("/api/logs")
public class CommonLogController {
    Logger logger = Logger.getLogger(CommonLogController.class.getName());
    @Autowired
    private CommonLogQueryService commonLogQueryService;

    @GetMapping("/indices")
    public List<String> getAllIndices(@RequestParam(defaultValue = "bx") String env) throws IOException {
        return commonLogQueryService.getAllIndices(env);
    }

    /**
     * 根据查询条件检索日志
     *
     * @param request 查询请求参数
     * @return 查询结果
     */
    @PostMapping("/query")
    public LogQueryResponse<LogEntry> queryLogs(@RequestBody LogQueryRequest request) {
        logger.info("Received log query request"+ JSON.toJSONString(request));
        return commonLogQueryService.queryLogs(request);
    }

    /**
     * 获取最近的日志记录
     *
     * @param size 获取数量，默认为10条
     * @return 最近的日志列表
     */
    @GetMapping("/recent")
    public LogQueryResponse<LogEntry> getRecentLogs(@RequestParam(defaultValue = "10") int size) {
        return commonLogQueryService.getRecentLogs(size);
    }
}