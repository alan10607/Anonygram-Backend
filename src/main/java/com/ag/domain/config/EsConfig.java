package com.ag.domain.config;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.json.jackson.JacksonJsonpMapper;
//import co.elastic.clients.transport.ElasticsearchTransport;
//import co.elastic.clients.transport.rest_client.RestClientTransport;
//import org.apache.http.Header;
//import org.apache.http.HttpHost;
//import org.apache.http.conn.ssl.TrustAllStrategy;
//import org.apache.http.message.BasicHeader;
//import org.apache.http.ssl.SSLContextBuilder;
//import org.elasticsearch.client.RestClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.net.ssl.SSLContext;
//import java.security.KeyManagementException;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//
//@Configuration
//public class EsConfig {
//    private final String host = "localhost";
//    private final int port = 9200;
//    private final String password = "OGmrJovJi37nakTDF*zc";
//
////    @Bean
////    public ElasticsearchClient elasticsearchClient() {
////        // Create the low-level client
////        HttpHost serverUrl = new HttpHost(host, port, "https");
////        RestClient restClient = RestClient
////                .builder(serverUrl)
////                .setDefaultHeaders(new Header[]{
////                        new BasicHeader("Authorization", "ApiKey " + password)
////                })
////                .build();
////
////        // Create the transport with a Jackson mapper
////        ElasticsearchTransport transport = new RestClientTransport(
////                restClient, new JacksonJsonpMapper());
////
////        // And create the API client
////        ElasticsearchClient esClient = new ElasticsearchClient(transport);
////        return esClient;
////    }
//
//    private SSLContext buildSSLContext() {
//        try {
//            return new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build();
//        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//}
//
///**
// * https://www.elastic.co/guide/en/kibana/current/docker.html
// * <p>
// * ✅ Elasticsearch security features have been automatically configured!
// * ✅ Authentication is enabled and cluster connections are encrypted.
// * <p>
// * ℹ️  Password for the elastic user (reset with `bin/elasticsearch-reset-password -u elastic`):
// * OGmrJovJi37nakTDF*zc
// * <p>
// * ℹ️  HTTP CA certificate SHA-256 fingerprint:
// * 744569bb53c0776679140278cfb81d5aec37f2b6505a6270eead2c31a93adf14
// * <p>
// * ℹ️  Configure Kibana to use this cluster:
// * • Run Kibana and click the configuration link in the terminal when Kibana starts.
// * • Copy the following enrollment token and paste it into Kibana in your browser (valid for the next 30 minutes):
// * eyJ2ZXIiOiI4LjEyLjIiLCJhZHIiOlsiMTcyLjIxLjAuMjo5MjAwIl0sImZnciI6Ijc0NDU2OWJiNTNjMDc3NjY3OTE0MDI3OGNmYjgxZDVhZWMzN2YyYjY1MDVhNjI3MGVlYWQyYzMxYTkzYWRmMTQiLCJrZXkiOiJEWUtJN28wQnFVb3FKX2lxdWdrRTp3cnpJM0NjalJMNmg5SzVuLXkwLTRnIn0=
// * <p>
// * ℹ️ Configure other nodes to join this cluster:
// * • Copy the following enrollment token and start new Elasticsearch nodes with `bin/elasticsearch --enrollment-token <token>` (valid for the next 30 minutes):
// * eyJ2ZXIiOiI4LjEyLjIiLCJhZHIiOlsiMTcyLjIxLjAuMjo5MjAwIl0sImZnciI6Ijc0NDU2OWJiNTNjMDc3NjY3OTE0MDI3OGNmYjgxZDVhZWMzN2YyYjY1MDVhNjI3MGVlYWQyYzMxYTkzYWRmMTQiLCJrZXkiOiJESUtJN28wQnFVb3FKX2lxdWdrRTpoMW9uc1J2SlFhS3NHM29EM0FjNjVBIn0=
// * <p>
// * If you're running in Docker, copy the enrollment token and run:
// * `docker run -e "ENROLLMENT_TOKEN=<token>" docker.elastic.co/elasticsearch/elasticsearch:8.12.2`
// **/


import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
//@EnableElasticsearchRepositories()
public class EsConfig {


    @Bean
    public RestHighLevelClient client() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .usingSsl()
                .withBasicAuth("elastic", "OGmrJovJi37nakTDF*zc") // put your
                .build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }
}