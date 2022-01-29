package com.fbhome.yygh.hospital.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fbhome.yygh.hospital.repository.DepartmentRepository;
import com.fbhome.yygh.hospital.service.DepartmentService;
import com.fbhome.yygh.model.hosp.Department;
import com.fbhome.yygh.vo.hosp.DepartmentQueryVo;
import com.fbhome.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    //根据医院编号，查询医院所有科室列表
    @Override
    public List<DepartmentVo> findeDeptTree(String hoscode) {
        //创建一个list集合，用于最终的数据封装
        List<DepartmentVo> result=new ArrayList<>();
        //根据医院编号查询医院里的所所有科室信息
        Department departmentQuery=new Department();
        departmentQuery.setHoscode( hoscode );
        Example<Department> example=Example.of( departmentQuery );
        //所有科室列表的信息
        List<Department> departmentList=departmentRepository.findAll(example);
        //根据大科室编号 bigcode进行分组,获取里面每个大科室里面的下级子科室
        Map<String, List<Department>> departmentMap =
                departmentList.stream().collect( Collectors.groupingBy( Department::getBigcode ) );
        //遍历map集合 departmentMap
        for(Map.Entry<String,List<Department>> entry:departmentMap.entrySet()){
            //大科室的编号
            String bigcode = entry.getKey();
            //大科室编号对应的全局数据
            List<Department> department1List = entry.getValue();
            // 封装大科室
            DepartmentVo department1Vo=new DepartmentVo();
            department1Vo.setDepcode( bigcode );
            department1Vo.setDepname( department1List.get( 0 ).getBigname() );
            //封装小科室
            List<DepartmentVo> childern=new ArrayList<>();
            for(Department department:department1List){
                DepartmentVo departmentVo2=new DepartmentVo();
                departmentVo2.setDepcode(  department.getDepcode());
                departmentVo2.setDepname( department.getDepname() );
                //封装到list集合中去
                childern.add( departmentVo2 );
            }
            //把小科室的list集合放到大科室的childern里面
            department1Vo.setChildren( childern );
            //放到最终的Result里
            result.add( department1Vo );
        }
        return result;
    }
    //根据科室编号、医院编号 查询科室名称
    @Override
    public Object getDepName(String hoscode, String depcode) {
        Department department=departmentRepository.getDepartmentByHoscodeAndDepcode( hoscode,depcode);
       if(department!=null){
           return department.getDepname();
       }
        return null;
    }
}
