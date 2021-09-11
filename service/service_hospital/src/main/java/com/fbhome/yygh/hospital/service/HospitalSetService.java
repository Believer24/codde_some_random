package com.fbhome.yygh.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fbhome.yygh.model.hosp.Hospital;
import com.fbhome.yygh.model.hosp.HospitalSet;


public interface HospitalSetService extends IService<HospitalSet> {
    String getSignKey(String hoscode);
}
