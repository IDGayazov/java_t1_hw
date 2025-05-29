package com.example.task1.aop;

import com.example.task1.dto.ErrorLogDto;
import com.example.task1.service.ErrorLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class ErrorLogging {
    private static final String LOGGING_ANNOTATION =
            "com.example.task1.annotation.LoggingException";

    private final ErrorLogService errorLogService;

    public ErrorLogging(@Qualifier("dataSourceErrorService") ErrorLogService errorLogService){
        this.errorLogService = errorLogService;
    }

    @AfterThrowing(
            pointcut="@within(" + LOGGING_ANNOTATION + ") || @annotation(" + LOGGING_ANNOTATION + ")",
            throwing="ex"
    )
    public void logServiceError(JoinPoint joinPoint, Exception ex){
        try{
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            errorLogService.saveErrorLog(new ErrorLogDto(signature, ex, null));
        }catch(Exception e){
            log.error("Error in saving CRUD error log in DB");
        }
    }

}
