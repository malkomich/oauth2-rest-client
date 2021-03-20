package com.github.malkomich.oauth2.rest.client.document.publish;

import com.github.malkomich.oauth2.rest.client.document.fetch.DocumentFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class DocumentPublisherFactory {

    private static ApplicationContext context;

    @Autowired
    public DocumentPublisherFactory(ApplicationContext context) {
        this.context = context;
    }

    private final static Map<String, Class<? extends DocumentPublisher>> implementations = Map.of(
            "webclient", DocumentPublisherWebClient.class,
            "resttemplate", DocumentPublisherRestTemplate.class,
            "feign", DocumentPublisherFeignClient.class
    );

    public static DocumentPublisher instance(String client) {
        final String clientType = Optional.ofNullable(client)
                .orElse("webclient");
        final Class<? extends DocumentPublisher> clazz = implementations.get(clientType);

        return context.getBean(clazz);
    }
}
