package com.zengcode.camel.component;

import com.zengcode.camel.model.Customer;
import org.apache.camel.Body;
import org.apache.camel.jsonpath.JsonPath;
import org.springframework.stereotype.Component;

@Component
public class JsonProcessorWithCustomerClass {
    public void extractInfo(@Body Customer customer) {
        System.out.println("🧍 Customer name: " + customer.getName());
        System.out.println("🆔 Customer ID: " + customer.getId());
    }
}
