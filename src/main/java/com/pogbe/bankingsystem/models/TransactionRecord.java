package com.pogbe.bankingsystem.models;

import com.pogbe.bankingsystem.constants.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING) // Debit or a credit
    private TransactionType transactionType;

    private String description;

    private BigDecimal amount;

    @Column(unique = true)
    private String transactionReference;

    @ManyToOne
    @JoinColumn(name = "sender_user",nullable = false)
    private Account senderAccount;

    @ManyToOne
    @JoinColumn(name = "receiver_user",nullable = false)
    private Account receiverAccount;
}
