package com.tcs.customer.repository;


import com.tcs.customer.model.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByCustomerName(String name);
    Optional<Customer> findByCustomerEmail(String email);
}
