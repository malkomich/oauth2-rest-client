package com.github.malkomich.oauth2.rest.client.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.util.Optional;

public class FeignClientConfig {

    private static final String REGISTRATION_ID = "oauth2-rest-client";

    public static class FeignClientDocumentFetchConfig {

        @Bean
        public OAuthRequestInterceptor repositoryClientOAuth2Interceptor(
                @Qualifier("fetchOAuth2Properties") OAuth2Config.Oauth2Properties properties
        ) {
            return OAuthRequestInterceptor.instance(properties);
        }
    }

    public static class FeignClientDocumentPublishConfig {

        @Bean
        public OAuthRequestInterceptor repositoryClientOAuth2Interceptor(
                @Qualifier("publishOAuth2Properties") OAuth2Config.Oauth2Properties properties
        ) {
            return OAuthRequestInterceptor.instance(properties);
        }
    }

    @RequiredArgsConstructor
    static class OAuthRequestInterceptor implements RequestInterceptor {

        private final OAuth2AuthorizedClientManager manager;
        private final OAuth2Config.Oauth2Properties oauth2Properties;

        static OAuthRequestInterceptor instance(OAuth2Config.Oauth2Properties properties) {
            return new OAuthRequestInterceptor(buildAuthorizedClientManager(properties), properties);
        }

        @Override
        public void apply(RequestTemplate requestTemplate) {
            final OAuth2AuthorizedClient client = manager.authorize(OAuth2AuthorizeRequest
                    .withClientRegistrationId(REGISTRATION_ID)
                    .attribute(OAuth2ParameterNames.GRANT_TYPE, oauth2Properties.getGrantType())
                    .attribute(OAuth2ParameterNames.ACCESS_TOKEN, oauth2Properties.getAccessTokenUri())
                    .attribute(OAuth2ParameterNames.CLIENT_ID, oauth2Properties.getClientId())
                    .attribute(OAuth2ParameterNames.CLIENT_SECRET, oauth2Properties.getClientSecret())
                    .attribute(OAuth2ParameterNames.SCOPE, oauth2Properties.getScope())
                    .principal(SecurityContextHolder.getContext().getAuthentication())
                    .build());

            final String authToken = Optional.ofNullable(client.getAccessToken())
                    .map(AbstractOAuth2Token::getTokenValue)
                    .map("Bearer "::concat)
                    .orElseThrow(() -> new IllegalStateException("Cannot access the API without an access token"));

            requestTemplate.header(HttpHeaders.AUTHORIZATION, authToken);
        }

        private static AuthorizedClientServiceOAuth2AuthorizedClientManager buildAuthorizedClientManager(
                OAuth2Config.Oauth2Properties oauth2Properties
        ) {
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
    }
}
