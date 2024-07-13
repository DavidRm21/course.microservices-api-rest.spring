package com.paymentchain.controller;

import com.paymentchain.entities.Transaction;
import com.paymentchain.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping()
@AllArgsConstructor
public class TransactionController {

    private final TransactionRepository transactionRepository;

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Transaction> transactions =  transactionRepository.findAll();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") long id) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Transaction newTransaction) {
        Transaction transaction = transactionRepository.save(newTransaction);
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody Transaction updateTransaction) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        if(transaction!=null){
            transaction.setAccountIban(updateTransaction.getAccountIban());
            transaction.setFee(updateTransaction.getFee());
            transaction.setDate(updateTransaction.getDate());
            transaction.setStatus(updateTransaction.getStatus());
            transaction.setChannel(updateTransaction.getChannel());
            transaction.setDescription(updateTransaction.getDescription());
            transaction.setAmount(updateTransaction.getAmount());
            transaction.setReference(updateTransaction.getReference());
            Transaction save = transactionRepository.save(transaction);
            return ResponseEntity.ok(save);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        transactionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}