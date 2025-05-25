package com.zengcode.camel.component;

import org.apache.camel.Body;
import org.apache.camel.ExchangeProperty;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;

@Component
public class FirstBean {

    public String enrichMessage(
            @Body String body,
            @Header("x-id") String id,
            @ExchangeProperty("source") String source
    ) {
        System.out.println("🔥 [FirstBean] BODY: " + body);
        System.out.println("🔥 [FirstBean] HEADER x-id: " + id);
        System.out.println("🔥 [FirstBean] PROPERTY source: " + source);

        return "Edited by FirstBean --> " + body;
    }
}
