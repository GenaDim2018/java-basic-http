package ru.fisunov.http.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private InputStream inputStream;
    private String uri;
    private String raw;
    private Map<String, String> params;

    public String getUri() {
        return uri;
    }

    public Request(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[2048];
        int n = inputStream.read(buffer);
        this.raw = new String(buffer, 0, n);
        this.uri = parseUri(raw);
        this.params = parseGetRequestParams(raw);
    }

    private String parseUri(String request) {
        int startIndex = request.indexOf(' ');
        int endIndex = request.indexOf(' ', startIndex + 1);
        String uri = request.substring(startIndex + 1, endIndex);
        if (!uri.contains("?")) {
            return uri;
        }
        endIndex = uri.indexOf('?');
        return uri.substring(0, endIndex);
    }

    private Map<String, String> parseGetRequestParams(String request) {
        int startIndex = request.indexOf(' ');
        int endIndex = request.indexOf(' ', startIndex + 1);
        String uri = request.substring(startIndex + 1, endIndex);
        if (!uri.contains("?")) {
            return Collections.emptyMap();
        }
        String[] paramsKeyValue = uri.substring(uri.indexOf('?') + 1).split("&");
        Map<String, String> out = new HashMap<>();
        for (String p : paramsKeyValue) {
            String[] keyValue = p.split("=");
            out.put(keyValue[0], keyValue[1]);
        }
        return out;
    }

    public void show() {
        System.out.println("Запрос:");
        System.out.println("uri: " + uri);
        System.out.println("params: " + params);
    }

    public String getParam(String key) {
        return params.get(key);
    }
}
