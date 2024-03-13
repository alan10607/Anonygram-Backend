package com.ag.domain.config;

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


import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.MappingElasticsearchEntityInformation;
import org.springframework.data.util.TypeInformation;

import javax.net.ssl.SSLContext;

@Configuration
@EnableElasticsearchRepositories()
public class EsConfig extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .usingSsl(buildSslContext())
                .withBasicAuth("elastic", "OGmrJovJi37nakTDF*zc") // put your
                .build();
    }

    private static SSLContext buildSslContext() {
        try {
            return new SSLContextBuilder()
                    .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}