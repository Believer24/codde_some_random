package com.fbhome.yygh.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fbhome.yygh.hospital.mapper.HospitalSetMapper;
import com.fbhome.yygh.hospital.service.HospitalSetService;
import com.fbhome.yygh.model.hosp.Hospital;
import com.fbhome.yygh.model.hosp.HospitalSet;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
    // 2.根据传递过来的医院编号，去查询数据库，查询signKey
    @Override
    public String getSignKey(String hoscode) {
        QueryWrapper<HospitalSet> wrapper=new QueryWrapper<>();
        wrapper.eq( "hoscode",hoscode );
        HospitalSet hospitalSet=baseMapper.selectOne( wrapper );
        return hospitalSet.getSignKey();
    }


}
