package com.fbhome.hospital.mapper;

import com.fbhome.hospital.model.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

}
