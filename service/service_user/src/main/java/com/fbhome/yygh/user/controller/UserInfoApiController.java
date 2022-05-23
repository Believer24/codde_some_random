package com.fbhome.yygh.user.controller;


import com.fbhome.yygh.common.result.Result;
import com.fbhome.yygh.common.utils.AuthoContextHolder;
import com.fbhome.yygh.model.user.UserInfo;
import com.fbhome.yygh.user.service.UserInfoService;
import com.fbhome.yygh.vo.user.LoginVo;
import com.fbhome.yygh.vo.user.UserAuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    //用户手机号登录接口
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        Map<String,Object> userInfos=userInfoService.loginUser(loginVo);
        return Result.ok(userInfos);

    }
    //用户认证的接口
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request){
        //传递两个参数,第一个是用户id，第二个是参数认证数据VO对象
        userInfoService.userAuth( AuthoContextHolder.getUserId( request ),userAuthVo);
        return Result.ok();
    }

    //获取用户id信息的接口
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request){
        Long userId=AuthoContextHolder.getUserId( request );
        UserInfo userInfo=userInfoService.getById(userId);
        return Result.ok(userInfo);
    }
}
