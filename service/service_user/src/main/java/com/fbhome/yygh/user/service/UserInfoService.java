package com.fbhome.yygh.user.service;

import com.fbhome.yygh.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService {
    Map<String, Object> loginUser(LoginVo loginVo);
}
