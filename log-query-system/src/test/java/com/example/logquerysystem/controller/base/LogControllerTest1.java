package com.example.logquerysystem.controller.base;

import com.alibaba.fastjson.JSON;
import com.example.logquerysystem.LogQuerySystemApplication;
import com.example.logquerysystem.model.LogEntry;
import com.example.logquerysystem.model.LogQueryRequest;
import com.example.logquerysystem.model.LogQueryResponse;
import com.example.logquerysystem.service.biz.indexs.intf.CommonLogQueryService;
import com.example.logquerysystem.service.impl.SimpleLogQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LogQuerySystemApplication.class)
public class LogControllerTest1 {
    @Autowired
    private CommonLogQueryService commonLogQueryService;
    @Autowired
    private SimpleLogQueryService simpleLogQueryService;


    @Test
    public void test() {
        LogQueryRequest request = new LogQueryRequest();
        request.setTid("9090909ldjglsjgj-dkdk");
        request.setServiceName("userservices");
        request.setLogLevel("INFO-testAndOr");

        ZonedDateTime startTime = ZonedDateTime.parse("2025-02-01T16:00:00Z");
        ZonedDateTime endTime = ZonedDateTime.parse("2025-02-06T16:00:00Z");
        //验证and之间是否生效
        request.setMessageAndList(Arrays.asList("高高的滕王阁","验证数据"));
//        request.setMessageAndList(Arrays.asList("马","验"));
        request.setMessageOrList(Arrays.asList("物换星移","高高的滕王阁","俯仰自在"));
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        System.out.println(JSON.toJSONString(request));
        LogQueryResponse<LogEntry> logEntryLogQueryResponse = commonLogQueryService.queryLogs(request);
        System.out.println(JSON.toJSONString(logEntryLogQueryResponse));
    }

    @Test
    public void test111() throws IOException {
        List<String> allIndices = commonLogQueryService.getAllIndices("bx");
        System.out.println(JSON.toJSONString(allIndices));
    }

    @Test
    public void test11() {
        LogQueryRequest request = JSON.parseObject("{\"logLevel\":\"\",\"messageAndList\":[],\"messageOrList\":[],\"pageNum\":1,\"pageSize\":20,\"serviceName\":\"userservices\",\"sortField\":\"@timestamp\",\"sortOrder\":\"desc\",\"tid\":\"\"}", LogQueryRequest.class);

        LogQueryResponse<LogEntry> logEntryLogQueryResponse = commonLogQueryService.queryLogs(request);
        System.out.println(JSON.toJSONString(logEntryLogQueryResponse));
    }

    @Test
    public void test1() throws IOException {
        LogQueryResponse<LogEntry> userservices = simpleLogQueryService.queryAllLogs("userservices", 1, 10);
        System.out.println(JSON.toJSONString(userservices));
    }


    @Test
    public void test2() throws IOException {
        LogQueryResponse<LogEntry> userservices = simpleLogQueryService.queryAllLogs("userservices", 1, 10);
        System.out.println(JSON.toJSONString(userservices));
    }
}
