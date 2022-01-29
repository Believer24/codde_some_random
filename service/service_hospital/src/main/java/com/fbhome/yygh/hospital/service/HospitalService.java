package com.fbhome.yygh.hospital.service;

import com.fbhome.yygh.model.hosp.Hospital;
import com.fbhome.yygh.vo.hosp.HospitalQueryVo;
import com.fbhome.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService  {
    //上传医院接口
    void save(Map<String, Object> paramMap);

    Hospital getByHosCode(String hoscode);

    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);
    //医院详情页
    Map<String, Object> getHospById(String id);

    String getHospName(String hoscode);
    //根据医院名称做查询
    List<Hospital> findByHosName(String hosname);
    //根据医院编号获取医院科室的挂号详情信息
    Map<String, Object> item(String hoscode);
    //根据医院编号查询
    Hospital getByHoscode(String hoscode);

}
