package com.github.malkomich.oauth2.rest.client.document.publish;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class DocumentPublisherWebClient implements DocumentPublisher {

    private final WebClient webClient;

    @Override
    public PublishResponse publish(PublishRequest request) {
        return null;
    }
}
