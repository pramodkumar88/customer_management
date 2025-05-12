package com.tcs.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcs.customer.exception.CustomerNotFoundException;
import com.tcs.customer.model.entities.Customer;
import com.tcs.customer.service.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.tcs.customer.request.JSONRequest.buildCustomer;
import static com.tcs.customer.request.JSONRequest.createUserRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerServiceImpl customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateCustomer_Success() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerName("test");
        customer.setCustomerEmail("test@test.com");
        customer.setAnnualSpend(5000.0);
        customer.setLastPurchaseDate(LocalDateTime.now());
        Mockito.when(customerService.create(any(Customer.class))).thenReturn(customer);
        Mockito.when(customerService.calculateMemberShipTier(any(Customer.class))).thenReturn("Gold");
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("test"))
                .andExpect(jsonPath("$.tier").value("Gold"));
    }

    @Test
    void getCustomerById_success() throws Exception {
        UUID id = UUID.randomUUID();
        Customer customer = buildCustomer();
        customer.setCustomerId(id);
        Mockito.when(customerService.getByCustomerId(id)).thenReturn(Optional.of(customer));
        Mockito.when(customerService.calculateMemberShipTier(customer)).thenReturn("Gold");

        mockMvc.perform(get("/customers/{customerid}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerEmail").value("test@testuser.com"))
                .andExpect(jsonPath("$.tier").value("Gold"));
    }

    @Test
    void testGetCustomerById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(customerService.getByCustomerId(id)).thenThrow(new RuntimeException("Customer not found"));
        mockMvc.perform(get("/customers/" + id))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getCustomerByName_success() throws Exception {
        Mockito.when(customerService.getByCustomerName("Test User")).thenReturn(Optional.of(buildCustomer()));
        Mockito.when(customerService.calculateMemberShipTier(any(Customer.class))).thenReturn("Gold");

        mockMvc.perform(get("/customers").param("name", "Test User"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Test User"));
    }

    @Test
    void getCustomerByEmail_success() throws Exception {
        Mockito.when(customerService.getByCustomerEmail("test@testuser.com")).thenReturn(Optional.of(buildCustomer()));
        Mockito.when(customerService.calculateMemberShipTier(any(Customer.class))).thenReturn("Gold");

        mockMvc.perform(get("/customers").param("email", "test@testuser.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerEmail").value("test@testuser.com"));
    }

    @Test
    void updateCustomer_success() throws Exception {
        UUID customerId = UUID.randomUUID();
        Customer updatedCustomer = buildCustomer();
        updatedCustomer.setCustomerId(customerId);
        updatedCustomer.setCustomerName("New Test User");
        Mockito.when(customerService.updateCustomerDetails(customerId, updatedCustomer)).thenReturn(updatedCustomer);
        Mockito.when(customerService.calculateMemberShipTier(any(Customer.class))).thenReturn("Gold");
        mockMvc.perform(put("/customers/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("New Test User"));
    }

    @Test
    void deleteCustomer_success() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/customers/" + id))
                .andExpect(status().isNoContent());
        Mockito.verify(customerService).delete(id);
    }


    @Test
    void createCustomer_shouldReturnBadRequest_whenIdIsProvided() throws Exception {
        Customer customerWithId = new Customer();
        customerWithId.setCustomerId(UUID.randomUUID());
        customerWithId.setCustomerName("Test User 1");
        customerWithId.setCustomerEmail("test@testuser1.com");
        customerWithId.setAnnualSpend(1000.0);
        customerWithId.setLastPurchaseDate(LocalDateTime.now().minusMonths(2));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerWithId)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID should not be provided in the request body"));
    }


    @Test
    void updateCustomer_shouldReturnNotFound_whenExceptionIsThrown() throws Exception {
        UUID fakeId = UUID.randomUUID();
        Customer updateRequest = new Customer();
        updateRequest.setCustomerName("Updated Test Name");
        updateRequest.setCustomerEmail("updated@testuser.com");
        updateRequest.setAnnualSpend(2000.0);
        updateRequest.setLastPurchaseDate(LocalDateTime.now().minusMonths(2));

        String errorMessage = "Customer not found with id: " + fakeId;

        Mockito.when(customerService.updateCustomerDetails(fakeId, updateRequest))
                .thenThrow(new CustomerNotFoundException(errorMessage));

        mockMvc.perform(put("/customers/" + fakeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(errorMessage));
    }

    @Test
    void testHandleGenericException_shouldReturnInternalServerError() throws Exception {
        UUID customerId = UUID.randomUUID();

        Customer updateRequest = new Customer();
        updateRequest.setCustomerName("Invalid Data");
        updateRequest.setCustomerEmail("invalid@test.com");
        updateRequest.setAnnualSpend(1500.0);
        updateRequest.setLastPurchaseDate(LocalDateTime.now());

        // Simulate unexpected exception
        Mockito.when(customerService.updateCustomerDetails(customerId, updateRequest))
                .thenThrow(new RuntimeException("Unexpected failure"));

        mockMvc.perform(put("/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred: Unexpected failure"));
    }
}
