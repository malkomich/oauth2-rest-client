package com.github.malkomich.oauth2.rest.client.document.fetch;

import com.github.malkomich.oauth2.rest.client.config.FeignClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class DocumentFetcherFeignClient implements DocumentFetcher {

    private final DocumentClient documentClient;

    @Override
    public DocumentFetchResponse fetchDocument(int id) {
        return documentClient.fetchDocument(id);
    }

    @FeignClient(name = "documentFetch",
                 url = "${api.document.fetch.host}",
                 configuration= FeignClientConfig.FeignClientDocumentFetchConfig.class)
    public interface DocumentClient {

        @GetMapping("${api.document.fetch.endpoint}")
        DocumentFetchResponse fetchDocument(@PathVariable("id") int id);
    }
}
