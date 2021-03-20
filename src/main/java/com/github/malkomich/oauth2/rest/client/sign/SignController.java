package com.github.malkomich.oauth2.rest.client.sign;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    @GetMapping("/sign/{id}")
    public ResponseEntity<SignService.SignResponse> signDocument(@PathVariable int id,
                                                                 @RequestParam(required = false) String client) {
        return signService.sign(id, client)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
