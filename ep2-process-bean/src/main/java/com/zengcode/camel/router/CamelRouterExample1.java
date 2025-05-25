package com.zengcode.camel.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelRouterExample1 extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("timer:multi-bean1?period=5000")
                .autoStartup(true)
                .setBody().simple("Payload-${random(1000)}")
                .setHeader("x-id", simple("ID-${random(10000,99999)}"))
                .setProperty("source", constant("ep2-multi"))
                .to("direct:enrich");

        from("direct:enrich")
                .process(exchange -> {
                    String originalBody = exchange.getMessage().getBody(String.class);
                    String id = exchange.getMessage().getHeader("x-id", String.class);
                    String source = exchange.getProperty("source", String.class);

                    System.out.println("🔥 [FirstProcess-style] BODY: " + originalBody);
                    System.out.println("🔥 [FirstProcess-style] HEADER x-id: " + id);
                    System.out.println("🔥 [FirstProcess-style] PROPERTY source: " + source);

                    String updatedBody = "FirstProcess edited  -> " + originalBody;
                    exchange.getMessage().setBody(updatedBody); // ✅ แก้ body ที่แท้จริง
                })
                // Second Processor
                .process(exchange -> {
                    String newBody = exchange.getMessage().getBody(String.class); // ✅ จะได้ body ใหม่แน่นอน
                    String id = exchange.getMessage().getHeader("x-id", String.class);
                    String source = exchange.getProperty("source", String.class);

                    System.out.println("🔎 [SecondProcess-style] BODY: " + newBody);
                    System.out.println("🔎 [SecondProcess-style] HEADER x-id: " + id);
                    System.out.println("🔎 [SecondProcess-style] PROPERTY source: " + source);
                })
                .log("✅ Flow complete: ${body}");
    }
}
