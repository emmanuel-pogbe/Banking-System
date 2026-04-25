package com.pogbe.bankingsystem.models;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique=true)
    private String accountNumber;

    private String firstThreeDigits;
    private String lastThreeDigits;

    private String accountPin;

    private BigDecimal accountBalance;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id",nullable = false,unique = true)
    private UserModel user;

    public Account(String generatedAccountNumber, String firstThreeDigits, String lastThreeDigits, String accountPin) {
        this.accountNumber = generatedAccountNumber;
        this.firstThreeDigits = firstThreeDigits;
        this.lastThreeDigits = lastThreeDigits;
        this.accountPin = accountPin;
        this.accountBalance = BigDecimal.ZERO;
    }
}
