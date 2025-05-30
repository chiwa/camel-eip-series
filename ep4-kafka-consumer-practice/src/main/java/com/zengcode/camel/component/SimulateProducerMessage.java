package com.zengcode.camel.component;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.kafka.KafkaConstants;
import org.springframework.stereotype.Component;

@Component
public class SimulateProducerMessage {
    private final ProducerTemplate producerTemplate;
    public SimulateProducerMessage(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public void example1() {
        String body = "Example 1 message.";
        producerTemplate.sendBody("kafka:ep4-topic", body);
    }

}
