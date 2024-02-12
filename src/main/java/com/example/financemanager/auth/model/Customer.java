package com.example.financemanager.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username")
    private String userName;
    @Column(name = "password_hash")
    private String password;
    @Column(name = "account_number")
    private String accountNumber;
    @Column(name = "email")
    private String email;
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

}