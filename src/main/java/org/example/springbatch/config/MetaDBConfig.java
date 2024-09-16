package org.example.springbatch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class MetaDBConfig {

    @Primary // 스프링 배치의 기본적인 메타데이터는 @Primary로 잡혀 있는 DB 소스에 초기화되게 됩니다.
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-meta") // application.properties 에 정의된 내용입니다. (4개의 값)
    public DataSource metaDBSource() {

        return DataSourceBuilder.create().build();
    }

    @Primary // 충돌 방지
    @Bean
    public PlatformTransactionManager metaTransactionManager() {

        return new DataSourceTransactionManager(metaDBSource());
    }
}
