package com.zengcode.camel.router;

import com.zengcode.camel.component.FirstBean;
import com.zengcode.camel.component.JsonProcessor;
import com.zengcode.camel.component.SecondBean;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelRouterExample3 extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("timer:multi-bean?period=3000")
                .autoStartup(false)
                .setBody(constant("""
                          {
                            "customer": {
                              "name": "Pee",
                              "id": 12345
                            }
                          }
                        """))
                .bean(JsonProcessor.class, "extractInfo")
                .log("✅ Done JSON route");
    }
}
