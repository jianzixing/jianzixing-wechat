package com.jianzixing.webapp.handler;

import java.io.InputStream;

public class ResponseFileWrapper {
    private InputStream inputStream;
    private String name;

    public ResponseFileWrapper(InputStream inputStream, String name) {
        this.inputStream = inputStream;
        this.name = name;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getName() {
        return name;
    }
}
