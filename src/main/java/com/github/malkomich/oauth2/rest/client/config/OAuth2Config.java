package com.github.malkomich.oauth2.rest.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2Config {

    @Bean(name = "fetchOAuth2Properties")
    @ConfigurationProperties(prefix = "security.oauth2.client.document-fetch")
    Oauth2Properties fetcherOauthProperties() {
        return new Oauth2Properties();
    }

    @Bean(name = "publishOAuth2Properties")
    @ConfigurationProperties(prefix = "security.oauth2.client.document-publish")
    Oauth2Properties publisherOauthProperties() {
        return new Oauth2Properties();
    }

    @Data
    class Oauth2Properties {
        private String grantType;
        private String clientId;
        private String clientSecret;
        private String accessTokenUri;
        private String scope;
    }
}
