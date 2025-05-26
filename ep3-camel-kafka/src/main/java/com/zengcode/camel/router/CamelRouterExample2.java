package com.zengcode.camel.router;

import com.zengcode.camel.service.OrderService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CamelRouterExample2 extends RouteBuilder {

    @Autowired
    private OrderService orderService;

    @Override
    public void configure() throws Exception {

        from("timer:kafka-simple2?period=5000")
                .autoStartup(true)
                .to("direct:createOrder");


        from("direct:createOrder")
                .process(exchange -> {
                    String orderId = "ORD-" + System.currentTimeMillis();
                    String message = "New order created at " + System.currentTimeMillis();
                    orderService.sendOrder(orderId, message);
                })
                .log("✅ Order published to Kafka: ${body}");


        from("kafka:order-topic")
                .process(exchange -> {
                    log.info("Consumed Kafka topic : order-topic, message = {}", exchange.getMessage().getBody());
                });
    }
}
