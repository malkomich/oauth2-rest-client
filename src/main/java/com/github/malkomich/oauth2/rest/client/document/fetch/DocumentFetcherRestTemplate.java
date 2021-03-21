package com.github.malkomich.oauth2.rest.client.document.fetch;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
public class DocumentFetcherRestTemplate implements DocumentFetcher {

    private final RestTemplate restTemplate;

    @Value("${api.document.fetch.host}")
    private String host;

    @Value("${api.document.fetch.endpoint}")
    private String endpoint;

    public DocumentFetcherRestTemplate(@Qualifier("fetchRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public DocumentFetchResponse fetchDocument(int id) {
        return restTemplate.getForObject(
                buildURI(id),
                DocumentFetchResponse.class);
    }

    private URI buildURI(int id) {
        return UriComponentsBuilder
                .fromHttpUrl(host)
                .path(endpoint)
                .uriVariables(Map.of("id", id))
                .build()
                .toUri();
    }
}
