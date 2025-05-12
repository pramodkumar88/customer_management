package com.tcs.customer.model.dto;

import com.tcs.customer.model.entities.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
    private UUID customerId;
    private String customerName;
    private String customerEmail;
    private Double annualSpend;
    private LocalDateTime lastPurchaseDate;
    private String tier;

    public CustomerResponse(Customer customer, String tier) {
        this.customerId = customer.getCustomerId();
        this.customerName = customer.getCustomerName();
        this.customerEmail = customer.getCustomerEmail();
        this.annualSpend = customer.getAnnualSpend();
        this.lastPurchaseDate = customer.getLastPurchaseDate();
        this.tier = tier;
    }

}
