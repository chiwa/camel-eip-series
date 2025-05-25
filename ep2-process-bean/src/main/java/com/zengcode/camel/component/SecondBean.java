package com.zengcode.camel.component;

import org.apache.camel.Body;
import org.apache.camel.ExchangeProperty;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;

@Component
public class SecondBean {
    public void inspect(
            @Body String body,
            @Header("x-id") String id,
            @ExchangeProperty("source") String source
    ) {
        System.out.println("🔎 [SecondBean] BODY: " + body);
        System.out.println("🔎 [SecondBean] HEADER x-id: " + id);
        System.out.println("🔎 [SecondBean] PROPERTY source: " + source);
    }
}
