package com.github.malkomich.oauth2.rest.client.exception;

public class ConfigError extends RuntimeException {

    public ConfigError(String message, String... params) {
        super(String.format(message, params));
    }
}
