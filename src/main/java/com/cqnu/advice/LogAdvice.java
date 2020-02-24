package com.cqnu.advice;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author zh
 * @date 2019/12/30
 */
public class LogAdvice {

    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        StringBuffer sb = new StringBuffer();
        Object target = joinPoint.getTarget();
        String methodName = joinPoint.getSignature().getName();
        sb.append(methodName);
        sb.append(" invoke start: parameters: ");
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            sb.append(arg).append(", ");
        }
        Field logger = target.getClass().getDeclaredField("LOGGER");
        Method infoMethod = null;
        Object loggerObject = null;
        boolean isPrintable = false;
        if (logger != null && logger.getDeclaringClass() != null) {
            logger.setAccessible(true);
            loggerObject = logger.get(target);
            logger.setAccessible(true);
            isPrintable = true;
        }
        if (isPrintable) {
            infoMethod = logger.getType().getDeclaredMethod("info", String.class);
            infoMethod.invoke(loggerObject, sb.toString());
        }
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed(args);

        long endTime = System.currentTimeMillis();
        if (isPrintable) {
            infoMethod.invoke(loggerObject, methodName + " 方法耗时: " + (endTime - startTime) + "ms.");
        }
        return result;
    }
}
