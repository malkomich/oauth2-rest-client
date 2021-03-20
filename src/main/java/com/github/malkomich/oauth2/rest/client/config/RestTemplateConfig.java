package com.github.malkomich.oauth2.rest.client.config;

import com.github.malkomich.oauth2.rest.client.document.fetch.DocumentFetcherErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;

@Configuration
public class RestTemplateConfig {

    private static final int CONNECT_TIMEOUT = 2000;
    private static final int READ_TIMEOUT = 10000;
    private static final String TRACE_ID = "traceId";
    private static final String REGISTRATION_ID = "oauth2-rest-client";

    @Autowired
    private Oauth2Properties oauth2Properties;

    @Bean
    @Qualifier("documentFetcher")
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,
                              DocumentFetcherErrorHandler errorHandler) {
        return restTemplateBuilder
                .errorHandler(errorHandler)
                .build();
    }

    @Bean
    RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
                .requestFactory(this::buildHttpRequestFactory)
                .interceptors(
                        buildClientHttpRequestInterceptor(),
                        new BearerTokenInterceptor(buildAuthorizedClientManager()));
    }

    private org.springframework.http.client.ClientHttpRequestFactory buildHttpRequestFactory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        return factory;
    }

    private AuthorizedClientServiceOAuth2AuthorizedClientManager buildAuthorizedClientManager() {
        final ClientRegistration clientRegistration =
                ClientRegistration.withRegistrationId(REGISTRATION_ID)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                        .authorizationGrantType(new AuthorizationGrantType(oauth2Properties.getGrantType()))
                        .tokenUri(oauth2Properties.getAccessTokenUri())
                        .clientId(oauth2Properties.getClientId())
                        .clientSecret(oauth2Properties.getClientSecret())
                        .scope(oauth2Properties.getScope())
                        .build();

        final ClientRegistrationRepository repository = new InMemoryClientRegistrationRepository(clientRegistration);
        final OAuth2AuthorizedClientService service = new InMemoryOAuth2AuthorizedClientService(repository);
        final OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .refreshToken()
                .build();
        final AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(repository, service);

        manager.setAuthorizedClientProvider(provider);

        return manager;
    }

    private ClientHttpRequestInterceptor buildClientHttpRequestInterceptor() {
        return (httpRequest, bytes, clientHttpRequestExecution) -> {
            httpRequest.getHeaders().add("traceId", TRACE_ID);
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        };
    }

    @RequiredArgsConstructor
    class BearerTokenInterceptor implements ClientHttpRequestInterceptor {

        private final OAuth2AuthorizedClientManager manager;

        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest,
                                            byte[] bytes,
                                            ClientHttpRequestExecution clientHttpRequestExecution) {
            final OAuth2AuthorizedClient client = manager.authorize(OAuth2AuthorizeRequest
                    .withClientRegistrationId(REGISTRATION_ID)
                    .attribute(OAuth2ParameterNames.GRANT_TYPE, oauth2Properties.getGrantType())
                    .attribute(OAuth2ParameterNames.ACCESS_TOKEN, oauth2Properties.getAccessTokenUri())
                    .attribute(OAuth2ParameterNames.CLIENT_ID, oauth2Properties.getClientId())
                    .attribute(OAuth2ParameterNames.CLIENT_SECRET, oauth2Properties.getClientSecret())
                    .attribute(OAuth2ParameterNames.SCOPE, oauth2Properties.getScope())
                    .principal(SecurityContextHolder.getContext().getAuthentication())
                    .build());

            return Optional.ofNullable(client.getAccessToken())
                    .map(AbstractOAuth2Token::getTokenValue)
                    .map("Bearer "::concat)
                    .map(token -> executeRequest(httpRequest, bytes, clientHttpRequestExecution, token))
                    .orElseThrow(() -> new IllegalStateException("Cannot access the API without an access token"));
        }

        private ClientHttpResponse executeRequest(HttpRequest httpRequest,
                                                  byte[] bytes,
                                                  ClientHttpRequestExecution clientHttpRequestExecution,
                                                  String token) {
            try {
                httpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, token);
                return clientHttpRequestExecution.execute(httpRequest, bytes);
            } catch (final IOException exception) {
                throw new IllegalStateException("Cannot access the API without an access token");
            }
        }
    }
}
