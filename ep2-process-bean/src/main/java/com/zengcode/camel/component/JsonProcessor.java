package com.zengcode.camel.component;

import org.apache.camel.jsonpath.JsonPath;
import org.springframework.stereotype.Component;

@Component
public class JsonProcessor {
    public void extractInfo(@JsonPath("$.customer.name") String name) {
        System.out.println("📦 Customer name: " + name);
    }
}
