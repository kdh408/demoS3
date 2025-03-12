package com.example.demo.login;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class LoginCheckController {


    private LoginService loginService;

    @PostMapping("/user/idcheck")
    public HashMap<String, Object> idCheck(@RequestBody Map<String, String> request) {
        String useremail = request.get("email");
        return loginService.emailOverlap(useremail);
    }

    @PostMapping("/user/namecheck")
    public HashMap<String, Object> nameCheck(@RequestBody Map<String, String> request) {
        String username = request.get("name");
        return loginService.nameOverlap(username);
    }

}
