package com.ds.orderservice.service;

import com.ds.orderservice.common.Payment;
import com.ds.orderservice.common.TransactionRequest;
import com.ds.orderservice.common.TransactionResponse;
import com.ds.orderservice.entity.Order;
import com.ds.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RefreshScope
public class OrderService {

    Logger logger = LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private OrderRepository repository;
    @Autowired
   @Lazy
    private RestTemplate template;

    @Value("${microServices.paymentService.endPoints.endPoint1.url}")
    private String PAYMENT_SERVICE_ENDPOINT_URL_01;

    public TransactionResponse saveOrder(TransactionRequest request) {
        String response = "";
        Order order = request.getOrder();
        Payment payment = request.getPayment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice());
        // rest call
        Payment paymentResponse = template.postForObject(PAYMENT_SERVICE_ENDPOINT_URL_01, payment, Payment.class);
//
        response = paymentResponse.getPaymentStatus().equals("success") ? "payment processing successful and order placed" : "there is a failure in payment api , order added to cart";
        repository.save(order);
        return new TransactionResponse(order, paymentResponse.getAmount(), paymentResponse.getTransactionId(), response);
    }
}