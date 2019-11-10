package com.starter.conditionals;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnSubstituteDefaultExceptionMessageSenderConditional implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getBeanFactory().getBeanNamesForAnnotation(SubstituteDefaultExceptionMessageSender.class) == null
                || context.getBeanFactory().getBeanNamesForAnnotation(SubstituteDefaultExceptionMessageSender.class).length == 0;
    }
}
