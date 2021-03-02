package gr.grnet.eseal.aop;

import gr.grnet.eseal.exception.APIException;
import gr.grnet.eseal.utils.RequestLogField;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
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

        Class clazz = joinPoint.getTarget().getClass();
        Logger logger =  LoggerFactory.getLogger(clazz);
        long start = System.currentTimeMillis();

        MDC.put("request_id", String.valueOf(UUID.randomUUID()));

        String message = "Request completed";
        String status = HttpStatus.OK.toString();

        try {

            Object object = joinPoint.proceed();
            return object;
        } catch (APIException ex) {

            message = ex.getMessage();
            status = ex.getStatus().toString();
            throw ex;

        } finally {

            RequestLogField field = RequestLogField
                    .builder()
                    .path(request.getServletPath())
                    .method(request.getMethod())
                    .processing_time(getAge(start))
                    .status(status)
                    .type("request_log")
                    .build();

            logger.info(message, f(field));

            MDC.remove("request_id");
        }

    }

    private String getAge(long value) {
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


}
