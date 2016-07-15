package com.handson.infra;

import java.util.List;
import java.util.Map;

/**
 * Should grow if needed ;)
 */
public class HttpRequest {

    private final Map<String, List<String>> parameters;

    public HttpRequest(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String name) {
        if (parameters.get(name) == null) {
            return null;
        }
        return parameters.get(name).get(0);
    }
}
