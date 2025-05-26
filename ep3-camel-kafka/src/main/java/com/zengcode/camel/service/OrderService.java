package com.zengcode.camel.service;

import com.zengcode.camel.component.KafkaOrderProducer;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final String ORDER_TOPIC = "kafka:order-topic";
    private final KafkaOrderProducer kafkaOrderProducer;

    public OrderService(KafkaOrderProducer kafkaOrderProducer) {
        this.kafkaOrderProducer = kafkaOrderProducer;
    }

    public void sendOrder(String orderId, Object message) {
        kafkaOrderProducer.sendWithKey(
                ORDER_TOPIC,
                orderId,
                message
        );
    }
}
