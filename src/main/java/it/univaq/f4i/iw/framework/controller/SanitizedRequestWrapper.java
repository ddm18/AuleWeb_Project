package it.univaq.f4i.iw.framework.controller;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import it.univaq.f4i.iw.framework.security.SecurityHelpers;

public class SanitizedRequestWrapper extends HttpServletRequestWrapper {

    public SanitizedRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return (value != null) ? SecurityHelpers.stripSlashes(value) : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> sanitizedMap = new HashMap<>();
        super.getParameterMap().forEach((key, values) -> {
            String[] sanitizedValues = (values != null) 
                ? Arrays.stream(values).map(SecurityHelpers::stripSlashes).toArray(String[]::new)
                : null;
            sanitizedMap.put(key, sanitizedValues);
        });
        return sanitizedMap;
    }
}
