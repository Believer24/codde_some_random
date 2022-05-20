package com.fbhome.yygh.user.service;

import com.fbhome.yygh.model.user.UserInfo;
import com.fbhome.yygh.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService {
    Map<String, Object> loginUser(LoginVo loginVo);

    boolean save(UserInfo userInfo);

    UserInfo selectWxInfoOpenId(String openid);
}
