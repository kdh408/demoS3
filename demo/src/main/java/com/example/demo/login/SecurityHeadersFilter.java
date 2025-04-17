package com.example.demo.login;

import jakarta.servlet.*;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityHeadersFilter {

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
            //sessionCookieConfig.setSecure(true);  --> https에서만 적용 가능
            sessionCookieConfig.setPath("/");
        };
    }

}
