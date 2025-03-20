package com.example.demo.login;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "login")
public class Login {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String email;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer failedAttempts;

    @Column(columnDefinition ="BOOLEAN DEFAULT false")
    private Boolean locked;

    public void setFailedAttempts(){
        this.failedAttempts = 0;
    }

    public void incFailedAttempts() {
        this.failedAttempts++;
    }

    public void loginLocked(boolean state) {
        this.locked = state;
    }


    @Builder
    public Login(Long id, String email, String name, String password, Integer failedAttempts, Boolean locked) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.failedAttempts = 0;
        this.locked = false;
    }
}