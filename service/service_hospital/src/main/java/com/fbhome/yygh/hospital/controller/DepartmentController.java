package com.fbhome.yygh.hospital.controller;

import com.fbhome.yygh.common.result.Result;
import com.fbhome.yygh.hospital.service.DepartmentService;
import com.fbhome.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")
//@CrossOrigin
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    //根据医院编号，查询医院所有科室列表
    @ApiOperation( value="查询医院所有科室列表" )
    @GetMapping("getDeptList/{hoscode}")
    public Result getDeptList(@PathVariable String hoscode) {
       List<DepartmentVo> list= departmentService.findeDeptTree(hoscode);
       return Result.ok(list);
    }
}