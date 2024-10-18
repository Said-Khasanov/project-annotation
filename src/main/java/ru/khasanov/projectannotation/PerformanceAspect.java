package ru.khasanov.projectannotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.StringJoiner;

@Aspect
@Component
public class PerformanceAspect {

    @Around("@annotation(PerformanceAnalyze)")
    public Object analyzePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String returnTypeName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        StringJoiner argumentTypes = new StringJoiner(", ", "(", ")");
        StringJoiner argumentsValue = new StringJoiner(", ", "(", ")");
        for (Object arg : joinPoint.getArgs()) {
            argumentTypes.add(arg.getClass().getSimpleName());
            argumentsValue.add(arg.toString());
        }
        LocalTime before = LocalTime.now();
        System.out.printf("{Performance}: executing %s %s%s with arguments %s at %s%n",
                returnTypeName, methodName, argumentTypes, argumentsValue, before);
        Object result = joinPoint.proceed();
        LocalTime after = LocalTime.now();
        long seconds = ChronoUnit.SECONDS.between(before, after);
        System.out.printf("{Performance}: completed %s %s%s with arguments %s at %s,"
                        + " execution took %s seconds%n",
                returnTypeName, methodName, argumentTypes, argumentsValue, after, seconds);
        return result;
    }
}
