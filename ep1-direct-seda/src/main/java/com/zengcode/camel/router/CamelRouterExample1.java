package com.zengcode.camel.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelRouterExample1 extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("timer:multi-bean?period=3000")
                .setBody().simple("Payload-${random(1000)}")
                .setHeader("x-id", simple("ID-${random(10000,99999)}"))
                .setProperty("source", constant("ep2-multi"))
                .to("direct:enrich");

        from("direct:enrich")
                .process(exchange -> {
                    String body = exchange.getMessage().getBody(String.class);
                    String id = exchange.getMessage().getHeader("x-id", String.class);
                    String source = exchange.getProperty("source", String.class);

                    System.out.println("🔥 [FirstBean-style] BODY: " + body);
                    System.out.println("🔥 [FirstBean-style] HEADER x-id: " + id);
                    System.out.println("🔥 [FirstBean-style] PROPERTY source: " + source);
                })
                .process(exchange -> {
                    String body = exchange.getMessage().getBody(String.class);
                    String id = exchange.getMessage().getHeader("x-id", String.class);
                    String source = exchange.getProperty("source", String.class);

                    System.out.println("🔎 [SecondBean-style] BODY: " + body);
                    System.out.println("🔎 [SecondBean-style] HEADER x-id: " + id);
                    System.out.println("🔎 [SecondBean-style] PROPERTY source: " + source);
                })
                .log("✅ Flow complete: ${body}");

    }
}
