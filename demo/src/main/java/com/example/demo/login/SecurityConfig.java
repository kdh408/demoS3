package com.example.demo.login;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    //    private final HttpSecurity httpSecurity;
    private LoginService loginService;
    @Autowired
    private customAuthenticationFailureHandler FailureHandler;
    @Autowired
    private customAuthenticationSuccessHandler SuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // antMatchers 부분도 deprecated 되어 requestMatchers로 대체
        return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/img/**", "/lib/**");
    }



    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth
                        .requestMatchers("/user/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/myinfo").hasRole("MEMBER")
                        .requestMatchers("/**").permitAll()
        );

        http.formLogin(form ->
                form
                        .loginPage("/user/login")
                        //.defaultSuccessUrl("/user/login/result")
                        .successHandler(SuccessHandler)
                        .failureHandler(FailureHandler)
                        .permitAll()
        );

        http.logout(form ->
                form
                        .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                        .logoutSuccessUrl("/user/logout/result")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
        );

        http.exceptionHandling(form ->
                form.accessDeniedPage("/user/denied")
        );

        //http.addFilterAfter(new SecurityHeadersFilter(), SecurityContextPersistenceFilter.class);

        http.headers(headers ->
                headers
                        //https인 경우 설정 필요 .httpStrictTransportSecurity()
                        .contentTypeOptions(contentType -> {})    //nosniff
                        .xssProtection(xssProtection ->xssProtection.disable())
                        .contentSecurityPolicy(csp ->
                                //csp.policyDirectives("default-src 'self'; script-src 'self';")    //CSP
                                csp.policyDirectives("script-src 'self';")    //CSP
                        )
        );


        return http.build();
    }

}
