package com.example.financemanager.auth.controller;

import com.example.financemanager.auth.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.userName = :usernameOrEmail OR c.email = :usernameOrEmail")
    Customer findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

}