package com.tcs.customer.request;

import com.tcs.customer.model.dto.CustomerResponse;
import com.tcs.customer.model.entities.Customer;

import java.time.LocalDateTime;

import static com.tcs.customer.constants.CustomerContants.GOLD;

public class JSONRequest {

    public static final String createUserRequest = """
            {
                "customerName": "test",
                "customerEmail": "test@test.com",
                "annualSpend": 5000,
                "lastPurchaseDate": "2024-10-10T10:10:10"
            }
        """;

    public static Customer buildCustomer() {
        Customer c = new Customer();
        c.setCustomerName("Test User");
        c.setCustomerEmail("test@testuser.com");
        c.setAnnualSpend(1200.0);
        c.setLastPurchaseDate(LocalDateTime.now().minusMonths(3));
        return c;
    }

    public static CustomerResponse buildCustomerWithTier() {
        CustomerResponse c = new CustomerResponse();
        c.setCustomerName("Test User");
        c.setCustomerEmail("test@testuser.com");
        c.setAnnualSpend(1200.0);
        c.setTier(GOLD);
        c.setLastPurchaseDate(LocalDateTime.now().minusMonths(3));
        return c;
    }
}
