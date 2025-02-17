package com.example.logquerysystem.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

/**
 * Elasticsearch配置类
 * 负责创建和管理ES集群客户端连接
 */
@Configuration
@EnableConfigurationProperties
public class ElasticsearchConfig {

    /**
     * 配置类，用于加载和绑定 Elasticsearch 相关的配置属性。
     *
     * @ConfigurationProperties(prefix = "elasticsearch")：注解用于绑定配置文件中的属性到当前类的字段上，
     * 这里指定了前缀为 "elasticsearch" 的配置属性将被绑定到 ElasticsearchProperties 类中。
     *
     * @Bean：注解表示这是一个 Bean 的声明方法，Spring 容器会调用该方法来生成一个 Bean 实例，
     * 并将其注册到 Spring 应用上下文中。
     *
     * @return 返回 ElasticsearchProperties 类的实例，该实例包含了从配置文件中加载的 Elasticsearch 相关配置属性。
     */
    @ConfigurationProperties(prefix = "elasticsearch")
    @Bean
    public ElasticsearchProperties elasticsearchProperties() {
        return new ElasticsearchProperties();
    }

    /**
     * 创建一个名为 "bxElasticsearchClient" 的 Bean，该 Bean 是一个 Elasticsearch 客户端。
     *
     * @param elasticsearchProperties Elasticsearch 配置属性
     * @return 返回配置好的 Elasticsearch 客户端，该客户端根据 "bx" 环境配置创建
     */
    @Bean(name = "BXElasticsearchClient")
    public RestHighLevelClient bxElasticsearchClient(ElasticsearchProperties elasticsearchProperties) {
        return createElasticsearchClient(elasticsearchProperties,"bx");
    }

    /**
     * 创建一个名为 "wgqElasticsearchClient" 的 Bean，该 Bean 是一个 Elasticsearch 客户端。
     *
     * @param elasticsearchProperties Elasticsearch 配置属性
     * @return 返回配置好的 Elasticsearch 客户端
     */
    @Bean(name = "WGQElasticsearchClient")
    public RestHighLevelClient wgqElasticsearchClient(ElasticsearchProperties elasticsearchProperties) {
        return createElasticsearchClient(elasticsearchProperties,"wgq");
    }

    /**
     * 根据环境配置创建 Elasticsearch 客户端
     *
     * @param ElasticsearchProperties Elasticsearch 配置的环境信息
     * @return 创建的 Elasticsearch 客户端
     */
    private RestHighLevelClient createElasticsearchClient(ElasticsearchProperties ElasticsearchProperties, String env) {
        ElasticsearchProperties.Environment environment = ElasticsearchProperties.getEnvironments().get(env);
        // 创建HttpHost数组
        HttpHost[] hosts = environment.getNodes().stream()
                .map(node -> new HttpHost(node.getHost(), ElasticsearchProperties.getPort(), ElasticsearchProperties.getScheme()))
                .toArray(HttpHost[]::new);
        // 判断是否需要认证
        if (ElasticsearchProperties.getUsername() != null && ElasticsearchProperties.getPassword() != null) {
            // 创建认证信息
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(ElasticsearchProperties.getUsername(), ElasticsearchProperties.getPassword()));

            // 构建客户端
            RestClientBuilder builder = RestClient.builder(hosts)
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider));

            return new RestHighLevelClient(builder);
        }else {
            return new RestHighLevelClient(RestClient.builder(hosts));
        }
    }

}

