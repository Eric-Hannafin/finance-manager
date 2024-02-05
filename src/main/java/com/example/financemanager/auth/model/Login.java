package com.example.financemanager.auth.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor
public class Login {

    private String usernameOrEmail;
    private String password;
}