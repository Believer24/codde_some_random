package com.fbhome.yygh.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) //exclude 不包含,取消数据源的自动配置
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.fbhome")
public class ServiceOssApplication {
    public static void main(String args[]){
        SpringApplication.run( ServiceOssApplication.class,args);
    }
}
