package com.example.demo.login;

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

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginService implements UserDetailsService {
    private LoginRepository loginRepository;

    @Transactional
    public Long joinUser(LoginDto loginDto) {
        // 비밀번호 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        loginDto.setPassword(passwordEncoder.encode(loginDto.getPassword()));

        return loginRepository.save(loginDto.toEntity()).getId();
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Optional<Login> userEntityWrapper = loginRepository.findByEmail(userEmail);
        //Login userEntity = userEntityWrapper.get();
        Login userEntity = userEntityWrapper.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (("admin@example.com").equals(userEmail)) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));
        }

        return new User(userEntity.getEmail(), userEntity.getPassword(), authorities);

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