package com.fbhome.yygh.user.controller;


import com.fbhome.yygh.common.result.Result;
import com.fbhome.yygh.user.service.UserInfoService;
import com.fbhome.yygh.vo.user.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

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
}
