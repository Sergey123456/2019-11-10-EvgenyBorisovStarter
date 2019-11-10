package com.starter.autocomplite;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "exceptionhandler")
public class ExceptionHandlerProperties {
    private String host = "smtp.gmail.com";
    private int port = 587;

    private String username = "username";
    private String password = "password";

    private String mail_transport_protocol = "smtp";
    private boolean mail_smtp_auth = true;
    private boolean mail_smtp_starttls_enable = true;
    private boolean mail_debug = true;

    private List<String> email;

    private String subject = "Exception caught";
    private String text = "Attention! Exception caught.";

    private String environment_variable = "EXCEPTIONS.EMAILS";

}