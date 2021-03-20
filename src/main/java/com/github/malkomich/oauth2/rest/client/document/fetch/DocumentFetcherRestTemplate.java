package com.github.malkomich.oauth2.rest.client.document.fetch;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentFetcherRestTemplate implements DocumentFetcher {

    @Qualifier("documentFetcher")
    private final RestTemplate restTemplate;

    @Value("${api.document.fetch.host}")
    private String host;

    @Value("${api.document.fetch.endpoint}")
    private String endpoint;

    @Override
    public DocumentFetchResponse fetchDocument(int id) {
        return restTemplate.getForObject(
                fetchURI(id),
                DocumentFetchResponse.class);
    }

    private URI fetchURI(int id) {
        return UriComponentsBuilder
                .fromHttpUrl(host)
                .path(endpoint)
                .uriVariables(Map.of("id", id))
                .build()
                .toUri();
    }
}
