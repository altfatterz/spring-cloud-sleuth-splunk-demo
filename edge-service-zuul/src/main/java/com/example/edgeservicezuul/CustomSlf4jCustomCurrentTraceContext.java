package com.example.edgeservicezuul;

import brave.internal.HexCodec;
import brave.internal.Nullable;
import brave.propagation.CurrentTraceContext;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.log.Slf4jCurrentTraceContext;

public class CustomSlf4jCustomCurrentTraceContext extends CurrentTraceContext {

    // Backward compatibility for all logging patterns
    private static final String LEGACY_EXPORTABLE_NAME = "X-Span-Export";
    private static final String LEGACY_PARENT_ID_NAME = "X-B3-ParentSpanId";
    private static final String LEGACY_TRACE_ID_NAME = "X-B3-TraceId";
    private static final String LEGACY_SPAN_ID_NAME = "X-B3-SpanId";

    private static final Logger log = LoggerFactory
            .getLogger(Slf4jCurrentTraceContext.class);

    public static CustomSlf4jCustomCurrentTraceContext create() {
        return create(CurrentTraceContext.Default.inheritable());
    }

    public static CustomSlf4jCustomCurrentTraceContext create(CurrentTraceContext delegate) {
        return new CustomSlf4jCustomCurrentTraceContext(delegate);
    }

    final CurrentTraceContext delegate;

    CustomSlf4jCustomCurrentTraceContext(CurrentTraceContext delegate) {
        if (delegate == null)
            throw new NullPointerException("delegate == null");
        this.delegate = delegate;
    }

    @Override public TraceContext get() {
        return this.delegate.get();
    }

    @Override public Scope newScope(@Nullable TraceContext currentSpan) {
        final String previousTraceId = MDC.get("traceId");
        final String previousParentId = MDC.get("parentId");
        final String previousSpanId = MDC.get("spanId");
        final String spanExportable = MDC.get("spanExportable");
        final String legacyPreviousTraceId = MDC.get(LEGACY_TRACE_ID_NAME);
        final String legacyPreviousParentId = MDC.get(LEGACY_PARENT_ID_NAME);
        final String legacyPreviousSpanId = MDC.get(LEGACY_SPAN_ID_NAME);
        final String legacySpanExportable = MDC.get(LEGACY_EXPORTABLE_NAME);

        if (currentSpan != null) {
            String traceIdString = currentSpan.traceIdString();

            MDC.put("trId", ExtraFieldPropagation.get(currentSpan, "trId"));
            MDC.put("traceId", traceIdString);
            MDC.put(LEGACY_TRACE_ID_NAME, traceIdString);
            String parentId = currentSpan.parentId() != null ?
                    HexCodec.toLowerHex(currentSpan.parentId()) :
                    null;
            replace("parentId", parentId);
            replace(LEGACY_PARENT_ID_NAME, parentId);
            String spanId = HexCodec.toLowerHex(currentSpan.spanId());
            MDC.put("spanId", spanId);
            MDC.put(LEGACY_SPAN_ID_NAME, spanId);
            String sampled = String.valueOf(currentSpan.sampled());
            MDC.put("spanExportable", sampled);
            MDC.put(LEGACY_EXPORTABLE_NAME, sampled);
            log("Starting scope for span: {}", currentSpan);
            if (currentSpan.parentId() != null) {
                if (log.isTraceEnabled()) {
                    log.trace("With parent: {}", currentSpan.parentId());
                }
            }
        }
        else {
            MDC.remove("traceId");
            MDC.remove("parentId");
            MDC.remove("spanId");
            MDC.remove("spanExportable");
            MDC.remove(LEGACY_TRACE_ID_NAME);
            MDC.remove(LEGACY_PARENT_ID_NAME);
            MDC.remove(LEGACY_SPAN_ID_NAME);
            MDC.remove(LEGACY_EXPORTABLE_NAME);
        }

        Scope scope = this.delegate.newScope(currentSpan);

        class ThreadContextCurrentTraceContextScope implements Scope {
            @Override public void close() {
                log("Closing scope for span: {}", currentSpan);
                scope.close();
                replace("traceId", previousTraceId);
                replace("parentId", previousParentId);
                replace("spanId", previousSpanId);
                replace("spanExportable", spanExportable);
                replace(LEGACY_TRACE_ID_NAME, legacyPreviousTraceId);
                replace(LEGACY_PARENT_ID_NAME, legacyPreviousParentId);
                replace(LEGACY_SPAN_ID_NAME, legacyPreviousSpanId);
                replace(LEGACY_EXPORTABLE_NAME, legacySpanExportable);
            }
        }
        return new ThreadContextCurrentTraceContextScope();
    }

    private void log(String text, TraceContext span) {
        if (span == null) {
            return;
        }
        if (log.isTraceEnabled()) {
            log.trace(text, span);
        }
    }

    static void replace(String key, @Nullable String value) {
        if (value != null) {
            MDC.put(key, value);
        }
        else {
            MDC.remove(key);
        }
    }
}
