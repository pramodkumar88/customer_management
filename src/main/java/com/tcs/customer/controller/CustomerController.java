package com.tcs.customer.controller;

import com.tcs.customer.model.dto.CustomerResponse;
import com.tcs.customer.model.entities.Customer;
import com.tcs.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService service;

    @Operation(summary = "Get customer by ID", description = "Fetch customer details by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer"),
            @ApiResponse(responseCode = "404", description = "Customer not found with the provided ID")
    })
    @GetMapping("/{customerid}")
    public ResponseEntity<?> getCustomerById(@PathVariable UUID customerid) {
        Optional<Customer> customer = service.getByCustomerId(customerid);
        return customer.map(c -> ResponseEntity.ok(withTier(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new customer", description = "Create a new customer. The customer ID should not be provided in the request body.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created customer"),
            @ApiResponse(responseCode = "400", description = "Bad request - customer ID should not be provided in the request body")
    })
    @PostMapping
    public ResponseEntity<?> createCustomer(@Valid @RequestBody Customer customer) {
        if (customer.getCustomerId() != null) {
            return ResponseEntity.badRequest().body("ID should not be provided in the request body");
        }
        Customer created = service.create(customer);
        return ResponseEntity.status(201).body(withTier(created));
    }


    @Operation(summary = "Get customer by name", description = "Fetch customer details by their name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer"),
            @ApiResponse(responseCode = "404", description = "Customer not found with the provided name")
    })
    @GetMapping(params = "name")
    public ResponseEntity<?> getCustomerByName(@RequestParam String name) {
        Optional<Customer> customer = service.getByCustomerName(name);
        return customer.map(c -> ResponseEntity.ok(withTier(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get customer by email", description = "Fetch customer details by their email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer"),
            @ApiResponse(responseCode = "404", description = "Customer not found with the provided email")
    })
    @GetMapping(params = "email")
    public ResponseEntity<?> getCustomerByEmail(@RequestParam String email) {
        Optional<Customer> customer = service.getByCustomerEmail(email);
        return customer.map(c -> ResponseEntity.ok(withTier(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Operation(summary = "Update customer details", description = "Update the details of an existing customer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated customer"),
            @ApiResponse(responseCode = "404", description = "Customer not found with the provided ID")
    })
    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateCustomer(@PathVariable UUID customerId, @Valid @RequestBody Customer customer) {
            Customer updated = service.updateCustomerDetails(customerId, customer);
            return ResponseEntity.ok(withTier(updated));
    }


    @Operation(summary = "Delete customer", description = "Delete an existing customer by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted customer"),
            @ApiResponse(responseCode = "404", description = "Customer not found with the provided ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CustomerResponse withTier(Customer customer) {
        return new CustomerResponse(customer, service.calculateMemberShipTier(customer));
    }
}
