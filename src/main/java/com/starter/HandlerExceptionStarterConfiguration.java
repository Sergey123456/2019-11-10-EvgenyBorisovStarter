package com.starter;

import com.starter.autocomplite.ExceptionHandlerProperties;
import com.starter.conditionals.ConditionalOnSubstituteDefaultExceptionMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class HandlerExceptionStarterConfiguration {

    @Autowired
    ExceptionHandlerProperties exceptionHandlerProperties;

    @Bean
    ExceptionHandlerProperties getExceptionHandlerProperties() {
        return new ExceptionHandlerProperties();
    }

    @Bean
    public BeanPostProcessor getExceptionsHandlerAnnotationBeanPostProcessor() {
        return new HandlerExceptionAnnotationBeanPostProcessor();
    }

    @Bean
    @ConditionalOnSubstituteDefaultExceptionMessageSender
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(exceptionHandlerProperties.getHost());
        mailSender.setPort(exceptionHandlerProperties.getPort());

        mailSender.setUsername(exceptionHandlerProperties.getUsername());
        mailSender.setPassword(exceptionHandlerProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", exceptionHandlerProperties.getMail_transport_protocol());
        props.put("mail.smtp.auth", exceptionHandlerProperties.isMail_smtp_auth());
        props.put("mail.smtp.starttls.enable", exceptionHandlerProperties.isMail_smtp_starttls_enable());
        props.put("mail.debug", exceptionHandlerProperties.isMail_debug());

        return mailSender;
    }
}
