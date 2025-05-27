package com.example.task1.aop;

import com.example.task1.service.DataSourceErrorLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorLogging {
    private static final String LOGGING_ANNOTATION =
            "com.example.task1.annotation.LoggingException";

    private final DataSourceErrorLogService errorLogService;

    @AfterThrowing(
            pointcut="@within(" + LOGGING_ANNOTATION + ") || @annotation(" + LOGGING_ANNOTATION + ")",
            throwing="ex"
    )
    public void logServiceError(JoinPoint joinPoint, Exception ex){
        try{
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            errorLogService.saveErrorLog(signature, ex);
        }catch(Exception e){
            log.error("Error in saving CRUD error log in DB");
        }
    }

}
