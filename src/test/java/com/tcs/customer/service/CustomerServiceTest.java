package com.tcs.customer.service;

import com.tcs.customer.exception.CustomerNotFoundException;
import com.tcs.customer.model.entities.Customer;
import com.tcs.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private UUID customerId;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerId = UUID.randomUUID();
        customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setCustomerName("Test User");
        customer.setCustomerEmail("test@example.com");
        customer.setAnnualSpend(5000.0);
        customer.setLastPurchaseDate(LocalDateTime.now().minusMonths(3));
    }

    @Test
    void testCreateCustomer() {
        when(repository.save(any(Customer.class))).thenReturn(customer);
        Customer result = customerService.create(customer);
        assertEquals(customer.getCustomerId(), result.getCustomerId());
    }

    @Test
    void testGetById_Success() {
        when(repository.findById(customerId)).thenReturn(Optional.of(customer));
        Optional<Customer> result = customerService.getByCustomerId(customerId);
        assertEquals("Test User", result.get().getCustomerName());
    }

    @Test
    void testGetById_NotFound() {
        when(repository.findById(customerId)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> customerService.getByCustomerId(customerId));
    }

    @Test
    void testUpdateCustomer_Success() {
        Customer updated = new Customer();
        updated.setCustomerName("Updated Name");
        updated.setCustomerEmail("updated@example.com");
        updated.setAnnualSpend(7500.0);
        updated.setLastPurchaseDate(LocalDateTime.now().minusMonths(1));

        when(repository.findById(customerId)).thenReturn(Optional.of(customer));
        when(repository.save(any(Customer.class))).thenReturn(updated);

        Customer result = customerService.updateCustomerDetails(customerId, updated);
        assertEquals("Updated Name", result.getCustomerName());
    }

    @Test
    void testDeleteCustomer_NotFound() {
        when(repository.existsById(customerId)).thenReturn(false);
        assertThrows(CustomerNotFoundException.class, () -> customerService.delete(customerId));
    }

    @Test
    void testDeleteCustomer_Success() {
        when(repository.existsById(customerId)).thenReturn(true);
        doNothing().when(repository).deleteById(customerId);

        assertDoesNotThrow(() -> customerService.delete(customerId));
        verify(repository, times(1)).deleteById(customerId);
    }

    @Test
    void testCalculateTier_Platinum() {
        customer.setAnnualSpend(15000.0);
        customer.setLastPurchaseDate(LocalDateTime.now().minusMonths(3));
        assertEquals("Platinum", customerService.calculateMemberShipTier(customer));
    }

    @Test
    void testCalculateTier_Gold() {
        customer.setAnnualSpend(5000.0);
        customer.setLastPurchaseDate(LocalDateTime.now().minusMonths(9));
        assertEquals("Gold", customerService.calculateMemberShipTier(customer));
    }

    @Test
    void testCalculateTier_Silver() {
        customer.setAnnualSpend(800.0);
        customer.setLastPurchaseDate(LocalDateTime.now().minusMonths(13));
        assertEquals("Silver", customerService.calculateMemberShipTier(customer));
    }

    @Test
    void testGetByName_Success() {
        when(repository.findByCustomerName("Test User")).thenReturn(Optional.of(customer));
        Optional<Customer> result = customerService.getByCustomerName("Test User");
        assertEquals("Test User", result.get().getCustomerName());
    }

    @Test
    void testGetByName_NotFound() {
        when(repository.findByCustomerName("Unknown")).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> customerService.getByCustomerName("Unknown"));
    }

    @Test
    void testGetByEmail_Success() {
        when(repository.findByCustomerEmail("test@example.com")).thenReturn(Optional.of(customer));
        Optional<Customer> result = customerService.getByCustomerEmail("test@example.com");
        assertEquals("test@example.com", result.get().getCustomerEmail());
    }

    @Test
    void testGetByEmail_NotFound() {
        when(repository.findByCustomerEmail("unknown@example.com")).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> customerService.getByCustomerEmail("unknown@example.com"));
    }
}