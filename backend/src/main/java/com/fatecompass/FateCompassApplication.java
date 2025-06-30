package com.fatecompass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 算命網站後端主應用程式
 * 支援獨立運行和Tomcat部署
 */
@SpringBootApplication
public class FateCompassApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(FateCompassApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FateCompassApplication.class);
    }
} 