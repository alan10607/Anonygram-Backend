package com.alan10607.ag.config;

import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class XssHtmlEscapedRequestWrapper extends HttpServletRequestWrapper {
    public XssHtmlEscapedRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return escapeHtml(value);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        Map<String, String[]> escapedMap = new HashMap<>();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] escapedValues = Arrays.stream(entry.getValue())
                    .map(this::escapeHtml)
                    .toArray(String[]::new);
            escapedMap.put(entry.getKey(), escapedValues);
        }

        return escapedMap;
    }

    private String escapeHtml(String input) {
        return HtmlUtils.htmlEscape(input);
    }
}
