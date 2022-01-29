package com.fbhome.yygh.hospital.repository;

import com.fbhome.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {
    //判断是否存在数据
    Hospital getHospitalByHoscode(String hashcode);
    //根据医院名称做查询
    List<Hospital> findHospitalByHosnameLike(String hosname);
}
