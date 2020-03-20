package ru.web.webapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Autowired
    private Environment environment;

    public String getTokenSecret() {
        return environment.getProperty("tokenSecret");
    }

    public String getAmazonAccessKeyId() {
        return environment.getProperty("amazonAccessKeyId");
    }

    public String getAmazonSecretKey() {
        return environment.getProperty("amazonSecretKey");
    }
}
