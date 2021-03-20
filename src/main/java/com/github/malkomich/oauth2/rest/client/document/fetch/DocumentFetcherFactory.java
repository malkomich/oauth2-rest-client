package com.github.malkomich.oauth2.rest.client.document.fetch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class DocumentFetcherFactory {

    private static ApplicationContext context;

    @Autowired
    public DocumentFetcherFactory(ApplicationContext context) {
        DocumentFetcherFactory.context = context;
    }

    private final static Map<String, Class<? extends DocumentFetcher>> implementations = Map.of(
            "webclient", DocumentFetcherWebClient.class,
            "resttemplate", DocumentFetcherRestTemplate.class,
            "feign", DocumentFetcherFeignClient.class
    );

    public static DocumentFetcher instance(String client) {
        final String clientType = Optional.ofNullable(client)
                .orElse("webclient");
        final Class<? extends DocumentFetcher> clazz = implementations.get(clientType);

        return context.getBean(clazz);
    }
}
