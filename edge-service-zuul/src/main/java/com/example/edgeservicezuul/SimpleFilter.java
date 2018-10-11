package com.example.edgeservicezuul;

import brave.Tracer;
import brave.Tracing;
import brave.propagation.*;
import brave.spring.web.TracingClientHttpRequestInterceptor;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.HttpURLConnection;

@Component
public class SimpleFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(SimpleFilter.class);

    @Autowired
    private Tracer tracer;

    @Autowired
    private Tracing tracing;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        // http :8080/customers 'TracerId:123123'
        String traceId = request.getHeader("TracerId");
        System.out.println("Custom traceId:" + traceId);

        // configure a function that injects a trace context into a request

//        tracing.propagation().injector(new Propagation.Setter<HttpHeaders, String>() {
//            @Override
//            public void put(HttpHeaders carrier, String key, String value) {
//                System.out.println("---- I was here ----");
//            }
//        });


        TraceContext.Injector<HttpHeaders> injector = tracing.propagation().injector(HttpHeaders::set);

//        injector.inject(tracing.currentTraceContext().get(),  );


        // see comment in TraceContextOrSamplingFlags

        System.out.println(tracer.currentSpan());

        // Headers Sent to the downstream service, here we would like to
        // change the X-B3-TraceId and X-B3-ParentSpanId to the one provided by Nevis
        // if none is provided then generate one

        // X-B3-TraceId
        // X-B3-SpanId
        // X-B3-ParentSpanId
        // X-B3-Sampled


        log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

        return null;
    }

}