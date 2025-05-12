package com.tcs.customer.service;


import com.tcs.customer.model.entities.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    Customer create(Customer customer);
    Optional<Customer> getByCustomerId(UUID customerId);
    Optional<Customer> getByCustomerName(String name);
    Optional<Customer> getByCustomerEmail(String email);
    Customer updateCustomerDetails(UUID id, Customer updatedCustomer);
    void delete(UUID customerId);
    String calculateMemberShipTier(Customer customer);

}
