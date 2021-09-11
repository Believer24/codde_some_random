package com.fbhome.yygh.hospital.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fbhome.yygh.hospital.repository.DepartmentRepository;
import com.fbhome.yygh.hospital.service.DepartmentService;
import com.fbhome.yygh.model.hosp.Department;
import com.fbhome.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        // paramMap转换成Department对象
        String paramMapString =JSONObject.toJSONString( paramMap);
        Department department= JSONObject.parseObject( paramMapString,Department.class );

        Department departmentExist=departmentRepository.
                getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());
        //如果有，更新，否则新增,根据医院编号和科室编号进行查询
        if(departmentExist!=null){
            departmentExist.setId( UUID.randomUUID().toString() );
            departmentExist.setUpdateTime( new Date() );
            departmentExist.setIsDeleted( 0 );
            departmentRepository.save(  departmentExist );
        }else {
            department.setId( UUID.randomUUID().toString() );
            department.setCreateTime( new Date() );
            department.setUpdateTime( new Date() );
            department.setIsDeleted( 0 );
            departmentRepository.save( department );
        }
    }

    //查询科室接口
    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //创建pageable对象里面设置当前页和每页记录数,0是第一页
        Pageable pageable= PageRequest.of( page-1,limit );
        //创建Example对象
        Department department=new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted( 0 );
        ExampleMatcher matcher= ExampleMatcher.matching()
                .withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING )
                .withIgnoreCase(true);
        Example<Department> example=Example.of( department,matcher );
        Page<Department> all = departmentRepository.findAll( example, pageable );
        return all;
    }

    //  删除科室接口
    @Override
    public void remove(String hoscode, String depcode) {
        //  根据医院编号和科室编号 查询出科室信息
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode( hoscode, depcode );
        if(department!=null){
            //调用删除
            departmentRepository.deleteById( department.getId() );
        }
    }
}