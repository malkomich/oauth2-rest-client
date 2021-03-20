package com.github.malkomich.oauth2.rest.client.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final String REGISTRATION_ID = "oauth2-rest-client";

    @Bean
    @Qualifier("document")
    WebClient webClient(WebClient.Builder webclientBuilder,
                        @Value("${api.document.fetch.host}") String host) {
        return webclientBuilder
                .baseUrl(host)
                .build();
    }

    @Bean
    WebClient.Builder webclientBuilder(Oauth2Properties properties) {
        final ReactiveClientRegistrationRepository clientRegistryRepo =
                new InMemoryReactiveClientRegistrationRepository(ClientRegistration
                        .withRegistrationId(REGISTRATION_ID)
                        .tokenUri(properties.getAccessTokenUri())
                        .clientId(properties.getClientId())
                        .clientSecret(properties.getClientSecret())
                        .authorizationGrantType(new AuthorizationGrantType(properties.getGrantType()))
                        .scope(properties.getScope())
                        .build());
        final ReactiveOAuth2AuthorizedClientService clientService =
                new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistryRepo);
        final ReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistryRepo, clientService);
        final ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        oauthFilter.setDefaultClientRegistrationId(REGISTRATION_ID);

        return WebClient.builder()
                .filter(oauthFilter);
    }
}
