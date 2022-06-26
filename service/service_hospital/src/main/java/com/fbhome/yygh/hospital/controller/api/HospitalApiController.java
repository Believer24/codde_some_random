package com.fbhome.yygh.hospital.controller.api;

import com.fbhome.yygh.common.result.Result;
import com.fbhome.yygh.hospital.service.DepartmentService;
import com.fbhome.yygh.hospital.service.HospitalService;
import com.fbhome.yygh.hospital.service.ScheduleService;
import com.fbhome.yygh.model.hosp.Hospital;
import com.fbhome.yygh.vo.hosp.DepartmentVo;
import com.fbhome.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp/hospital")
public class HospitalApiController {
    @Autowired
    private HospitalService hospitalService;
    private DepartmentService departmentService;
    private ScheduleService scheduleService;
    @ApiOperation( value="查询医院列表" )
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitals = hospitalService.selectHospPage( page, limit, hospitalQueryVo );
        return Result.ok(hospitals);

    }

    @ApiOperation( value = "根据医院名称进行查询")
    @GetMapping("findByHosName/{hosname}")
    public Result findByHosName(@PathVariable String hosname){
       List<Hospital> hospitalList= hospitalService.findByHosName(hosname);
       return Result.ok(hospitalList);
    }

    @ApiOperation( value="根据医院编号获取科室" )
    @GetMapping("department/{hoscode}")
    public Result index(@PathVariable String hoscode){
        List<DepartmentVo> departmentVos = departmentService.findeDeptTree( hoscode );
        return Result.ok(departmentVos);
    }

    @ApiOperation( value="根据医院编号获取预约挂号详情信息")
    @GetMapping("findHospDetail/{hoscode}")
    public Result item(@PathVariable String hoscode){
        Map<String,Object> map=hospitalService.item(hoscode);
        return Result.ok(map);
    }

    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingSchedule(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Integer page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Integer limit,
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode) {
        return Result.ok(scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode));
    }

    @ApiOperation(value = "获取排班数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleList(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode,
            @ApiParam(name = "workDate", value = "排班日期", required = true)
            @PathVariable String workDate) {
        return Result.ok(scheduleService.getDetailSchedule(hoscode, depcode, workDate));
    }

}
