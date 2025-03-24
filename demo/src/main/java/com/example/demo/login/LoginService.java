package com.example.demo.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class LoginService implements UserDetailsService {
    private LoginRepository loginRepository;
    HttpServletRequest request;

    @Transactional
    public Long joinUser(LoginDto loginDto) {

        String email = loginDto.getEmail();

        if (!isValidEmail(email)) {
            request.getSession().setAttribute("errorMessage", "유효한 이메일이 아닙니다.");
            return null;
        }

        //중복확인
        if (loginRepository.existsByEmail(email)) {
            request.getSession().setAttribute("errorMessage", "이미 사용 중인 이메일입니다.");
            return null;
        }

        if (!loginDto.getPassword().equals(loginDto.getPasswordCheck())) {
            request.getSession().setAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return null;
        }



        // 비밀번호 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        loginDto.setPassword(passwordEncoder.encode(loginDto.getPassword()));

        return loginRepository.save(loginDto.toEntity()).getId();
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Optional<Login> userEntityWrapper = loginRepository.findByEmail(userEmail);
        Login userEntity = userEntityWrapper.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (("admin@example.com").equals(userEmail)) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));
        }

        return new User(userEntity.getEmail(), userEntity.getPassword(), authorities);

    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    public List<Login> findAllMembers() {
        return loginRepository.findAll();
    }

    public void userDelete(Long id) {
        loginRepository.deleteById(id);
    }

    public HashMap<String, Object>  emailOverlap(String useremail) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("result", loginRepository.existsByEmail(useremail));
        return map;
    }

    //닉네임 중복 검사ß
    public HashMap<String, Object> nameOverlap(String username) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("result", loginRepository.existsByName(username));
        return map;
    }

}