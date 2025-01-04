package com.dolphin.adminbackend.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dolphin.adminbackend.constant.OrderStatus;
import com.dolphin.adminbackend.model.dto.request.OrderReq;
import com.dolphin.adminbackend.model.jpa.Customer;
import com.dolphin.adminbackend.model.jpa.Order;
import com.dolphin.adminbackend.model.jpa.OrderItem;
import com.dolphin.adminbackend.model.jpa.Payment;
import com.dolphin.adminbackend.model.jpa.Product;
import com.dolphin.adminbackend.repository.CustomerRepo;
import com.dolphin.adminbackend.repository.OrderRepo;
import com.dolphin.adminbackend.repository.PaymentRepo;
import com.dolphin.adminbackend.repository.ProductRepo;
import com.dolphin.adminbackend.socketio.WebSocketController;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private CustomerRepo custRepo;

    @Autowired
    private ProductRepo prodRepo;

    @Autowired
    private WebSocketController webSocketController;

    // @Autowired
    // private OrderEventPublisher orderEventPublisher;

    /**
     * Create a new order.
     *
     * @param order the order to create
     * @return the created order
     * @throws JsonProcessingException
     */
    public Order createOrder(OrderReq orderRequest) {

        Customer customer = null;
        Long customerId = orderRequest.getCustomerId();
        Optional<Customer> optionalCustomer = custRepo.findById(customerId);
        if (optionalCustomer.isPresent()) {
            customer = optionalCustomer.get();
        } else {
            throw new EntityNotFoundException("customerId " + customerId + " not found");
        }

        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(itemRequest -> {
                    Product product = null;
                    Optional<Product> optProduct = prodRepo.findById(itemRequest.getProductId());
                    if (optProduct.isPresent()) {
                        // At this point, product is not initialized yet (lazy loading), so it will
                        // appear as an empty object
                        product = optProduct.get();

                    } else {
                        throw new EntityNotFoundException("productId " + itemRequest.getProductId() + " not found");
                    }
                    return new OrderItem(
                            null, // ID is auto-generated
                            product,
                            itemRequest.getQuantity(),
                            product.getPrice(), // product is initialized by accessing any property of it
                            null // Order will be set later
                    );
                }).collect(Collectors.toList());

        Double totalAmount = orderItems.stream()
                .mapToDouble(item -> item.getPricePerUnit() * item.getQuantity())
                .sum();

        Order order = new Order();
        order.setCustomer(customer);
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(new Date());

        // Set the order reference for all items
        orderItems.forEach(item -> item.setOrder(order));

        Order savedOrder = orderRepo.save(order);
        //orderEventPublisher.publishOrderCreatedEvent("new order created");
        webSocketController.handleNewOrder(orderRepo.count());
        log.info("Saved order ID: {}", savedOrder.getId());
        return savedOrder;
    }

    /**
     * Retrieve an order by its ID.
     *
     * @param orderId the ID of the order
     * @return the order, if found
     */
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepo.findById(orderId);
    }

    /**
     * Retrieve all orders.
     *
     * @return a list of all orders
     */
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    /**
     * Update an existing order.
     *
     * @param orderId      the ID of the order to update
     * @param updatedOrder the new order details
     * @return the updated order
     */
    public Order updateOrder(Long orderId, Order updatedOrder) {
        return orderRepo.findById(orderId).map(order -> {
            // order.setCustomerName(updatedOrder.getCustomerName());
            order.setTotalAmount(updatedOrder.getTotalAmount());
            order.setPayments(updatedOrder.getPayments());
            return orderRepo.save(order);
        }).orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    /**
     * Delete an order by its ID.
     *
     * @param orderId the ID of the order to delete
     */
    public void deleteOrder(Long orderId) {
        orderRepo.deleteById(orderId);
    }

    /**
     * Add a payment to an order.
     *
     * @param orderId the ID of the order
     * @param payment the payment to add
     * @return the updated order
     */
    public Order addPaymentToOrder(Long orderId, Payment payment) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        payment.setOrder(order); // Associate payment with the order
        paymentRepo.save(payment); // Save the payment

        order.getPayments().add(payment); // Add payment to order's payment list
        return orderRepo.save(order);
    }

    /**
     * Get payments for a specific order.
     *
     * @param orderId the ID of the order
     * @return a list of payments associated with the order
     */
    public List<Payment> getPaymentsForOrder(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        return order.getPayments();
    }
}
