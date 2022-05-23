package com.fbhome.yygh.common.utils;


import com.fbhome.yygh.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;


//获取当前用户信息的工具类
public class AuthoContextHolder {
    //获取当前用户的id
    public  static  Long getUserId(HttpServletRequest request){
        String token=request.getHeader("token");
        //jwt从token中获取userId
        Long userId= JwtHelper.getUserId( token );
        return userId;
    }

    //获取当前用户的名称
    public static String getUserName(HttpServletRequest request){
        String token=request.getHeader( "token" );
        String userName=JwtHelper.getUserName( token );
        return userName;
    }
}
