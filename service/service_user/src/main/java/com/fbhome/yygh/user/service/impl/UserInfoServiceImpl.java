package com.fbhome.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.fbhome.yygh.common.exception.YyghException;
import com.fbhome.yygh.common.helper.JwtHelper;
import com.fbhome.yygh.common.result.ResultCodeEnum;
import com.fbhome.yygh.model.user.UserInfo;
import com.fbhome.yygh.user.mapper.UserInfoMapper;
import com.fbhome.yygh.user.service.UserInfoService;
import com.fbhome.yygh.vo.user.LoginVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        //1.从loginVo里得到注册、登录用户的手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //2.判断手机号和验证码是否为空
        if(StringUtils.isEmpty( phone )||StringUtils.isEmpty( code )){
            throw new YyghException( ResultCodeEnum.PARAM_ERROR );
        }
        //3.判断下手机验证码是否与输入一致
        QueryWrapper<UserInfo> wrapper=new QueryWrapper<>();
        wrapper.eq( "phone",phone );
        UserInfo userInfo=baseMapper.selectOne( wrapper );
        //第一次使用这个手机号进行登录
        if(userInfo==null){
            //添加信息到数据库
            userInfo=new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            this.save(userInfo);
        }
        //校验用户是否被禁用
        if(userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }
        //4.判断是否是第一次登录，根据手机号查询数据库，如果不存在相同手机号就是第一次登录

        //5.不是第一次登录，就直接进行登录

        //6.返回登录后的一些信息，返回用户名，token信息
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //TODO 返回token，使用JWT生成token
        String token=JwtHelper.createToken(userInfo.getId(),name);

        map.put("token", token);
        return map;

    }


    @Override
    public boolean save(UserInfo entity) {
        return false;
    }
}
