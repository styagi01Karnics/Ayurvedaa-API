package com.ayurveda.common.config;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Logs entry and exit of controllers (INFO) and services (DEBUG), with duration.
 * Argument values are deliberately not logged because they can contain passwords and tokens.
 */
@Slf4j
@Aspect
@Component
public class ApiLoggingAspect {

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        return trace(joinPoint, "controller", true);
    }

    @Around("within(@org.springframework.stereotype.Service *)")
    public Object aroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        return trace(joinPoint, "service", false);
    }

    private Object trace(ProceedingJoinPoint joinPoint, String layer, boolean info) throws Throwable {
        String method = methodName(joinPoint);
        logEntry(info, layer, method);

        long start = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            logExit(info, layer, method, elapsedMs(start));
            return result;
        } catch (Throwable ex) {
            log.debug("<- [{}] {} failed in {} ms with {}",
                    layer, method, elapsedMs(start), ex.getClass().getSimpleName());
            throw ex;
        }
    }

    private void logEntry(boolean info, String layer, String method) {
        if (info) {
            log.info("-> [{}] {}", layer, method);
        } else {
            log.debug("-> [{}] {}", layer, method);
        }
    }

    private void logExit(boolean info, String layer, String method, long elapsedMs) {
        if (info) {
            log.info("<- [{}] {} completed in {} ms", layer, method, elapsedMs);
        } else {
            log.debug("<- [{}] {} completed in {} ms", layer, method, elapsedMs);
        }
    }

    private long elapsedMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    private String methodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    }
}
