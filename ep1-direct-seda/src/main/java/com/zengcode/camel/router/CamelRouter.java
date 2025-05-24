package com.zengcode.camel.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("timer:direct-demo?period=2000")
                .autoStartup(false)
                .setBody().simple("Direct message at ${date:now:HH:mm:ss}")
                .to("direct:logDirect");

        from("direct:logDirect")
                .log("[DIRECT] - ${threadName}] >> ${body}");

        from("timer:seda-demo?period=2000")
                .autoStartup(true)
                .setBody().simple("Seda message at ${date:now:HH:mm:ss}")
                .to("seda:logSeda")
                .log("[TIMER] Sent to seda: ${body}");

        from("seda:logSeda?concurrentConsumers=3")
                .log("[SEDA - ${threadName}] >> ${body}");

    }
}
