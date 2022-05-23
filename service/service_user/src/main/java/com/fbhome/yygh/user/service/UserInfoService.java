package com.fbhome.yygh.user.service;

import com.fbhome.yygh.model.user.UserInfo;
import com.fbhome.yygh.vo.user.LoginVo;
import com.fbhome.yygh.vo.user.UserAuthVo;

import java.util.Map;

public interface UserInfoService {
    Map<String, Object> loginUser(LoginVo loginVo);

    boolean save(UserInfo userInfo);

    UserInfo selectWxInfoOpenId(String openid);
    //用户认证方法
    void userAuth(Long userId, UserAuthVo userAuthVo);

    UserInfo getById(Long userId);
}
