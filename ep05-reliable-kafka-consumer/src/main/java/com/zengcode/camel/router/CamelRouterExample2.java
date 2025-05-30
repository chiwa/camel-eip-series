package com.zengcode.camel.router;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
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

        onException(Exception.class)
                .maximumRedeliveries(3)
                .redeliveryDelay(1000)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .handled(true)
                .log("❌ Retry ครบแล้ว ส่ง DLQ")
                .log("🧠 [Consumer] 💥 Retry failed → ส่งเข้า DLQ: ${body}")
                .to("kafka:ep5-dlq-topic")
                .process(exchange -> {
                    KafkaManualCommit manual = exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
                    if (manual != null) {
                        manual.commit();
                        log.info("🧠 [Consumer] 🧹 Manual Commit หลังส่ง DLQ");
                    }
                });

        from("kafka:ep5-reliable-topic"
                + "?groupId=ep5-reliable-group"
                + "&autoOffsetReset=earliest"
                + "&autoCommitEnable=false"
                + "&allowManualCommit=true")
                .routeId("reliable-consumer")
                .process(exchange -> {
                    int value = exchange.getMessage().getBody(Integer.class);
                    log.info("🧠 [Consumer] 📥 รับข้อความ: {}", value);
                    if (value % 2 != 0) {
                        log.warn("🧠 [Consumer] ❌ Mock failure (odd number): {}", value);
                        throw new RuntimeException("Failing odd number");
                    }
                    log.info("🧠 [Consumer] ✅ ดำเนินการสำเร็จ: {}", value);
                })
                .process(exchange -> {
                    KafkaManualCommit manual = exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
                    if (manual != null) {
                        manual.commit();
                        log.info("🧠 [Consumer] 🧹 Manual Commit success");
                    }
                })
                .log("🧠 [Consumer] 🔚 จบการทำงานของ: ${body}");

        from("kafka:ep5-dlq-topic"
                + "?groupId=dlq-retry"
                + "&autoOffsetReset=earliest"
                + "&autoCommitEnable=false"
                + "&allowManualCommit=true")
                .routeId("dlq-retry-route")
                .autoStartup(true)
                .delay(2000)
                .process(exchange -> {
                    Integer oldVal = exchange.getMessage().getBody(Integer.class);
                    Integer newVal = oldVal + 1;
                    exchange.getMessage().setBody(newVal);
                    log.info("🔁 [DLQ Retry] 📥 รับจาก DLQ: {}", oldVal);
                    log.info("🔁 [DLQ Retry] 🔁 แก้ไขข้อมูล: {} → {}", oldVal, newVal);
                })
                .to("kafka:ep5-reliable-topic")
                .log("🔁 [DLQ Retry] 🚀 ส่งกลับไป Kafka สำเร็จ: ${body}")
                .process(exchange -> {
                    KafkaManualCommit manual = exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
                    if (manual != null) {
                        manual.commit();
                        log.info("🔁 [DLQ Retry] 🧹 Manual Commit DLQ Retry success");
                    }
                });

        from("timer:manual-producer?period=5000&repeatCount=2&delay=10000")
                .autoStartup(true)
                .process(exchange -> {
                    int val = counter.getAndIncrement();
                    exchange.getMessage().setBody(val, Integer.class);
                    log.info("🧑‍🏭 [Producer] ✅ ส่งข้อความ: {}", val);
                })
                .to("kafka:ep5-reliable-topic");
    }
}

