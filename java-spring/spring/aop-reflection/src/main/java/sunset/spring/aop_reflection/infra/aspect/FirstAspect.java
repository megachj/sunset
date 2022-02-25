package sunset.spring.aop_reflection.infra.aspect;

import sunset.spring.aop_reflection.infra.annotation.MyMethod;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Aspect @Order(value = 1)
@Component
public class FirstAspect {

    @Before("@annotation(myMethodAnnotation)")
    public void loggingParameter(JoinPoint jp, MyMethod myMethodAnnotation) {
        MethodInvocationProceedingJoinPoint invocationJp = (MethodInvocationProceedingJoinPoint) jp;

        List<Class<?>> parameterTypes = Arrays.asList(((MethodSignature)jp.getSignature()).getParameterTypes());
    }
}
