package com.github.malkomich.oauth2.rest.client.document.publish;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class DocumentPublisherRestTemplate implements DocumentPublisher {

    private final RestTemplate restTemplate;

    @Value("${api.document.publish.host}")
    private String host;

    @Value("${api.document.publish.endpoint}")
    private String endpoint;

    public DocumentPublisherRestTemplate(@Qualifier("publishRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PublishResponse publish(PublishRequest request) {
        return restTemplate.postForObject(
                buildURI(),
                request,
                PublishResponse.class);
    }

    private URI buildURI() {
        return UriComponentsBuilder
                .fromHttpUrl(host)
                .path(endpoint)
                .build()
                .toUri();
    }
}
