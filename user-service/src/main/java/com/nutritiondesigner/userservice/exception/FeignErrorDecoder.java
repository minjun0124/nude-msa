package com.nutritiondesigner.userservice.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final Environment env;

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400:
                break;
            case 404:
                if (methodKey.contains("createCart")) {
                    return new ResponseStatusException(HttpStatus.valueOf(response.status())
                            , env.getProperty("cart_service.exception.cart_create_fail"));
                }
                break;
            default:
                break;
        }
        return null;
    }
}
