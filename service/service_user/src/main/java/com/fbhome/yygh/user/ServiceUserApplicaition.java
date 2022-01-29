package com.fbhome.yygh.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.fbhome")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.fbhome")
public class ServiceUserApplicaition {
    public static void main(String args[]){
        SpringApplication.run(ServiceUserApplicaition.class,args);
    }
}
