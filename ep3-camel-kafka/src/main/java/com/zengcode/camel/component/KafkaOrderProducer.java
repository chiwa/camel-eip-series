package com.zengcode.camel.component;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.kafka.KafkaConstants;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderProducer {
    private final ProducerTemplate producerTemplate;
    public KafkaOrderProducer(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public void send(String topic, Object message) {
        producerTemplate.sendBody(topic, message);
    }

    /**
     * Partitioning the message
     * @param topic
     * @param key
     * @param message
     */
    public void sendWithKey(String topic, String key, Object message) {
        producerTemplate.sendBodyAndHeader(
                topic,
                message,
                KafkaConstants.KEY,
                key
        );
    }
}
