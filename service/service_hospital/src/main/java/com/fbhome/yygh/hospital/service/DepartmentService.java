package com.fbhome.yygh.hospital.service;


import com.fbhome.yygh.model.hosp.Department;
import com.fbhome.yygh.vo.hosp.DepartmentQueryVo;
import com.fbhome.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void save(Map<String, Object> paramMap);

    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    void remove(String hoscode, String depcode);

    List<DepartmentVo> findeDeptTree(String hoscode);
    //根据科室编号、医院编号 查询科室名称
    Object getDepName(String hoscode, String depcode);
    //根据科室编号、医院编号 查询科室对象
    Department getDepartment(String hoscode, String depcode);
}
