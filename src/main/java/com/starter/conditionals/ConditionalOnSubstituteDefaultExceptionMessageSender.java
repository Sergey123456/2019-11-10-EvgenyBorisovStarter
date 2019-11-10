package com.starter.conditionals;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnSubstituteDefaultExceptionMessageSenderConditional.class)
public @interface ConditionalOnSubstituteDefaultExceptionMessageSender {
}
