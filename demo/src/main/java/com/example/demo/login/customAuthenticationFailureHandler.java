package com.example.demo.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;


@Component
public class customAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final LoginRepository loginRepository;

    public customAuthenticationFailureHandler(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String loginId = request.getParameter("username"); // 로그인 시도한 이메일
       // Login user = loginRepository.findByEmail(loginId)
       //         .orElseThrow(() -> new UsernameNotFoundException("Invalid username or passsword"));


        Login user = loginRepository.findByEmail(loginId).orElse(null);

        if(user==null){
            request.getSession().setAttribute("errorMessage", "!!로그인 실패!! " + "로그인 시도 제한 횟수는 5회입니다.");
            response.sendRedirect("/user/login");
        }


        //로그인 횟수 5회 이상
        if (user.getFailedAttempts() >= 5) {
            request.getSession().setAttribute("errorMessage", "계정 잠김. 관리자에게 문의 바람");
            response.sendRedirect("/user/login");
        }else{
            //로그인 횟수 5회 미만
            user.incFailedAttempts();
            loginRepository.save(user);

            //attempt +1 == 5
            if(user.getFailedAttempts() == 5) {
                user.loginLocked(true);
                loginRepository.save(user);
                request.getSession().setAttribute("errorMessage", "로그인 실패"+ user.getFailedAttempts()+"/5 -> 계정 잠김. 관리자 문의 바람");
            }else{
                request.getSession().setAttribute("errorMessage", "!!로그인 실패!! " + "로그인 시도 제한 횟수는 5회입니다.");
            }

            response.sendRedirect("/user/login"); // 로그인 페이지로 리디렉트
        }
    }
}

