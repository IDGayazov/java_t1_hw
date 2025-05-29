package com.example.task1.aop;

import com.example.task1.dto.ErrorLogDto;
import com.example.task1.service.ErrorLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TimeMetricLogging {
    private static final String METRIC_ANNOTATION =
            "com.example.task1.annotation.Metric";

    @Value("${app.time.limit-ms}")
    private String timeInMillis;

    private final ErrorLogService errorLogService;

    public TimeMetricLogging(@Qualifier("timeLimitExceedErrorService") ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    @Around("@annotation(" + METRIC_ANNOTATION + ")")
    public Object timeMeasuringAndErrorLogging(ProceedingJoinPoint joinPoint) throws Throwable{
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        if (duration > Long.parseLong(timeInMillis)) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            log.warn("Method: ({}) exceed time limit with time: {}ms", signature, duration);
            errorLogService.saveErrorLog(new ErrorLogDto(signature, null, duration));
        }

        return result;
    }
}
