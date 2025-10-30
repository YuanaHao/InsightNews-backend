package com.sosd.insightnews.auth;

import com.sosd.insightnews.exception.http.BadRequestException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class AuthCheckAspect {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Autowired
    private ApplicationContext applicationContext;

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(authCheck)")
    public Object handleCustomAnnotation(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {

        // 获取注解中的表达式
        String expression = authCheck.value();

        // 获取方法的参数
        Object[] args = joinPoint.getArgs();
        Method method = getMethod(joinPoint);

        // 创建SpEL上下文
        StandardEvaluationContext context = new MethodBasedEvaluationContext(null, method, args, parameterNameDiscoverer);
        // 注册 BeanResolver，用于解析 Bean
        context.setBeanResolver(new BeanFactoryResolver(applicationContext));

        // 解析表达式
        Boolean result = parser.parseExpression(expression).getValue(context, Boolean.class);

        // 在此可以对结果做逻辑处理
        // System.out.println("解析后的表达式结果：" + result);

        if (result == null || !result) {
            throw new BadRequestException("权限不足");
        }

        // 执行目标方法
        return joinPoint.proceed();
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        Method method = joinPoint.getTarget().getClass()
                .getMethod(joinPoint.getSignature().getName(),
                        ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes());
        return method;
    }
}
