package com.geomin.demo;

import jakarta.servlet.ServletContextListener;
import jakarta.servlet.http.HttpServlet;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.ui.context.ThemeSource;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.servlet.HttpServletBean;

@PropertySource("classpath:apiKey.properties")

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}