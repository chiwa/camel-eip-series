package com.zengcode.camel.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:hello?period=3000")
                .autoStartup(false)
                .setBody(constant("Hello Camel!"))
                .log("Received message: ${body}");

        from("timer:transform?period=3000")
                .autoStartup(false)
                .setBody(constant("Camel Rocks"))
                .transform().simple("Transformed: ${body}")
                .log(" ${body}");

        from("timer:gen?period=5000")
                .autoStartup(false)
                .setBody().simple("Generated ID: ${uuid}")
                //.log("id ${body}");
                        .to("direct:demo");

        from("direct:demo")
                .process(exchange -> {
                    String input = exchange.getMessage().getBody(String.class);
                    exchange.getMessage().setBody("Processed: " + input);
                })
                .log(" ${body}");


        from("timer:source?period=1000")
                .setBody().simple("Message at ${date:now:HH:mm:ss}")
                .to("seda:async-log")
                .log(" Sent to seda: ${body}");

        from("seda:async-log?concurrentConsumers=3")
                .log(" [${threadName}] >> Processing: ${body}");
    }
}
