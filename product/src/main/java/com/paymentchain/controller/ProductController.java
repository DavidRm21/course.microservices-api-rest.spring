package com.paymentchain.controller;

import com.paymentchain.entities.Product;
import com.paymentchain.repository.ProductRepository;
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
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Product> product = productRepository.findAll();
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") long id) {
        Product product = productRepository.findById(id).orElse(null);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Product newProduct) {
        Product product = productRepository.save(newProduct);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody Product updateProduct) {
        Product product =  productRepository.findById(id).orElse(null);
        if (product!=null){
            product.setName(updateProduct.getName());
            product.setCode(updateProduct.getCode());
            Product save = productRepository.save(product);
            return ResponseEntity.ok(save);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}