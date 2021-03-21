package com.github.malkomich.oauth2.rest.client.document.fetch;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class DocumentFetcherWebClient implements DocumentFetcher {

    private final WebClient webClient;

    @Value("${api.document.fetch.endpoint}")
    private String endpoint;

    public DocumentFetcherWebClient(@Qualifier("fetchWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public DocumentFetchResponse fetchDocument(int id) {
        return webClient.get()
                .uri(endpoint, id)
                .retrieve()
                .bodyToMono(DocumentFetchResponse.class)
                .block();
    }
}
