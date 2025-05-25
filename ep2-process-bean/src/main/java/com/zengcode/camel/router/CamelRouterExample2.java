package com.zengcode.camel.router;

import com.zengcode.camel.component.FirstBean;
import com.zengcode.camel.component.SecondBean;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CamelRouterExample2 extends RouteBuilder {

    private final FirstBean firstBean;
    private final SecondBean secondBean;

    public CamelRouterExample2(FirstBean firstBean, SecondBean secondBean) {
        this.firstBean = firstBean;
        this.secondBean = secondBean;
    }


    @Override
    public void configure() throws Exception {

        from("timer:multi-bean?period=5000")
                .autoStartup(true)
                .setBody().simple("Payload-${random(1000)}")
                .setHeader("x-id", simple("ID-${random(10000,99999)}"))
                .setProperty("source", constant("ep2-multi"))
                .to("direct:enrich");

        from("direct:enrich")
                .bean(FirstBean.class, "enrichMessage")
                .bean(SecondBean.class, "inspect")
                .log("✅ Flow complete: ${body}");
    }
}
