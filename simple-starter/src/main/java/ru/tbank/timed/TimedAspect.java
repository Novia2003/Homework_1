package ru.tbank.timed;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@Slf4j
public class TimedAspect {

    @Around("@annotation(ru.tbank.timed.Timed) || @within(ru.tbank.timed.Timed)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();

        Object proceed = joinPoint.proceed();

        long executionTime = System.nanoTime() - start;
        log.info("{} executed in {} nanoseconds", joinPoint.getSignature(), executionTime);
        return proceed;
    }
}
