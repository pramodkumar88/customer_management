package com.tcs.customer.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "CUSTOMER_DETIALS")
public class Customer   {
    @Id
    @GeneratedValue
    @Column(name = "CUSTOMER_ID")
    private UUID customerId;

    @NotBlank(message = "Customer name cannot be blank")
    @Size(min = 3, max = 100, message = "Customer name must be between 3 and 100 characters")
    @Column(name ="CUSTOMER_NAME")
    private String customerName;

    @NotBlank(message = "Customer email cannot be blank")
    @Email(message = "Customer email must be a valid email address")
    @Size(max = 100, message = "Customer email cannot exceed 100 characters")
    @Column(name = "CUSTOMER_EMAIL")
    private String customerEmail;

    @NotNull(message = "Annual spend cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Annual spend must be greater than zero")
    @Column(name="ANNUAL_SPEND")
    private Double annualSpend;

    @PastOrPresent(message = "Last purchase date cannot be in the future")
    @Column(name = "LAST_PURCHASE_DATE")
    private LocalDateTime lastPurchaseDate;

}
