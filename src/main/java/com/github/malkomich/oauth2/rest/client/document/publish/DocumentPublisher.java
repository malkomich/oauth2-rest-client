package com.github.malkomich.oauth2.rest.client.document.publish;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

public interface DocumentPublisher {

    PublishResponse publish(PublishRequest request);

    @Getter
    class PublishResponse {
        private int transactionId;
    }

    @Builder
    class PublishRequest {
        private int documentId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
        private OffsetDateTime signDate;
    }
}
