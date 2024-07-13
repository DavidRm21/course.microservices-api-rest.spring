package com.paymentchain.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.entities.Customer;
import com.paymentchain.entities.CustomerProduct;
import com.paymentchain.repository.CustomerRepository;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final WebClient.Builder webClientBuilder;

    HttpClient client = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPINTVL,60)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .responseTimeout(Duration.ofSeconds(1))
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

    private String getProductName(long id){
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://localhost:8002/product")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8002/product"))
                .build();
        JsonNode block = build.method(HttpMethod.GET).uri("/" + id)
                .retrieve().bodyToMono(JsonNode.class).block();
        return block.get("name").asText();
    }

    private List<?> getTransactions(String iban){
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://localhost:8003/transaction")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        List<?> transactions = build.method(HttpMethod.GET).uri(uriBuilder -> uriBuilder
                .path("/customer/transaction")
                .queryParam("iban", iban)
                .build())
                .retrieve().bodyToFlux(Object.class).collectList().block();
        return transactions;
    }

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

    @GetMapping("/full")
    public ResponseEntity<?> findByCode(@RequestParam String code) {
        log.info("Buscar por el codigo");
        Customer customer = customerRepository.findByCode(code);
        log.info("Cleinte encontrado");
        List<CustomerProduct> products = customer.getProducts();
        log.info("productos obtenidos");
        products.forEach(x -> {
            String productName = getProductName(x.getId());
            x.setProductName(productName);
        });
        log.info("productos cargados");

        log.info("iban para transacciones: {}",customer.getIban());
        List<?> transactions = getTransactions(customer.getIban());
        log.info("obtiene lista de transacciones");
        customer.setTransactions(transactions);
        log.info("transacciones cargadas");
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
        Customer customer =  customerRepository.findById(id)
                .map(existCustomer -> {
                    existCustomer.setName(updateCustomer.getName());
                    existCustomer.setPhone(updateCustomer.getPhone());
                    existCustomer.setAddress(updateCustomer.getAddress());
                    existCustomer.setIban(updateCustomer.getIban());
                    existCustomer.setCode(updateCustomer.getCode());
                    existCustomer.setSurname(updateCustomer.getSurname());
                    return existCustomer;
                }).orElse(null);
        if(customer!= null){
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