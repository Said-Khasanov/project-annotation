package ru.khasanov.projectannotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

@Component
public class PerformanceAnalyzeBeanPostProcessor implements BeanPostProcessor {
    private final Map<String, List<String>> annotatedMap = new HashMap<>();
    private final String textBefore = "{Performance}: executing %s %s%s with arguments %s at %s%n";
    private final String textAfter = "{Performance}: completed %s %s%s with arguments %s at %s,"
            + "execution took %s seconds%n";

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        annotatedMap.put(beanName, new ArrayList<>());
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(PerformanceAnalyze.class)) {
                annotatedMap.get(beanName).add(method.getName());
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        List<String> annotatedMethods = annotatedMap.get(beanName);
        if (!annotatedMethods.isEmpty()) {
            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(),
                    (proxy, method, args) -> {
                        String methodName = method.getName();
                        if (annotatedMethods.contains(methodName)) {
                            String returnTypeName = method.getReturnType().getName();
                            StringJoiner argumentTypes = new StringJoiner(", ", "(", ")");
                            StringJoiner arguments = new StringJoiner(", ");
                            Parameter[] parameters = method.getParameters();
                            for (int i = 0; i < parameters.length; i++) {
                                String typeName = parameters[i].getType().getSimpleName();
                                argumentTypes.add(typeName);
                                String name = parameters[i].getName();
                                arguments.add(name + "=" + args[i]);
                            }
                            LocalTime before = LocalTime.now();
                            System.out.printf(textBefore,
                                    returnTypeName, methodName, argumentTypes, arguments, before);
                            Object result = method.invoke(bean, args);
                            LocalTime after = LocalTime.now();
                            long seconds = Duration.between(before, after).toSeconds();
                            System.out.printf(textAfter,
                                    returnTypeName, methodName, argumentTypes, arguments, after, seconds);
                            return result;
                        }
                        return method.invoke(bean, args);
                    });
        }
        return bean;
    }

}
