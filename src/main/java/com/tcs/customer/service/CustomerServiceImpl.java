package com.tcs.customer.service;

import com.tcs.customer.exception.CustomerNotFoundException;
import com.tcs.customer.model.entities.Customer;
import com.tcs.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static com.tcs.customer.constants.CustomerContants.*;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> getByCustomerId(UUID customerId) {
        return Optional.ofNullable(customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId)));
    }

    @Override
    public Optional<Customer> getByCustomerName(String name) {
        return Optional.ofNullable(customerRepository.findByCustomerName(name).orElseThrow(() -> new CustomerNotFoundException("Customer not found with Customer Name: " + name)));
    }

    @Override
    public Optional<Customer> getByCustomerEmail(String email) {
        return Optional.ofNullable(customerRepository.findByCustomerEmail(email).orElseThrow(() -> new CustomerNotFoundException("Customer not found with Customer Email: " + email)));
    }

    @Override
    public Customer updateCustomerDetails(UUID id, Customer updatedCustomer) {
        return customerRepository.findById(id).map(customer -> {
            customer.setCustomerName(updatedCustomer.getCustomerName());
            customer.setCustomerEmail(updatedCustomer.getCustomerEmail());
            customer.setAnnualSpend(updatedCustomer.getAnnualSpend());
            customer.setLastPurchaseDate(updatedCustomer.getLastPurchaseDate());
            return customerRepository.save(customer);
        }).orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
    }

    @Override
    public void delete(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found with id: " + customerId);
        }
        customerRepository.deleteById(customerId);
    }

    @Override
    public String calculateMemberShipTier(Customer customer) {
        if (customer.getAnnualSpend() == null) return SILVER;

        double spend = customer.getAnnualSpend();
        LocalDateTime lastPurchase = customer.getLastPurchaseDate();

        if (spend >= 10000 && lastPurchase != null && ChronoUnit.MONTHS.between(lastPurchase, LocalDateTime.now()) <= 6) {
            return PLATINUM;
        } else if (spend >= 1000 && spend < 10000 && lastPurchase != null && ChronoUnit.MONTHS.between(lastPurchase, LocalDateTime.now()) <= 12) {
            return GOLD;
        } else {
            return SILVER;
        }
    }
}
