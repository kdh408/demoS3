package com.example.demo.login;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityHeadersFilter {
    /*@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Cookie cookie = new Cookie("name", "value");
        cookie.setHttpOnly(true); //httpOnly 생성 > javaScript에서 쿠키에 접근 불가 (XSS 공격으로부터 보호)
        cookie.setSecure(true);
        cookie.setPath("/");  //쿠키가 적용될 경로 설정, 사이트 전체에 대해 쿠키가 유효
        httpResponse.addCookie(cookie); // http 응답에 쿠키 추가

        chain.doFilter(request, response);
    }*/

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieCustomizer() {
        return factory -> factory.addContextCustomizers(context -> {
            context.setUseHttpOnly(true); // HttpOnly
        });
    }

    @Bean
    public ServletContextInitializer initializer() {
        return servletContext -> {
            SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
            sessionCookieConfig.setHttpOnly(true);
            sessionCookieConfig.setSecure(false);
            //sessionCookieConfig.setSecure(true);  --> https에서만 적용 가능
            sessionCookieConfig.setPath("/");
        };
    }

}
