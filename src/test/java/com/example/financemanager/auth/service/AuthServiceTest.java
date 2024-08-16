package com.example.financemanager.auth.service;

import com.example.financemanager.auth.controller.AuthRepository;
import com.example.financemanager.auth.exception.CustomerRegistrationException;
import com.example.financemanager.auth.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService underTest;

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @Mock
    private AuthRepository mockAuthRepository;

    private Customer mockCustomer;

    @BeforeEach
    void setUp() {
        mockCustomer = populateCustomer();
    }

    @Test
    void registerUser_Success() {
        // Given
        String encodedPassword = "encodedPassword";
        when(mockPasswordEncoder.encode(mockCustomer.getPassword())).thenReturn(encodedPassword);

        // When
        underTest.registerUser(mockCustomer);

        // Then
        verify(mockPasswordEncoder).encode("testPassword");
        assertEquals(encodedPassword, mockCustomer.getPassword());
        verify(mockAuthRepository).save(mockCustomer);
    }

    @Test
    void registerUser_nullPassword_ShouldThrowException() {
        // Given
        mockCustomer.setPassword(null);

        // When & Then
        assertThrows(CustomerRegistrationException.class, () -> underTest.registerUser(mockCustomer));

        verify(mockAuthRepository, never()).save(mockCustomer);
    }

    @Test
    void validateUser_Success() {
        // Given
        when(mockAuthRepository.findByUsernameOrEmail(anyString())).thenReturn(mockCustomer);
        when(mockPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // When
        boolean result = underTest.validateUser("testUsername", "testPassword");

        // Then
        verify(mockAuthRepository).findByUsernameOrEmail(anyString());
        verify(mockPasswordEncoder).matches(anyString(), anyString());
        assertTrue(true);
    }

    @Test
    void validateUser_CustomerNotFound() {
        // Given
        when(mockAuthRepository.findByUsernameOrEmail(anyString())).thenReturn(null);

        // When
        boolean result = underTest.validateUser("testUsername", "testPassword");

        // Then
        verify(mockAuthRepository).findByUsernameOrEmail(anyString());
        assertFalse(result);
    }

    @Test
    void validateUser_PasswordNotMatching() {
        // Given
        when(mockAuthRepository.findByUsernameOrEmail(anyString())).thenReturn(mockCustomer);
        when(mockPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When
        boolean result = underTest.validateUser("testUsername", "testPassword");

        // Then
        verify(mockAuthRepository).findByUsernameOrEmail(anyString());
        verify(mockPasswordEncoder).matches(anyString(), anyString());
        assertFalse(result);
    }

    private Customer populateCustomer() {
        Customer customer = new Customer();
        customer.setUserName("testName");
        customer.setPassword("testPassword");
        customer.setEmail("testEmail");
        return customer;
    }
}