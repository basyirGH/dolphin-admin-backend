package com.dolphin.adminbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.dolphin.adminbackend.model.Order;
import com.dolphin.adminbackend.model.request.OrderReq;
import com.dolphin.adminbackend.service.OrderService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    /*
     * If we get a very deep nested object of the "orders" field array,
     * it's because because of bidirectional relationships in JPA entities.
     * Specifically, the Order entity references Customer, and Customer likely
     * references Order again, leading to a circular reference when serializing the
     * response.
     * 
     * Why It Happens
     * Order entity has a Customer field.
     * Customer entity likely has a collection of Order objects (List<Order> or
     * similar).
     * When serializing the response, the JSON serializer (e.g., Jackson) attempts
     * to serialize the entire Order, including its Customer. Then it tries to
     * serialize the Customer, including its Orders, and so on, resulting in deeply
     * nested objects or a stack overflow.
     * Solution: Jackson provides @JsonManagedReference and @JsonBackReference
     * annotations to handle circular references.
     */
    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody OrderReq orderReq) {
        try {
            Order createdOrder = orderService.createOrder(orderReq);
            return ResponseEntity
                    .status(HttpStatus.CREATED) // HTTP 201
                    .body(createdOrder); // Include the created order in the response body
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }

    }

}
