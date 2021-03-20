package com.github.malkomich.oauth2.rest.client.document.publish;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class DocumentPublisherRestTemplate implements DocumentPublisher {

    private final RestTemplate restTemplate;

    @Override
    public PublishResponse publish(PublishRequest request) {
        return null;
    }
}
