package com.starter;

import com.starter.autocomplite.ExceptionHandlerProperties;
import com.starter.exceptions.NotFoundRecipientListException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerExceptionAnnotationBeanPostProcessor implements BeanPostProcessor {
    Map<String, Class<?>> beansAnnotatedCatchException = new HashMap<>();
    @Autowired
    Environment environment;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    ExceptionHandlerProperties exceptionHandlerProperties;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(HandlerException.class))
            beansAnnotatedCatchException.put(beanName, bean.getClass());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = beansAnnotatedCatchException.get(beanName);
        if (beanClass != null) {
            Class<?> exceptionClass =  beanClass.getAnnotation(HandlerException.class).exceptionClass();
            return Proxy.newProxyInstance(beanClass.getClassLoader(),
                                            beanClass.getInterfaces(),
                                            new ImplementationInvocationHandler(bean, exceptionClass));
        }
        return bean;
    }

    class ImplementationInvocationHandler implements InvocationHandler {
        Object bean;
        Class<?> exceptionClass;

        ImplementationInvocationHandler(Object bean, Class<?> exceptionClass) {
            this.bean = bean;
            this.exceptionClass = exceptionClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object retVal = null;
            try {
                retVal = method.invoke(bean, args);
            } catch (Exception e) {
                if (e.fillInStackTrace().getCause().getClass().getCanonicalName().equals(
                        exceptionClass.getCanonicalName())) {
                    System.out.println("Я тебя поймал!");
                    try {
                        mailSender.send(getMessage(e));
                    } catch (Exception ex) {
                        return null;
                    }
                }
            }
            return retVal;
        }

        private SimpleMailMessage getMessage(Exception e) throws NotFoundRecipientListException {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(getEmails());
            msg.setSubject(exceptionHandlerProperties.getSubject() + " " +
                                                        e.fillInStackTrace().getCause().getClass().getCanonicalName());
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement ste: e.getStackTrace()) {
                sb.append(ste + "\n");
            }
            msg.setText(exceptionHandlerProperties.getText()
                            + "\n\nException:\n" + e.fillInStackTrace().getCause().getClass().getCanonicalName()
                            + "\nStack trace:\n" + sb);
            return msg;
        }

        private String[] getEmails() throws NotFoundRecipientListException {
            try {
                if (exceptionHandlerProperties.getEmail() == null
                        || exceptionHandlerProperties.getEmail().isEmpty()) {
                    List<String> emails;
                    File file = ResourceUtils.getFile(environment.getProperty("EXCEPTIONS.EMAILS"));
                    emails = Files.readAllLines(file.toPath());
                    return emails.toArray(String[]::new);
                } else
                    return exceptionHandlerProperties.getEmail().toArray(String[]::new);
            } catch (Exception e) {
                System.out.println();
                System.out.println("Сan not find the recipient list!");
                System.out.println("Error messages were not sent!");
                System.out.println();
                System.out.println("You can set the recipient list in one of the following ways:");
                System.out.println("1. application.properties -> exceptionhandler.email=email@email.com");
                System.out.println("2. add environment variable in OS with name \"EXCEPTIONS.EMAILS\" " +
                        "and change file with recipient list.");
                System.out.println("File recipient list example (RecipintList.txt):");
                System.out.println("-----------------------------------------------");
                System.out.println("email1@email.com");
                System.out.println("email2@email.com");
                System.out.println("email3@email.com");
                System.out.println("-----------------------------------------------");
                System.out.println();
                throw new NotFoundRecipientListException();
            }
        }
    }
}
