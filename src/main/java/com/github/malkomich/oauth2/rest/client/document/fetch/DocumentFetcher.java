package com.github.malkomich.oauth2.rest.client.document.fetch;

import lombok.Builder;
import lombok.Data;

public interface DocumentFetcher {

    DocumentFetchResponse fetchDocument(int id);

    @Data
    class DocumentFetchResponse {
        private int id;
        private String name;
        private byte[] content;
    }

    @Builder
    class DocumentFetchRequest {
        private int documentId;
    }
}
