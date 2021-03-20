package com.github.malkomich.oauth2.rest.client.document.fetch;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class DocumentFetcherWebClient implements DocumentFetcher {

    @Qualifier("documentFetcher")
    private final WebClient webClient;

    @Value("${api.document.fetch.endpoint}")
    private String endpoint;

    @Override
    public DocumentFetchResponse fetchDocument(int id) {
        return webClient.method(HttpMethod.GET)
                .uri(endpoint, id)
                .retrieve()
                .bodyToMono(DocumentFetchResponse.class)
                .block();
    }
}
