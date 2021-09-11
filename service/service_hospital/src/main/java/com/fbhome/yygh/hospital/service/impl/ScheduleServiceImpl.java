package com.fbhome.yygh.hospital.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fbhome.yygh.hospital.repository.ScheduleRepository;
import com.fbhome.yygh.hospital.service.ScheduleService;
import com.fbhome.yygh.model.hosp.Department;
import com.fbhome.yygh.model.hosp.Hospital;
import com.fbhome.yygh.model.hosp.Schedule;
import com.fbhome.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        // 先把参数的Map集合转换成对象,JSON转换
        String paramMapString = JSONObject.toJSONString( paramMap );
        //根据医院编号和排版编号查询
        Schedule schedule = JSONObject.parseObject( paramMapString, Schedule.class );
        Schedule scheduleExist=scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(),schedule.getHosScheduleId());
        // 如果存在，进行修改
        if(scheduleExist!=null){
            scheduleExist.setUpdateTime( new Date() );
            scheduleExist.setIsDeleted( 0 );
            scheduleExist.setStatus( 1 );
            scheduleRepository.save(scheduleExist);
        }else{  // 如果不存在，进行添加操作
            schedule.setCreateTime( new Date() );
            schedule.setUpdateTime( new Date() );
            schedule.setIsDeleted( 0 );
            schedule.setStatus( 1 );
            scheduleRepository.save(schedule);
        }
    }
    //查询排班接口
    @Override
    public Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        //创建pageable对象里面设置当前页和每页记录数,0是第一页
        Pageable pageable= PageRequest.of( page-1,limit );
        //创建Example对象
        Schedule schedule=new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo,schedule);
        schedule.setIsDeleted( 0 );
        schedule.setStatus( 1 );
        ExampleMatcher matcher= ExampleMatcher.matching()
                .withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING )
                .withIgnoreCase(true);
        Example<Schedule> example=Example.of( schedule,matcher );
       Page<Schedule> all = scheduleRepository.findAll( example, pageable );
        return all;
    }
    //删除排班接口
    @Override
    public void remove(String hoscode, String hosScheduleId) {
        //根据医院编号和排班编号查询信息
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId( hoscode, hosScheduleId );
        if(schedule!=null){
            scheduleRepository.deleteById( schedule.getId() );
        }
    }
}
