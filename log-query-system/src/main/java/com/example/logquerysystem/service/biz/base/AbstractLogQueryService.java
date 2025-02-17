package com.example.logquerysystem.service.biz.base;

import com.alibaba.fastjson.JSON;
import com.example.logquerysystem.model.LogQueryRequest;
import com.example.logquerysystem.model.LogQueryResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 抽象日志查询服务
 * 提供基础的ES查询实现，包括通用的查询条件构建和结果处理逻辑
 */
public abstract class AbstractLogQueryService<T> implements LogQueryService<T> {
    Logger logger = Logger.getLogger(AbstractLogQueryService.class.getName());
    @Autowired
    @Qualifier("BXElasticsearchClient")
    protected RestHighLevelClient BXElasticsearchClient;
    @Autowired
    @Qualifier("WGQElasticsearchClient")
    protected RestHighLevelClient WGQElasticsearchClient;

    protected RestHighLevelClient getEsClient(String env) {
        return "wgq".equals(env) ? WGQElasticsearchClient : BXElasticsearchClient;
    }

    @Override
    public LogQueryResponse<T> queryLogs(LogQueryRequest request) {
        // 校验serviceName必填
        if (request.getServiceName() == null || request.getServiceName().trim().isEmpty()) {
            throw new IllegalArgumentException("serviceName是必填参数");
        }

        try {
            String indexName = request.getServiceName();
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // 构建查询条件
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 处理基础字段查询条件
            if (request.getServiceName() != null) {
                boolQuery.must(QueryBuilders.termQuery("appName", request.getServiceName()));
            }
            // 只有当LogLevel不为空且不是空字符串时才添加查询条件
            if (request.getLogLevel() != null && !request.getLogLevel().trim().isEmpty()) {
                boolQuery.must(QueryBuilders.termQuery("logLevel", request.getLogLevel()));
            }
            if (request.getStartTime() != null) {
                boolQuery.must(QueryBuilders.rangeQuery("@timestamp")
                        .gte(request.getStartTime()));
            }
            if (request.getEndTime() != null) {
                boolQuery.must(QueryBuilders.rangeQuery("@timestamp")
                        .lte(request.getEndTime()));
            }

            // 处理消息内容的AND条件，只有当列表不为空时才添加查询条件。
            // 使用operator.AND，这样可以确保所有关键字都必须出现。fuzzy_transpositions=false关闭模糊匹配、auto_generate_synonyms_phrase_query=false关闭同义词、
            if (!CollectionUtils.isEmpty(request.getMessageAndList())) {
                for (String keyword : request.getMessageAndList()) {
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        boolQuery.must(QueryBuilders.matchQuery("logMessage", keyword).operator(Operator.AND).fuzzyTranspositions(false).autoGenerateSynonymsPhraseQuery(false));
                    }
                }
            }
            // 只有当Tid不为空且不是空字符串时才添加查询条件
            if (request.getTid() != null && !request.getTid().trim().isEmpty()) {
                boolQuery.must(QueryBuilders.matchQuery("logMessage", request.getTid()).operator(Operator.AND));
            }

            // 处理消息内容的OR条件，只有当列表不为空时才添加查询条件
            if (!CollectionUtils.isEmpty(request.getMessageOrList())) {
                BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
                for (String keyword : request.getMessageOrList()) {
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        orQuery.should(QueryBuilders.matchQuery("logMessage", keyword).operator(Operator.AND).fuzzyTranspositions(false).autoGenerateSynonymsPhraseQuery(false));
                    }
                }
                if (orQuery.should().size() > 0) {
                    boolQuery.must(orQuery);
                }
            }
            String boolQueryStr = boolQuery.toString().replace("\n", "").replace("\r", "").replace(" ", "");
            logger.info("构建查询DSL："+boolQueryStr);
            sourceBuilder.query(boolQuery);

            // 设置分页
            sourceBuilder.from((request.getPageNum() - 1) * request.getPageSize());
            sourceBuilder.size(request.getPageSize());

            // 设置排序
            sourceBuilder.sort(request.getSortField(),
                    "desc".equalsIgnoreCase(request.getSortOrder()) ? SortOrder.DESC : SortOrder.ASC);
            // 设置聚合
            searchRequest.source(sourceBuilder);

            // 根据env参数选择ES客户端
            RestHighLevelClient esClient = getEsClient(request.getEnv());
            // 执行查询
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            logger.info("查询ES成功，查询到" + response.getHits().getTotalHits() + "条数据");
            logger.info("查询ES成功，查询结果:" + JSON.toJSONString(response));
            // 处理查询结果
            List<T> logs = new ArrayList<>();
            for (SearchHit hit : response.getHits().getHits()) {
                logs.add(convertToLogEntry(hit));
            }

            // 构建响应对象
            LogQueryResponse<T> queryResponse = new LogQueryResponse<>();
            queryResponse.setLogs(logs);
            queryResponse.setTotal(response.getHits().getTotalHits().value);
            queryResponse.setPageSize(request.getPageSize());
            queryResponse.setPageNum(request.getPageNum());

            return queryResponse;

        } catch (IOException e) {
            throw new RuntimeException("查询ES发生错误", e);
        }
    }

    @Override
    public LogQueryResponse<T> getRecentLogs(int size) {
        LogQueryRequest request = new LogQueryRequest();
        request.setPageSize(size);
        request.setPageNum(1);
        request.setSortField("@timestamp");
        request.setSortOrder("desc");
        return queryLogs(request);
    }

    /**
     * 将ES查询结果转换为日志对象
     *
     * @param hit ES查询命中的结果
     * @return 转换后的日志对象
     */
    protected abstract T convertToLogEntry(SearchHit hit);

    /**
     * 获取所有可用的索引列表
     *
     * @return 索引列表
     * @throws IOException 当ES操作发生IO异常时抛出
     */
    @Override
    public List<String> getAllIndices(String env) throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices("*");
        GetIndexResponse response = getEsClient(env).indices().get(request, RequestOptions.DEFAULT);
        List<String> filteredIndices = Arrays.stream(response.getIndices())
                .filter(index -> !index.startsWith(".")).collect(Collectors.toList());
        logger.info("获取ES索引列表成功，过滤后共" + filteredIndices.size() + "个索引");
        return filteredIndices;
    }
}