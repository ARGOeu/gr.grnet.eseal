package gr.grnet.eseal.aop;

import gr.grnet.eseal.exception.APIError;
import gr.grnet.eseal.utils.RequestLogField;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.f;


/**
 * Around advice can perform custom behavior before and after the method invocation.
 * The {@link #log} Around counts the incoming request processing time and logs info about it.
 * This aspect component is triggered for each method under the RestController annotation.
 */
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class AspectComponent {
    @Autowired
    private HttpServletRequest request;


    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {
    }


    @Around("restController()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {


        long start = System.currentTimeMillis();

        request.setAttribute("start_time", start);

        MDC.put("request_id", String.valueOf(UUID.randomUUID()));

        Object object = joinPoint.proceed();

        RequestLogField field = RequestLogField
                .builder()
                .path(request.getServletPath())
                .method(request.getMethod())
                .processing_time(getProcessingTime(start))
                .status(HttpStatus.OK.toString())
                .type("request_log")
                .build();


        getLogger(joinPoint).info("Request completed", f(field));

        MDC.remove("request_id");

        return object;

    }

    @Pointcut("within(@org.springframework.web.bind.annotation.ControllerAdvice *)")
    public void beanAnnotatedWithControllerAdvice() {}

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {}

    @Pointcut("publicMethod() && beanAnnotatedWithControllerAdvice()")
    public void publicMethodInsideAClassMarkedWithAtBeanAnnotatedWithControllerAdvice() {

    }


    @AfterReturning(pointcut = "publicMethodInsideAClassMarkedWithAtBeanAnnotatedWithControllerAdvice()", returning="response")
    public void logAfterReturningException(JoinPoint joinPoint, Object response) throws Throwable
    {

        if(Objects.isNull(MDC.get("request_id"))){
            MDC.put("request_id", String.valueOf(UUID.randomUUID()));
        }

        ResponseEntity<APIError> error = (ResponseEntity<APIError>) response;

        RequestLogField field = RequestLogField
                .builder()
                .path(request.getServletPath())
                .method(request.getMethod())
                .processing_time(getProcessingTime(Optional.ofNullable((Long) request.getAttribute("start_time")).orElse(System.currentTimeMillis())))
                .status(error.getStatusCode().toString())
                .type("request_log")
                .build();

        getLogger(joinPoint).error(Optional.ofNullable(error.getBody().getApiErrorBody().getMessage()).orElse("Internal server error"), f(field));

        MDC.remove("request_id");
    }

    private String getProcessingTime(long value) {
        long currentTime = System.currentTimeMillis();
        long age = currentTime - value;
        String ageString = DurationFormatUtils.formatDuration(age, "d") + "d";
        if ("0d".equals(ageString)) {
            ageString = DurationFormatUtils.formatDuration(age, "H") + "h";
            if ("0h".equals(ageString)) {
                ageString = DurationFormatUtils.formatDuration(age, "m") + "m";
                if ("0m".equals(ageString)) {
                    ageString = DurationFormatUtils.formatDuration(age, "s") + "s";
                    if ("0s".equals(ageString)) {
                        ageString = age + "ms";
                    }
                }
            }
        }
        return ageString;
    }

    private Logger getLogger(JoinPoint jp){

        Class clazz = jp.getTarget().getClass();
        return  LoggerFactory.getLogger(clazz);
    }


}

