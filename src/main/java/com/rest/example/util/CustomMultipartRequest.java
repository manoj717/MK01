package com.rest.example.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CustomMultipartRequest implements HttpOutputMessage {


    private final HttpHeaders headers = new HttpHeaders();

    private final ByteArrayOutputStream body = new ByteArrayOutputStream(1024);

    @Override
    public OutputStream getBody() throws IOException {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
