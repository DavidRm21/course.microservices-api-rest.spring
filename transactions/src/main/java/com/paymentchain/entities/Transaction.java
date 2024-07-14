package com.paymentchain.entities;

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
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String reference;
    @Column(name = "account_iban")
    private String accountIban;
    private String date;
    private double amount;
    private double fee;
    private String description;
    private String status;
    private String channel;
}
