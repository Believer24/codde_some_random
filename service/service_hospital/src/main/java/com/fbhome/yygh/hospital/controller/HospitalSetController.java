package com.fbhome.yygh.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fbhome.yygh.common.exception.YyghException;
import com.fbhome.yygh.hospital.service.HospitalSetService;
import com.fbhome.yygh.hospital.utils.MD5;
import com.fbhome.yygh.model.hosp.HospitalSet;
import com.fbhome.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.fbhome.yygh.common.result.Result;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
//@CrossOrigin
public class HospitalSetController {
    //注入Service
    @Autowired
    private HospitalSetService hospitalSetService;
    //1.查询医院设置表中的所有信息
    @ApiOperation( value = "获取所有医院设置的信息")
    @GetMapping("/findAll")
    public Result findAllHospitalSet(){
        //调用Service方法
        List<HospitalSet> hospitalSetList=hospitalSetService.list();
        return  Result.ok(hospitalSetList);
    }

    //2.逻辑删除医院设置,根据id
    @ApiOperation( value = "逻辑删除")
    @DeleteMapping("{id}")
    public boolean removeHospitalSet(@PathVariable Long id){
        boolean flag=hospitalSetService.removeById( id );
        return flag;
    }

    //3.条件查询带分页
    @PostMapping("/findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current, @PathVariable long limit,
                                  @RequestBody(required=false) HospitalSetQueryVo hospitalSetQueryVo ){
        //创建page对象，传递当前页，每页记录数
        Page<HospitalSet> page=new Page<>(current,limit);
        //构建条件
        QueryWrapper<HospitalSet> wrapper=new QueryWrapper<>();
        //医院的名称
        String hosname = hospitalSetQueryVo.getHosname();
        //医院的编号
        String hoscode = hospitalSetQueryVo.getHoscode();
        if(!StringUtils.isEmpty( hosname )){
            wrapper.like( "hosname",hospitalSetQueryVo.getHosname() );
        }
        if(!StringUtils.isEmpty( hoscode )){
            wrapper.eq( "hoscode",hospitalSetQueryVo.getHoscode() );
        }

        //调用方法实现分页查询
        Page<HospitalSet> pageHospitalSet=hospitalSetService.page( page,wrapper );

        //返回结果
        return Result.ok(pageHospitalSet);
    }

    //4.添加医院设置
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        //设置状态 1 使用 / 0 不能使用
        hospitalSet.setStatus( 1 );
        //签名密钥
        Random random=new Random();
        hospitalSet.setSignKey(  MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)) );
       //调用service
        boolean save=hospitalSetService.save(hospitalSet);
        if(save){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }
    //5.根据id获取医院设置
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id){
        //模拟异常
        HospitalSet hospitalSet = hospitalSetService.getById( id );
        return Result.ok(hospitalSet);
    }

    //6.修改医院设置
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean flag = hospitalSetService.updateById( hospitalSet );
        if(flag){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    //7.批量删除医院设置
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> ids){
        hospitalSetService.removeByIds( ids );
        return Result.ok();
    }

    //8.医院设置锁定和解锁
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,
                                  @PathVariable Integer status){
        //根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById( id );
        //设置医院状态
        hospitalSet.setStatus( status );
        //调用方法
        hospitalSetService.updateById( hospitalSet );
        return Result.ok();
    }

    //9.发送签名密钥
    @PutMapping("sendKey/{id}")
    public Result lockHospitalSet(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById( id );
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO 发送短信
        return  Result.ok();
    }
}
