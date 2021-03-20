package com.github.malkomich.oauth2.rest.client.document.publish;

import com.github.malkomich.oauth2.rest.client.config.FeignClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@RequiredArgsConstructor
public class DocumentPublisherFeignClient implements DocumentPublisher {

    private final DocumentClient documentClient;

    @Override
    public PublishResponse publish(PublishRequest request) {
        return documentClient.publishDocument(request);
    }

    @FeignClient(name = "documentClient",
                 url = "${api.document.publish.host}",
                 configuration= FeignClientConfig.class)
    public interface DocumentClient {

        @GetMapping("${api.document.publish.endpoint}")
        PublishResponse publishDocument(PublishRequest request);
    }
}
