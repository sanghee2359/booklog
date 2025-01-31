package com.api.booklog.security.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CorsLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String origin = httpRequest.getHeader("Origin");

        if (origin != null) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin); // 동적으로 Origin 설정
            httpResponse.setHeader("Access-Control-Allow-Methods", "HEAD, GET, PUT, POST, DELETE, PATCH");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
        }

        // 요청 헤더 로그 출력
        System.out.println("Request Headers: ");
        httpRequest.getHeaderNames().asIterator().forEachRemaining(header -> {
            System.out.println(header + ": " + httpRequest.getHeader(header));
        });

        // 응답 헤더 로그 출력
        System.out.println("Response Headers: ");
        httpResponse.getHeaderNames().forEach(header -> {
            System.out.println(header + ": " + httpResponse.getHeader(header));
        });

        // 필터 체인을 통해 요청을 계속 진행
        chain.doFilter(request, response);
    }
}
