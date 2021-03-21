package com.github.malkomich.oauth2.rest.client.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
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
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    private static final String REGISTRATION_ID = "oauth2-rest-client";
    private static final String TRACE_ID = "traceId";

    @Bean(name = "fetchWebClient")
    WebClient fetchWebClient(WebClient.Builder webclientBuilder,
                             @Value("${api.document.fetch.host}") String host,
                             @Qualifier("fetchOAuth2Properties") OAuth2Config.Oauth2Properties properties) {
        return webclientBuilder
                .filter(oauth2Filter(properties))
                .baseUrl(host)
                .build();
    }

    @Bean(name = "publishWebClient")
    WebClient publishWebClient(WebClient.Builder webclientBuilder,
                               @Value("${api.document.publish.host}") String host,
                               @Qualifier("publishOAuth2Properties") OAuth2Config.Oauth2Properties properties) {
        return webclientBuilder
                .filter(oauth2Filter(properties))
                .baseUrl(host)
                .build();
    }

    @Bean
    WebClient.Builder webclientBuilder() {
        return WebClient.builder()
                .clientConnector(defaultConnector())
                .defaultHeaders(httpHeaders -> httpHeaders.addAll(createHeaders()));
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("traceId", TRACE_ID);
        return headers;
    }

    private ClientHttpConnector defaultConnector() {
        final HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(client ->
                        client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                                .doOnConnected(conn -> conn
                                        .addHandlerLast(new ReadTimeoutHandler(10))
                                        .addHandlerLast(new WriteTimeoutHandler(10))));
        final ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient.wiretap(true));

        return connector;
    }

    private ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Filter(
            OAuth2Config.Oauth2Properties oauth2Properties
    ) {
        final ReactiveClientRegistrationRepository clientRegistryRepo =
                new InMemoryReactiveClientRegistrationRepository(ClientRegistration
                        .withRegistrationId(REGISTRATION_ID)
                        .tokenUri(oauth2Properties.getAccessTokenUri())
                        .clientId(oauth2Properties.getClientId())
                        .clientSecret(oauth2Properties.getClientSecret())
                        .authorizationGrantType(new AuthorizationGrantType(oauth2Properties.getGrantType()))
                        .scope(oauth2Properties.getScope())
                        .build());
        final ReactiveOAuth2AuthorizedClientService clientService =
                new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistryRepo);
        final ReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistryRepo, clientService);
        final ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        oauthFilter.setDefaultClientRegistrationId(REGISTRATION_ID);
        return oauthFilter;
    }
}
