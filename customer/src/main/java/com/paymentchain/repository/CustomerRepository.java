package com.paymentchain.repository;

import com.paymentchain.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.code=?1")
    Customer findByCode(String code);

    @Query("SELECT c FROM Customer c WHERE c.iban=?1")
    Customer findByAccount(String iban);
}
