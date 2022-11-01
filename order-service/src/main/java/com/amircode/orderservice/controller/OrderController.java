package com.amircode.orderservice.controller;

import com.amircode.orderservice.dto.OrderRequest;
import com.amircode.orderservice.model.Order;
import com.amircode.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory",fallbackMethod = "test")
    public String placeOrder(@RequestBody OrderRequest request){
        orderService.placeOrder(request);
        return "successful created!";
    }

    public String test(OrderRequest orderRequest, RuntimeException runtimeException){
        return "Oops! something went wrong. plz try later!";
    }
}
