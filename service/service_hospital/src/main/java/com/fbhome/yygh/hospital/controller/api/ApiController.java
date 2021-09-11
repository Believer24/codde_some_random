package com.fbhome.yygh.hospital.controller.api;

import com.fbhome.yygh.common.exception.YyghException;
import com.fbhome.yygh.common.helper.HttpRequestHelper;
import com.fbhome.yygh.common.result.Result;
import com.fbhome.yygh.common.result.ResultCodeEnum;
import com.fbhome.yygh.hospital.service.DepartmentService;
import com.fbhome.yygh.hospital.service.HospitalService;
import com.fbhome.yygh.hospital.service.HospitalSetService;
import com.fbhome.yygh.hospital.service.ScheduleService;
import com.fbhome.yygh.hospital.utils.MD5;
import com.fbhome.yygh.model.hosp.Department;
import com.fbhome.yygh.model.hosp.Hospital;
import com.fbhome.yygh.model.hosp.Schedule;
import com.fbhome.yygh.vo.hosp.DepartmentQueryVo;
import com.fbhome.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp/")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private HospitalSetService hospitalSetService;
    @Autowired
    private ScheduleService scheduleService;
    // 查询医院
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        // 获取到传递过来的医院的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap( requestMap );
        String hoscode = (String) paramMap.get( "hoscode" );
        // 1.获取医院系统中传递过来的签名,签名是进行了MD5的加密
        String hospitalSign = (String) paramMap.get( "sign" );
        String signKey=hospitalSetService.getSignKey(hoscode);
        // 3.数据库查出来的签名进行MD5加密
        String signMD5 = MD5.encrypt( signKey );
        // 4.判断签名是否一致
        if(!hospitalSign.equals( signMD5 )){
            throw new YyghException( ResultCodeEnum.SIGN_ERROR );
        }
        //调用Service里的方法实现根据医院编号查询
        Hospital hospital=hospitalService.getByHosCode(hoscode);
        return Result.ok();
    }

    // 上传医院接口
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request) {
        // 获取到传递过来的医院的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap( requestMap );

        // 1.获取医院系统中传递过来的签名,签名是进行了MD5的加密
        String hospitalSign = (String) paramMap.get( "sign" );
        // 2.根据传递过来的医院编号，去查询数据库，查询signKey
        String hoscode = (String) paramMap.get( "hoscode" );
        String signKey=hospitalSetService.getSignKey(hoscode);
        // 3.数据库查出来的签名进行MD5加密
        String signMD5 = MD5.encrypt( signKey );
        // 4.判断签名是否一致
        if(!hospitalSign.equals( signMD5 )){
            throw new YyghException( ResultCodeEnum.SIGN_ERROR );
        }
        //传输过程中"+"转换为了" "，因此要转换回来
        String logoData = (String) paramMap.get( "logoData" );
        logoData=logoData.replaceAll( " ","+" );
        paramMap.put( "logoData",logoData );
        // 调用Service的方法
        hospitalService.save(paramMap);
        return Result.ok();

    }
    //上传科室接口
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        // 获取到传递过来的科室的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap( requestMap );
        // 1.获取医院系统中传递过来的签名,签名是进行了MD5的加密
        String hospitalSign = (String) paramMap.get( "sign" );
        // 2.根据传递过来的医院编号，去查询数据库，查询signKey
        String hoscode = (String) paramMap.get( "hoscode" );
        String signKey=hospitalSetService.getSignKey(hoscode);
        // 3.数据库查出来的签名进行MD5加密
        String signMD5 = MD5.encrypt( signKey );
        // 4.判断签名是否一致
        if(!hospitalSign.equals( signMD5 )){
            throw new YyghException( ResultCodeEnum.SIGN_ERROR );
        }

        //调用service方法
        departmentService.save(paramMap);
        return Result.ok();
    }
    // 查询科室接口
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        // 获取到传递过来的科室的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap( requestMap );
        // 获取医院编号 判断当前页
        String hoscode= (String) paramMap.get( "hoscode" );
        //当前页和每页记录数
        int page=StringUtils.isEmpty( paramMap.get( "page" ) )
                ?1:Integer.parseInt( (String) paramMap.get( "page" ) );
        //获取每页记录数
        int limit=StringUtils.isEmpty( paramMap.get( "limit" ) )
                ?5:Integer.parseInt( (String) paramMap.get( "limit" ) );
        // TODO 签名校验

        DepartmentQueryVo departmentQueryVo=new DepartmentQueryVo();
        departmentQueryVo.setHoscode( hoscode );
        //调用Service方法
        Page<Department> pageModel=departmentService.findPageDepartment(page,limit,departmentQueryVo);
        return Result.ok(pageModel);
    }
    //删除科室接口
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        //获取传递过来的科室信息
        Map<String,String[]> requestMap=request.getParameterMap();
        Map<String,Object> paramMap=HttpRequestHelper.switchMap( requestMap );
        //医院编号 和 科室编号
        String hoscode= (String) paramMap.get( "hoscode" );
        String depcode= (String) paramMap.get( "depcode" );
        // TODO 签名校验

        departmentService.remove(hoscode,depcode);
        return Result.ok();
    }
    // 查询排班
    @PostMapping("schedule/list")
    public Result findschedule(HttpServletRequest request){
        //获取传递过来科室信息
        Map<String,String[]> requestMap=request.getParameterMap();
        Map<String,Object> paramMap=HttpRequestHelper.switchMap( requestMap );
        // 获取医院编号 判断当前页
        String hoscode= (String) paramMap.get( "hoscode" );
        // 获取科室编号 判断当前页
        String depcode= (String) paramMap.get( "depcode" );
        //当前页和每页记录数
        int page=StringUtils.isEmpty( paramMap.get( "page" ) )
                ?1:Integer.parseInt( (String) paramMap.get( "page" ) );
        //获取每页记录数
        int limit=StringUtils.isEmpty( paramMap.get( "limit" ) )
                ?5:Integer.parseInt( (String) paramMap.get( "limit" ) );
        // TODO 签名校验

        ScheduleQueryVo scheduleQueryVo=new ScheduleQueryVo();
        scheduleQueryVo.setHoscode( hoscode );
        scheduleQueryVo.setHoscode( depcode );
        //调用Service方法
        Page<Schedule> pageModel=scheduleService.findPageSchedule(page,limit,scheduleQueryVo);
        return Result.ok(pageModel);
    }
    //删除排班
    @PostMapping("schedule/remove")
    public Result remove(HttpServletRequest request){
        //获取传递过来科室信息
        Map<String,String[]> requestMap=request.getParameterMap();
        Map<String,Object> paramMap=HttpRequestHelper.switchMap( requestMap );
        //获取医院编号和排班编号
        String hoscode=(String)paramMap.get("hoscode");
        //获取排班编号
        String hosScheduleId=(String)paramMap.get("hosScheduleId");

        //TODO 签名校验

        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();
    }
    //上传排版接口
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        //获取传递过来科室信息
        Map<String,String[]> requestMap=request.getParameterMap();
        Map<String,Object> paramMap=HttpRequestHelper.switchMap( requestMap );
        //TODO 签名校验

        scheduleService.save(paramMap);
        return  Result.ok();
    }



}
