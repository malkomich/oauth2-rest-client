package com.github.malkomich.oauth2.rest.client.document.fetch;

import com.github.malkomich.oauth2.rest.client.RESTException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

@Component
public class DocumentFetcherErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        final int responseStatus = response.getRawStatusCode();

        throw new RESTException(
                String.format("Error connecting with document fetch API: %s", responseStatus));
    }
}
