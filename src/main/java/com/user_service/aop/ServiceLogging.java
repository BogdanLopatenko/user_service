package com.user_service.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceLogging {

    @Pointcut("execution(public * com.user_service.service..*.*(..))")
    public void serviceLayer(){}

    @Around("serviceLayer()")
    public Object LogServiceLayer(ProceedingJoinPoint joinPoint) throws Throwable {

        String signature = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();

        log.info("AOP: method entrance: {}", signature);

        Throwable caughtException = null;

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            caughtException = e;
            throw e;
        } finally {

            long duration = System.currentTimeMillis() - startTime;

            if (caughtException == null) {
                log.info("AOP: method exit (OK): {}. Executed in {} ms.", signature, duration);
            } else {
                log.warn("AOP: method exit (FAILED): {}. Executed in {} ms.", signature, duration);
            }
        }
    }
}
