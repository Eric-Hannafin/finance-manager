package com.example.financemanager.controller;

import com.example.financemanager.model.Transaction;
import com.example.financemanager.model.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String AMOUNT = "100.0";

    @Test
    @WithMockUser
    void testAddTransaction() throws Exception {
        Transaction transaction = new Transaction();

        transaction.setType("DEBIT");
        transaction.setAmount(new java.math.BigDecimal("100.00"));
        transaction.setDescription("Test Transaction");

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(transaction.getType()))
                .andExpect(jsonPath("$.amount").value(AMOUNT))
                .andExpect(jsonPath("$.description").value("Test Transaction"));

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @WithMockUser
    void testGetAllTransactions() throws Exception {
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionRepository.findAll()).thenReturn(transactions);

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(transactions.size()));

        verify(transactionRepository, times(1)).findAll();
    }
}