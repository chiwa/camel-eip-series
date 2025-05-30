package com.zengcode.camel.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CamelRouterExample1 extends RouteBuilder {

    private final AtomicInteger counter = new AtomicInteger(1);


    @Override
    public void configure() throws Exception {

        AtomicInteger counter = new AtomicInteger(1);

        from("timer:one-shot?period=5000&delay=10000&repeatCount=10")
                .autoStartup(false)
                .process(exchange -> {
                    exchange.getMessage().setBody(counter.getAndIncrement());
                })
                .to("kafka:ep4-example1-topic")
                .log("✅ ส่งสำเร็จ: ${body}");

        from("kafka:ep4-example1-topic?groupId=ep4-example1-group&autoOffsetReset=latest")
                .log("Message is incoming body = ${body}")
                .process(exchange -> {
                    int messageNumber = exchange.getMessage().getBody(Integer.class);
                    log.info("Message numeber {}", messageNumber);
                    if (messageNumber % 2 != 0) {
                        log.info("Do something with message : {}", exchange.getMessage().getBody() + ", but execption");
                        throw new RuntimeException("Mock exception!!!");
                    }
                })
                .log("✅ Auto Commit Received ${body}");

        from("timer:kafka-simple?period=5000")
                .log("I am still alive!!");
    }
}
