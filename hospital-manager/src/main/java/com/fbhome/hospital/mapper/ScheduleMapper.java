package com.fbhome.hospital.mapper;

import com.fbhome.hospital.model.Schedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ScheduleMapper extends BaseMapper<Schedule> {

}
