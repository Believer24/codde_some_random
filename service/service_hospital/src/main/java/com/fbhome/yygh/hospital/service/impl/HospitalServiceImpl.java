package com.fbhome.yygh.hospital.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fbhome.yygh.cmn.client.DictFeignClient;
import com.fbhome.yygh.hospital.repository.HospitalRepository;
import com.fbhome.yygh.hospital.service.HospitalService;
import com.fbhome.yygh.model.hosp.Hospital;
import com.fbhome.yygh.vo.hosp.HospitalQueryVo;
import com.fbhome.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> paramMap) {
        // 先把参数的Map集合转换成对象,JSON转换
        String mapString = JSONObject.toJSONString( paramMap );
        Hospital hospital = JSONObject.parseObject( mapString, Hospital.class );
        // 先判断是否存在相同的数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalIsExist=hospitalRepository.getHospitalByHoscode(hoscode);
        // 如果存在，进行修改
        if(hospitalIsExist!=null){
            Random r = new Random();
            hospital.setId( String.valueOf( r.nextFloat() ) );
            hospital.setStatus( hospitalIsExist.getStatus() );
            hospital.setCreateTime(hospitalIsExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else{  // 如果不存在，进行添加操作
            hospital.setStatus( 0 );
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }


    }

    @Override
    public Hospital getByHosCode(String hoscode) {
        Hospital hospital=hospitalRepository.getHospitalByHoscode( hoscode );
        return hospital;
    }

    //医院列表接口查询 
    @Override
    public  Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建pageable对象
        Pageable pageable= PageRequest.of( page-1,limit );
        //创建条件匹配器
        ExampleMatcher matcher=ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING  ).withIgnoreCase(true);
        //hospitalSetQueryVo转换Hospital对象
        Hospital hospital=new Hospital();
        BeanUtils.copyProperties(  hospitalQueryVo,hospital);
        //创建Example对象
        Example<Hospital> example=Example.of( hospital,matcher );
        //调用方法实现
        Page<Hospital> pages = hospitalRepository.findAll( example, pageable );
        //获取查询list集合，遍历进行医院等级封装
        pages.getContent().stream().forEach( item->{
            this.setHospitalClass(item);
        } );

        return pages;
    }
    //更新医院上线状态
    @Override
    public void updateStatus(String id, Integer status) {
        //根据id查询医院信息
        Hospital hospital = hospitalRepository.findById( id ).get();
        hospital.setStatus( status );
        hospital.setUpdateTime( new Date() );
        hospitalRepository.save( hospital );
    }
    //医院详情信息
    @Override
    public Map<String,Object> getHospById(String id) {
        Map<String,Object> result=new HashMap<>();
        Hospital hospital =  this.setHospitalClass(hospitalRepository.findById( id ).get());
        //医院基本信息(包含医院等级)
        result.put( "hospital",hospital );
        //单独处理更直观
        result.put( "bookingRule",hospital.getBookingRule() );
        // 不需要重复返回
        hospital.setBookingRule( null );
        return result;
    }

    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode( hoscode );
        if(hospital!=null){
            return hospital.getHosname();
        }
        return null;
    }
    //根据医院名称做查询
    @Override
    public List<Hospital> findByHosName(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }
    //实现根据医院编号查询
    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }
    //根据医院编号获取医院科室的挂号详情信息
    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String, Object> result = new HashMap<>();
        //医院详情
        Hospital hospital = this.setHospitalHosType(this.getByHoscode(hoscode));
        result.put("hospital", hospital);
        //预约规则
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }

    //获取查询到的医院集合，遍历进行医院等级封装
    private Hospital setHospitalHosType(Hospital hospital) {
        //根据dictCode和value获取医院等级名称
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());
        //查询省，市，地区
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());
        hospital.getParam().put("hostypeString",hostypeString);
        hospital.getParam().put("fullAddress",provinceString + cityString + districtString);
        return hospital;
    }

    //获取查询list集合，遍历进行医院等级封装
    private Hospital setHospitalClass(Hospital hospital) {
        String hostypeString = dictFeignClient.getName( "hostype", hospital.getHostype() );
        String provinceString = dictFeignClient.getName( hospital.getProvinceCode() );
        String cityString = dictFeignClient.getName( hospital.getCityCode() );
        String distictString = dictFeignClient.getName( hospital.getDistrictCode() );
        hospital.getParam().put( "hostypeString",hostypeString );
        hospital.getParam().put( "fullAddress",provinceString+cityString+distictString );
        return hospital;
    }
}
