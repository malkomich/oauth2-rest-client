package com.github.malkomich.oauth2.rest.client.sign;

import com.github.malkomich.oauth2.rest.client.document.fetch.DocumentFetcher;
import com.github.malkomich.oauth2.rest.client.document.fetch.DocumentFetcherFactory;
import com.github.malkomich.oauth2.rest.client.document.publish.DocumentPublisher;
import com.github.malkomich.oauth2.rest.client.document.publish.DocumentPublisherFactory;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mapstruct.factory.Mappers.getMapper;

@Service
@RequiredArgsConstructor
public class SignService {

    public Optional<SignResponse> sign(int id, String client) {
        return Optional.of(DocumentFetcherFactory.instance(client))
                .map(documentFetcher -> documentFetcher.fetchDocument(id))
                .map(documentFetchResponse -> getMapper(DocumentMapper.class).execute(documentFetchResponse))
                .map(this::toPublishRequest)
                .map(publishRequest -> DocumentPublisherFactory.instance(client).publish(publishRequest))
                .map(this::toSignResponse);
    }

    private SignResponse toSignResponse(DocumentPublisher.PublishResponse publishResponse) {
        return SignResponse.builder()
                .transactionId(publishResponse.getTransactionId())
                .build();
    }

    private DocumentPublisher.PublishRequest toPublishRequest(Document document) {
        return DocumentPublisher.PublishRequest.builder()
                .documentId(document.getId())
                .signDate(LocalDateTime.now())
                .build();
    }

    @Mapper
    interface DocumentMapper {
        SignService.Document execute(DocumentFetcher.DocumentFetchResponse fetchResponse);
    }

    @Data
    static class Document {
        private int id;
        private String name;
        private byte[] content;
    }

    @Getter
    @Builder
    static class SignResponse {
        private int transactionId;
        private String content;
    }
}
