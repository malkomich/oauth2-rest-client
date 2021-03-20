package com.github.malkomich.oauth2.rest.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "security.oauth2.client")
public class Oauth2Properties {
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String accessTokenUri;
    private String scope;
}
