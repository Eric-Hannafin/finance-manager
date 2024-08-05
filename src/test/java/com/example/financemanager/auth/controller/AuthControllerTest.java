package com.example.financemanager.auth.controller;

import com.example.financemanager.auth.model.Customer;
import com.example.financemanager.auth.model.LoginRequest;
import com.example.financemanager.auth.service.AuthService;
import com.example.financemanager.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer customer;
    private LoginRequest login;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setUserName("username");
        customer.setPassword("password");
        customer.setEmail("email");

        login = new LoginRequest();
        login.setUsernameOrEmail("testUserName");
        login.setPassword("testPassword");
    }

    @Test
    @WithMockUser
    void testRegisterUser_Success() throws Exception {

        doNothing().when(authService).registerUser(any(Customer.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        verify(authService, times(1)).registerUser(any(Customer.class));

    }

    @Test
    @WithMockUser
    void testRegisterUser_Failure() throws Exception {

        doThrow(new RuntimeException()).when(authService).registerUser(any(Customer.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User registration failed"));

        verify(authService, times(1)).registerUser(any(Customer.class));

    }

    @Test
    @WithMockUser
    void testAuthenticateUser_Success() throws Exception {
        Cookie mockCookie = new Cookie("token", "dummyTokenValue");
        given(authService.validateUser(any(String.class), any(String.class))).willReturn(true);
        given(jwtUtil.createToken(any(String.class), any(long.class))).willReturn(mockCookie);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("token")) // Check that the cookie exists
                .andExpect(content().string("User authenticated successfully"));
    }

    @Test
    @WithMockUser
    void testAuthenticateUser_Failure() throws Exception {
        given(authService.validateUser(any(String.class), any(String.class))).willReturn(false);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())  // Expect Unauthorized status
                .andExpect(content().string("Failed to login user"));
    }

}