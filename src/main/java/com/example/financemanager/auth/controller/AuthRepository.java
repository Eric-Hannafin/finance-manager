package com.example.financemanager.auth.controller;

import com.example.financemanager.auth.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Customer, Long> {
}