package com.cinemaabyss.proxy.controller;

import com.cinemaabyss.proxy.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProxyController {

    private final ProxyService proxyService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\": true}");
    }

    @RequestMapping(value = "/api/movies/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> proxyMovies(HttpServletRequest request) throws IOException {
        String path = buildFullPath(request);
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String body = getRequestBody(request);
        HttpHeaders headers = getRequestHeaders(request);

        log.info("Proxying movies request: {} {}", method, path);

        return proxyService.routeMoviesRequest(path, method, body, headers);
    }

    @RequestMapping(value = "/api/users/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> proxyUsers(HttpServletRequest request) throws IOException {
        String path = buildFullPath(request);
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String body = getRequestBody(request);
        HttpHeaders headers = getRequestHeaders(request);

        log.info("Proxying users request: {} {}", method, path);

        return proxyService.routeToMonolith(path, method, body, headers);
    }

    @RequestMapping(value = "/api/payments/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> proxyPayments(HttpServletRequest request) throws IOException {
        String path = buildFullPath(request);
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String body = getRequestBody(request);
        HttpHeaders headers = getRequestHeaders(request);

        log.info("Proxying payments request: {} {}", method, path);

        return proxyService.routeToMonolith(path, method, body, headers);
    }

    @RequestMapping(value = "/api/subscriptions/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> proxySubscriptions(HttpServletRequest request) throws IOException {
        String path = buildFullPath(request);
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String body = getRequestBody(request);
        HttpHeaders headers = getRequestHeaders(request);

        log.info("Proxying subscriptions request: {} {}", method, path);

        return proxyService.routeToMonolith(path, method, body, headers);
    }

    @RequestMapping(value = "/api/events/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> proxyEvents(HttpServletRequest request) throws IOException {
        String path = buildFullPath(request);
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String body = getRequestBody(request);
        HttpHeaders headers = getRequestHeaders(request);

        log.info("Proxying events request: {} {}", method, path);

        return proxyService.routeToEvents(path, method, body, headers);
    }

    private String buildFullPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        
        if (queryString != null && !queryString.isEmpty()) {
            return path + "?" + queryString;
        }
        
        return path;
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        if (request.getContentLength() <= 0) {
            return "";
        }

        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    private HttpHeaders getRequestHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();

        var headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            var headerValues = request.getHeaders(headerName);

            while (headerValues.hasMoreElements()) {
                headers.add(headerName, headerValues.nextElement());
            }
        }

        return headers;
    }
}
