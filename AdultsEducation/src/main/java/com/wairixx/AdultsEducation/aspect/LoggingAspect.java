package com.wairixx.AdultsEducation.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(com.wairixx.AdultsEducation.aspect.Loggable) " +
            "|| @within(com.wairixx.AdultsEducation.aspect.Loggable)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Loggable annotation = resolveAnnotation(signature);

        String fullName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

        if (annotation.logArgs()) {
            log.info("→ {} called with args: {}", fullName, Arrays.toString(joinPoint.getArgs()));
        } else {
            log.info("→ {} called", fullName);
        }

        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            log.error("✗ {} failed after {} ms: {}", fullName,
                    System.currentTimeMillis() - startTime, ex.getMessage());
            throw ex;
        }

        long timeTaken = System.currentTimeMillis() - startTime;
        if (annotation.logResult()) {
            log.info("← {} completed in {} ms, result: {}", fullName, timeTaken, result);
        } else {
            log.info("← {} completed in {} ms", fullName, timeTaken);
        }
        return result;
    }

    private Loggable resolveAnnotation(MethodSignature signature) {
        Loggable m = signature.getMethod().getAnnotation(Loggable.class);
        return m != null ? m : (Loggable) signature.getDeclaringType().getAnnotation(Loggable.class);
    }
}