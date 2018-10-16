Networking in Compose:

https://docs.docker.com/compose/networking/

```
edge-service -> customer-client -> customer-service
```

http://localhost:8080/actuator/filters
http://localhost:8080/actuator/routes

https://reflectoring.io/tracing-with-spring-cloud-sleuth/



`TraceWebClientAutoConfiguration` creates the `TracingClientHttpRequestInterceptor` interceptor

****

```
TracingFilter this is where the TracingContext is created:
```

Span span = handler.handleReceive(extractor, httpRequest);

code in `Tracer`:

```bash
TraceContext.Builder newContextBuilder
```


`TraceAutoConfiguration` creates `Propagation.Factory`

More info:

https://stackoverflow.com/questions/49581760/how-to-log-mdc-with-spring-sleuth-2-0
https://github.com/spring-cloud/spring-cloud-sleuth/issues/1106