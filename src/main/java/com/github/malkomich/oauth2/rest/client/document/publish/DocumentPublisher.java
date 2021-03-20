package com.github.malkomich.oauth2.rest.client.document.publish;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public interface DocumentPublisher {

    PublishResponse publish(PublishRequest request);

    @Getter
    class PublishResponse {
        private int transactionId;
    }

    @Builder
    class PublishRequest {
        private int documentId;
        private LocalDateTime signDate;
    }
}
