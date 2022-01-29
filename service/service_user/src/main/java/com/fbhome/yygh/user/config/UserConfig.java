package com.fbhome.yygh.user.config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.fbhome.yygh.user.mapper")
public class  UserConfig {
}
