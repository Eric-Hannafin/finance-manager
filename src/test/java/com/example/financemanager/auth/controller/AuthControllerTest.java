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
    private AuthService mockAuthService;

    @MockBean
    private JwtUtil mockJwtUtil;

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
        // Given
        doNothing().when(mockAuthService).registerUser(any(Customer.class));

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        verify(mockAuthService, times(1)).registerUser(any(Customer.class));

    }

    @Test
    @WithMockUser
    void testRegisterUser_Failure() throws Exception {
        // Given
        doThrow(new RuntimeException()).when(mockAuthService).registerUser(any(Customer.class));

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User registration failed"));

        verify(mockAuthService, times(1)).registerUser(any(Customer.class));

    }

    @Test
    @WithMockUser
    void testAuthenticateUser_Success() throws Exception {
        // Given
        Cookie mockCookie = new Cookie("token", "dummyTokenValue");
        given(mockAuthService.validateUser(any(String.class), any(String.class))).willReturn(true);
        given(mockJwtUtil.createToken(any(long.class), any(String.class))).willReturn(mockCookie);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("token"))
                .andExpect(content().string("User authenticated successfully"));
    }

    @Test
    @WithMockUser
    void testAuthenticateUser_Failure() throws Exception {
        // Given
        given(mockAuthService.validateUser(any(String.class), any(String.class))).willReturn(false);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Failed to login user"));
    }

    @Test
    @WithMockUser
    void testRefreshToken_InvalidToken() throws Exception {
        // Given
        Cookie mockRefreshToken = new Cookie("refreshToken", "invalid-refresh-token");
        given(mockJwtUtil.validateToken("valid-refresh-token")).willReturn(false);

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                        .cookie(mockRefreshToken)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Failed to validate refresh token"));
    }

    @Test
    @WithMockUser
    void testRefreshToken_validToken() throws Exception {
        // Given
        Cookie mockRefreshToken = new Cookie("refreshToken", "valid-refresh-token");
        Cookie mockAccessToken = new Cookie("accessToken", "valid-refresh-token");
        given(mockJwtUtil.validateToken("valid-refresh-token")).willReturn(true);
        given(mockJwtUtil.createToken(anyLong(), anyString())).willReturn(mockAccessToken);

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                        .cookie(mockRefreshToken)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Refresh token accepted and new access token provided"));
    }

    @Test
    @WithMockUser
    void testRefreshToken_NoToken() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/refresh")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("No cookies present. Unable to refresh token."));
    }

}