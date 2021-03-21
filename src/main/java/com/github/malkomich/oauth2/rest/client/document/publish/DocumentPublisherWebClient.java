package com.github.malkomich.oauth2.rest.client.document.publish;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DocumentPublisherWebClient implements DocumentPublisher {

    private final WebClient webClient;

    @Value("${api.document.publish.endpoint}")
    private String endpoint;

    public DocumentPublisherWebClient(@Qualifier("publishWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public PublishResponse publish(PublishRequest request) {
        return webClient.post()
                .uri(endpoint)
                .body(Mono.just(request), PublishRequest.class)
                .retrieve()
                .bodyToMono(PublishResponse.class)
                .block();
    }
}
