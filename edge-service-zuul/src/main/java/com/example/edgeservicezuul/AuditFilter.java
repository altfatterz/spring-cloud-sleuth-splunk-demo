package com.example.edgeservicezuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuditFilter extends ZuulFilter {

    private static Logger logger = LoggerFactory.getLogger(AuditFilter.class);
    private static Logger audit = LoggerFactory.getLogger("audit-logger");

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

        audit.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
        logger.info("AuditFilter was called...");

        return null;
    }

}