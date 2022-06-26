package com.fbhome.yygh.hospital.service;

import com.fbhome.yygh.model.hosp.Schedule;
import com.fbhome.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    //上传排班接口
    void save(Map<String, Object> paramMap);

    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);
    //删除排班
    void remove(String hoscode, String hosScheduleId);

    //查询排班
    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);
}
