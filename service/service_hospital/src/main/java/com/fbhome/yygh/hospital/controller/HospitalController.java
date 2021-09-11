package com.fbhome.yygh.hospital.controller;

import com.fbhome.yygh.common.result.Result;
import com.fbhome.yygh.hospital.service.HospitalService;
import com.fbhome.yygh.model.hosp.Hospital;
import com.fbhome.yygh.vo.hosp.HospitalQueryVo;
import com.fbhome.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    //医院列表(条件查询分页)
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pageModel=hospitalService.selectHospPage(page,limit,hospitalQueryVo);
        return Result.ok(pageModel);

    }
}
