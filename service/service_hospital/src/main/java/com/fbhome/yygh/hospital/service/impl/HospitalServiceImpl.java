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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
        String hashcode = hospital.getHoscode();
        Hospital hospitalIsExist=hospitalRepository.getHospitalByHoscode(hashcode);
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
