package com.example.demo.login;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@Controller
@AllArgsConstructor
public class LoginController {
    private LoginService loginService;

    // 메인 페이지
    @GetMapping("/")
    public String index() {
        return "redirect:/board/list";
    }

    // 회원가입 페이지
    @GetMapping("/user/signup")
    public String dispSignup() {
        return "signup";
    }

    // 회원가입 처리
    @PostMapping("/user/signup")
    public String execSignup(LoginDto loginDto) {
        if(loginDto.getEmail() == "" || loginDto.getPassword() == "" || loginDto.getName() == "") {
            //loginService.alert(SC_INTERNAL_SERVER_ERROR,"cannot singup");
            throw new IllegalArgumentException("회원가입 불가능");//"loginDto cannot be null");
        }

        Long result=loginService.joinUser(loginDto);
        if (result==null) {
            return "redirect:/user/signup";
        }else {
            return "redirect:/user/login";
        }


    }

    // 로그인 페이지
    @GetMapping("/user/login")
    public String dispLogin() {
        return "login";
    }

    // 로그인 결과 페이지
    @GetMapping("/user/login/result")
    public String dispLoginResult() {
        return  "redirect:/board/list";
    }

    // 로그아웃 결과 페이지
    @GetMapping("/user/logout/result")
    public String dispLogout() {
        return "logout";
    }

    //admin 회원관리 페이지
    @GetMapping("/user/admin")
    public String userAdmin(Model model){
        List<Login> login = loginService.findAllMembers();
        model.addAttribute("login", login);
        return "admin";
    }

    @GetMapping("/user/delete/{id}")
    public String userDelete(@PathVariable("id") Long id) {
        loginService.userDelete(id);

        return "redirect:/user/admin";
    }

    @GetMapping("/user/denied")
    public String dispDenied() {
        return "denied";
    }

    // 비밀번호 수정
    @GetMapping("/user/info")
    public String dispMyInfo() {
        return "myinfo";
    }


}