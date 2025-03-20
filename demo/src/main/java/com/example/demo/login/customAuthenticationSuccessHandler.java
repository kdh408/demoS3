package com.example.demo.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class customAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final LoginRepository loginRepository;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String loginId = request.getParameter("username");

        Login user = loginRepository.findByEmail(loginId)
                .orElseThrow(()->new UsernameNotFoundException("Invalid username or password"));

        if (user.getLocked() || user.getFailedAttempts() >= 5) {
            request.getSession().invalidate();
            request.getSession().setAttribute("errorMessage", "계정 잠김. 관리자에게 문의 바람");
            response.sendRedirect("/user/login");
        }else{
            user.setFailedAttempts();
            loginRepository.save(user);
            response.sendRedirect("/user/login/result");
        }
    }
}
