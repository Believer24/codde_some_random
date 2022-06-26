package com.fbhome.yygh.hospital.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fbhome.yygh.common.exception.YyghException;
import com.fbhome.yygh.common.result.ResultCodeEnum;
import com.fbhome.yygh.hospital.repository.ScheduleRepository;
import com.fbhome.yygh.hospital.service.DepartmentService;
import com.fbhome.yygh.hospital.service.HospitalService;
import com.fbhome.yygh.hospital.service.ScheduleService;
import com.fbhome.yygh.model.hosp.BookingRule;
import com.fbhome.yygh.model.hosp.Department;
import com.fbhome.yygh.model.hosp.Hospital;
import com.fbhome.yygh.model.hosp.Schedule;
import com.fbhome.yygh.vo.hosp.BookingScheduleRuleVo;
import com.fbhome.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;
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

    //根据医院编号和科室编号查询排班接口
    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        //1.根据医院编号和科室编号先做查询
        Criteria criteria=Criteria.where( "hoscode" ).is( hoscode ).and( "depcode" ).is( depcode );
        //2.根据工作日期workDate进行分组的操作
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match( criteria ), //匹配条件
                Aggregation.group( "workDate" ) //分组字段
                .first( "workDate" ).as( "workDate" )
                .count().as( "docCount" )
                        //3.统计可预约的号源数量
                .sum( "reservedNumber" ).as( "reservedNumber" )
                .sum( "availableNumber" ).as( "availableNumber" ),
                Aggregation.sort( Sort.Direction.DESC,"workDate" ),
                //4.实现分页
                Aggregation.skip( (page-1)*limit ),
                Aggregation.limit( limit )
        );
        //调用方法，最终执行
        AggregationResults<BookingScheduleRuleVo> aggResult = mongoTemplate.aggregate( aggregation, Schedule.class, BookingScheduleRuleVo.class );
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResult.getMappedResults();

        //分组查询的总记录数
        Aggregation totalAgg=Aggregation.newAggregation( Aggregation.match( criteria ),
                Aggregation.group( "workDate" ));
        AggregationResults<BookingScheduleRuleVo> totalAggResults = mongoTemplate.aggregate( totalAgg, Schedule.class, BookingScheduleRuleVo.class );
        int total=totalAggResults.getMappedResults().size();
        //把日期对应的星期获取出来
        for(BookingScheduleRuleVo bookingScheduleRuleVo:bookingScheduleRuleVoList){
                Date workDate=bookingScheduleRuleVo.getWorkDate();
                String dayOfWeek=this.getDayOfWeek( new DateTime(workDate) );
                bookingScheduleRuleVo.setDayOfWeek( dayOfWeek );

        }
        //设置最终数据，进行返回
        Map<String,Object> result=new HashMap<>();
        result.put( "bookingScheduleRuleVoList" ,bookingScheduleRuleVoList);
        result.put( "total", total);

        //获取医院名称
        String hosName=hospitalService.getHospName(hoscode);
        //其他基础数据
        Map<String,String> baseMap=new HashMap<>();
        baseMap.put( "hosName",hosName );
        result.put( "baseMap",baseMap );

        return result;
    }
    //根据医院编号、科室编号和工作日期，查询排班详情信息
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        //根据参数查询mongodb
        List<Schedule> scheduleList=scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
        //遍历得到的List集合，向其设置值，医院名称，科室名称，日期对应的星期
        scheduleList.stream().forEach( item-> {
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    /**
     * 获取可预约排班的数据
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    @Override
    public Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode) {
        Map<String,Object> result=new HashMap<>();
        //获取预约的规则
        //根据医院的编号，获取预约的规则
        Hospital hospital = hospitalService.getByHosCode( hoscode );
        if(Objects.isNull( hospital )){
            throw new YyghException( ResultCodeEnum.DATA_ERROR );
        }
        BookingRule bookingRule=hospital.getBookingRule();
        //获取可预约的数据（带分页）
        IPage iPage=this.getListDate(page,limit,bookingRule);
        //获取当前可预约的日期
        List<Date> dateList=iPage.getRecords();

        //获取可预约日期科室的剩余的预约数
        Criteria criteria=Criteria.where( "hoscode" ).is( hoscode ).and( "depcode" ).is( depcode ).and( "workDate" ).in( dateList );
        Aggregation agg=Aggregation.newAggregation(
                Aggregation.match( criteria ),
                Aggregation.group( "workDate" ).first( "workDate" ).as("workDate")
                .count().as( "docCount" ).sum( "availableNumber" ).as("availableNumber")
                .sum( "reservedNumber" ).as( "reservedNumber" )
        );
         AggregationResults<BookingScheduleRuleVo> aggregateResult =
                 mongoTemplate.aggregate( agg, Schedule.class, BookingScheduleRuleVo.class );
        List<BookingScheduleRuleVo> scheduleVoList = aggregateResult.getMappedResults();
        //合并数据  map集合 key日期 value预约规则和剩余数量等信息
        Map<Date,BookingScheduleRuleVo> scheduleRuleVoMap=new HashMap<>();
        if(!CollectionUtils.isEmpty(scheduleVoList)){
            scheduleRuleVoMap=scheduleVoList.stream().
                    collect( Collectors.toMap(BookingScheduleRuleVo::getWorkDate,
                    BookingScheduleRuleVo ->BookingScheduleRuleVo ));
        }
        //获取可预约排班的规则
        List<BookingScheduleRuleVo> list=new ArrayList<>();
        for(int i=0,len=dateList.size();i<len;i++){
            Date date=dateList.get( i );
            //从map集合中根据key获取value值
            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleRuleVoMap.get( date );
            //如果当天没有排班的医生
            if(bookingScheduleRuleVo==null){
                bookingScheduleRuleVo=new BookingScheduleRuleVo();
                //就诊人数
                bookingScheduleRuleVo.setDocCount( 0 );
                //科室剩余预约数 -1表示无号
                bookingScheduleRuleVo.setAvailableNumber( -1 );
            }
            bookingScheduleRuleVo.setWorkDate( date );
            bookingScheduleRuleVo.setWorkDateMd( date );
            //计算当前预约的日期对应的星期
            String dayOfWeek = this.getDayOfWeek( new DateTime( date ) );
            bookingScheduleRuleVo.setDayOfWeek( dayOfWeek );
            //最后一页的最后一条记录为即将预约， 0正常 1即将放号 -1当天已停止放号
            if(i==len-1&&page==iPage.getPages()){
                bookingScheduleRuleVo.setStatus( 1 );
            }else{
                bookingScheduleRuleVo.setStatus( 0 );
            }
            //当天预约如果过了停号时间,不能预约
            if(i==0 && page==1){
                DateTime stopTime=this.getDateTime( new Date(),bookingRule.getStopTime() );
                if(stopTime.isBeforeNow()){
                    //停止预约
                    bookingScheduleRuleVo.setStatus( -1 );
                }
            }
            list.add( bookingScheduleRuleVo );

        }


        //可预约日期规则数据
        result.put("bookingScheduleList", list);
        result.put("total", iPage.getTotal());
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        //科室
        Department department =departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
    }

    //获取可预约分页数据
    private IPage getListDate(Integer page, Integer limit, BookingRule bookingRule) {
        //获取当天的放号时间
        DateTime releaseTime = this.getDateTime(new Date(),bookingRule.getReleaseTime());
        //预约周期
        Integer cycle = bookingRule.getCycle();
        //如果当天放号时间已经过去，预约周期从后一天开始计算，周期+1
        if(releaseTime.isBeforeNow()){
            cycle+=1;
        }
        //获取可预约的所有的日期的最后一天显示即将放号
        List<Date> dateList=new ArrayList<>();
        for(int i=0;i<cycle;i++){
            DateTime curDateTime=new DateTime().plusDays( i );
            String dataString=curDateTime.toString("yyyy-MM-dd");
            dateList.add( new DateTime(dataString).toDate() );
        }
        //因为预约周期不同，每页显示日期最多7天数据，超过7天分页
        List<Date> pageDateList=new ArrayList<>();
        int start=(page-1)*limit;
        int end=(page-1)*limit+limit;
        //如果可以显示数据小于7，进行分页
        if(end>dateList.size()){
            end=dateList.size();
        }
        //放数据
        for(int i=start;i<end;i++){
            pageDateList.add( dateList.get( i ) );
        }
        //如果可以显示的数据大于7,进行分页
        IPage<Date> iPage=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page,7,dateList.size());
        iPage.setRecords( dateList );
        return iPage;

    }
    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    //封装排班详情中的其他值：医院名称，科室名称，日期对应的星期
    private void packageSchedule(Schedule schedule) {
        //根据医院编号设置医院名称
        schedule.getParam().put( "hosname",hospitalService.getHospName( schedule.getHoscode() ) );
        //根据医院编号设置科室名称
        schedule.getParam().put( "depname",departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()) );
        //设置日期对应的星期值
        schedule.getParam().put( "dayOfWeek",this.getDayOfWeek( new DateTime(schedule.getWorkDate()) ) );

    }

    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}
