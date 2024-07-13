package com.paymentchain.controller;

import com.paymentchain.entities.Customer;
import com.paymentchain.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CustomerControllerController {

    private final CustomerRepository customerRepository;

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Customer> customers = customerRepository.findAll();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") long id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Customer newCustomer) {
        newCustomer.getProducts().forEach(p -> p.setCustomer(newCustomer));
        Customer customerSave = customerRepository.save(newCustomer);
        return ResponseEntity.ok(customerSave);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody Customer updateCustomer) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer != null){
            customer.setName(updateCustomer.getName());
            customer.setPhone(updateCustomer.getPhone());
            customer.setAddress(updateCustomer.getAddress());
            customer.setIban(updateCustomer.getIban());
            customer.setCode(updateCustomer.getCode());
            customer.setSurname(updateCustomer.getSurname());
            Customer save = customerRepository.save(customer);
            return ResponseEntity.ok(save);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        customerRepository.deleteById(id);
        return ResponseEntity.ok("cliente con el id: " + id + " borrado.");
    }
}