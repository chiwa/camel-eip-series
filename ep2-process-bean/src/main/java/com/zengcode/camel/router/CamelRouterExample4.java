package com.zengcode.camel.router;

import com.zengcode.camel.component.JsonProcessorWithCustomerClass;
import com.zengcode.camel.model.Customer;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.stereotype.Component;

@Component
public class CamelRouterExample4 extends RouteBuilder {

    private final JsonProcessorWithCustomerClass jsonProcessorWithCustomerClass;

    public CamelRouterExample4(JsonProcessorWithCustomerClass jsonProcessorWithCustomerClass) {
        this.jsonProcessorWithCustomerClass = jsonProcessorWithCustomerClass;
    }

    @Override
    public void configure() throws Exception {

        DataFormat jsonFormat = new JacksonDataFormat(Customer.class);
        from("timer:json-full-2?period=3000")
                .autoStartup(false)
                .setBody(constant("""
                            {
                              "name": "Pee",
                              "id": 12345
                            }
                        """))
                .unmarshal(jsonFormat)
                .bean(jsonProcessorWithCustomerClass, "extractInfo")
                .log("✅ Done full object route");
    }
}
