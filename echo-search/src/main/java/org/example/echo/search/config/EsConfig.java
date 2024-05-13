package org.example.echo.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.Resource;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

//@Configuration
public class EsConfig {

//    @Resource
//    private EsConfigurationProperties esConfigurationProperties;
//
//    @Bean
//    public ElasticsearchClient elasticsearchClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                //设置用户名密码
//                new UsernamePasswordCredentials("elastic", "123456"));
//
//        // 创建 SSL 上下文
//        SSLContextBuilder sslBuilder = SSLContexts.custom().loadTrustMaterial(null, (chain, authType) -> true);
//        final SSLContext sslContext = sslBuilder.build();
//
//        RestClientBuilder builder = RestClient.builder(new HttpHost("127.0.0.1", 9200, "https"))
//                .setHttpClientConfigCallback(httpClientBuilder ->
//                        httpClientBuilder
//                                .setDefaultCredentialsProvider(credentialsProvider)
//                                .setSSLContext(sslContext)
//                                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE));
//
//        RestClient client = builder.build();
//        ElasticsearchTransport transport = new RestClientTransport(client, new JacksonJsonpMapper());
//        return new ElasticsearchClient(transport);
//    }
}
