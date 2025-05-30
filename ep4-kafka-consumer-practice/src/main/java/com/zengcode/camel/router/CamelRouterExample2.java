package com.zengcode.camel.router;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.consumer.KafkaManualCommit;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CamelRouterExample2 extends RouteBuilder {

    private final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public void configure() throws Exception {

        from("timer:manual-producer?period=5000&repeatCount=5&delay=10000")
                .autoStartup(false)
                .process(exchange -> {
                    int val = counter.getAndIncrement();
                    exchange.getMessage().setBody(val);
                })
                .to("kafka:ep4-example2-topic")
                .log("✅ [Producer] ส่งสำเร็จ: ${body}");

        from("kafka:ep4-example2-topic"
                + "?groupId=ep4-example2-group"
                + "&autoOffsetReset=latest"
                + "&autoCommitEnable=false"
                + "&allowManualCommit=true"
                + "&sessionTimeoutMs=10000"
                + "&heartbeatIntervalMs=3000")
                .routeId("manual-commit-consumer")
                .log("📥 [Consumer] รับข้อความ: ${body}")
                .process(exchange -> {
                    int messageNumber = exchange.getMessage().getBody(Integer.class);
                    log.info("🎯 กำลังประมวลผล message: {}", messageNumber);

                    if (messageNumber % 2 == 0) {
                       log.warn("❌ พังกับเลขคี่: {}", messageNumber);
                        System.exit(1);
                    }

                    KafkaManualCommit manual = exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
                    if (manual != null) {
                        manual.commit();
                        log.info("✅ Manual Commit success for: {}", messageNumber);
                    }
                })
                .log("✅ [Consumer] ประมวลผลเสร็จ: ${body}");

        from("timer:kafka-simple?period=5000")
                .log("I am still alive!!");
    }
}