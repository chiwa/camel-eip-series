package com.zengcode.camel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class Customer {
    private String name;
    private int id;

    // ✅ ต้องมี constructor เปล่า และ getter/setter ครบ
    public Customer() {}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
