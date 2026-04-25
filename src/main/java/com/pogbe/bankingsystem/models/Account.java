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
    private int accountNumber;

    private BigDecimal accountBalance;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id",nullable = false,unique = true)
    private UserModel user;

}
