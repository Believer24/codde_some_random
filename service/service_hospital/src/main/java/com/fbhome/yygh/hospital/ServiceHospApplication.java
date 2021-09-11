package com.fbhome.yygh.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.fbhome")
@EnableDiscoveryClient
public class ServiceHospApplication {
    public static void main(String[] args){
        SpringApplication.run(ServiceHospApplication.class, args);
    }
}
