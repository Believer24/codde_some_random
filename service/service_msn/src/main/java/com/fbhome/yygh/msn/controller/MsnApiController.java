package com.fbhome.yygh.msn.controller;


import com.fbhome.yygh.msn.service.MsnService;
import com.fbhome.yygh.msn.utils.RandomUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fbhome.yygh.common.result.Result;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/msn")
public class MsnApiController {
    @Autowired
    private MsnService msnService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //发送手机验证码
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone){
        //1.先从redis获取验证码，如果可以获取，返回ok
        //key 手机号  value 就是验证码
        String code=redisTemplate.opsForValue().get( phone );
        if(!StringUtils.isEmpty(code)){
            return Result.ok();
        }
        //2.如果从redis中获取不到，生成验证码，通过整合短信服务进行发送
          code= RandomUtil.getSixBitRandom();
        // 调用service方法，通过整合短信服务进行发送
         boolean isSend=msnService.send(phone,code);
        //3.生成的验证码放到redis中，设置有效时间
        if(isSend){
            redisTemplate.opsForValue().set( phone,code,2, TimeUnit.MINUTES );
            return Result.ok();
        }else{
            return Result.fail("发送短信失败！");
        }
    }

}
