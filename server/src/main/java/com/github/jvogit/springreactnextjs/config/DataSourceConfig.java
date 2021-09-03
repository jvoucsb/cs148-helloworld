package com.github.jvogit.springreactnextjs.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() throws URISyntaxException {
        final URI dbUri = new URI(System.getenv("DATABASE_URL"));

        final String username = dbUri.getUserInfo().split(":")[0];
        final String password = dbUri.getUserInfo().split(":")[1];
        final String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +
                // By default ssl is used. Set sslmode to disable to have http connections.
                "?sslmode=" + (Optional.ofNullable(System.getenv("PGSSLMODE")).orElse("require"));

        return DataSourceBuilder.create()
                .username(username)
                .password(password)
                .url(dbUrl)
                .build();
    }
}
