package com.fbhome.yygh.user.controller;

import com.fbhome.yygh.common.result.Result;
import com.fbhome.yygh.common.utils.AuthoContextHolder;
import com.fbhome.yygh.model.user.Patient;
import com.fbhome.yygh.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {
    @Autowired
    private PatientService patientService;
    //获取就诊人列表接口
    @GetMapping("auth/findAll")
    public Result findAll(HttpServletRequest request){
        Long userId=AuthoContextHolder.getUserId( request );
        List<Patient> list= patientService.findAllUserId(userId);
        return Result.ok(list);
    }
    //添加就诊人接口

    //根据id获取就诊人的信息

    //修改就诊人接口

    //删除就诊人信息
}
