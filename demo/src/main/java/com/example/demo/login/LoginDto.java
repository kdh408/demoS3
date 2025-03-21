package com.example.demo.login;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LoginDto {
    private Long id;
    private String email;
    private String name;
    private String password;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String passwordCheck;

    public Login toEntity(){
        return Login.builder()
                .id(id)
                .email(email)
                .name(name)
                .password(password)
                .failedAttempts(0)
                .locked(false)
                .build();
    }

    @Builder
    public LoginDto(Long id, String email, String name, String password, String passwordCheck) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.passwordCheck = passwordCheck;
    }
}