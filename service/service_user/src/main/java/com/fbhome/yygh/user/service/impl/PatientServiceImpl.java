package com.fbhome.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fbhome.yygh.cmn.client.DictFeignClient;
import com.fbhome.yygh.enums.DictEnum;
import com.fbhome.yygh.model.user.Patient;
import com.fbhome.yygh.user.mapper.PatientMapper;
import com.fbhome.yygh.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
    @Autowired
    private DictFeignClient dictFeignClient;


    @Override
    public List<Patient> findAllUserId(Long userId) {
        //根据userId查询出所有就诊人信息列表
        QueryWrapper<Patient> wrapper=new QueryWrapper<>();
        wrapper.eq( "user_id",userId );
        List<Patient> patients=baseMapper.selectList( wrapper );
        //远程调用字典表,得到编码的具体中文含义
        patients.stream().forEach( item->{
            //封装字典含义
            this.packPatient(item);
        } );
        return patients;
    }

    @Override
    public Patient getPatientById(Long id) {
        return this.packPatient( baseMapper.selectById( id ) );
    }

    /**
     * 封装Patient对象里字典的中文含义,远程调用cmn服务
     * @param item
     */
    public Patient packPatient(Patient item){
        //根据证件编号获取证件类型的具体值
       String CertificatesTypeName = dictFeignClient.getName( DictEnum.CERTIFICATES_TYPE.getDictCode(),item.getCertificatesType() );
        //联系人证件类型
        String contactsCertificatesTypeString = dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),item.getContactsCertificatesType());
        //省
        String provinceString = dictFeignClient.getName(item.getProvinceCode());
        //市
        String cityString = dictFeignClient.getName(item.getCityCode());
        //区
        String districtString = dictFeignClient.getName(item.getDistrictCode());
        item.getParam().put("certificatesTypeString", CertificatesTypeName);
        item.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        item.getParam().put("provinceString", provinceString);
        item.getParam().put("cityString", cityString);
        item.getParam().put("districtString", districtString);
        item.getParam().put("fullAddress", provinceString + cityString + districtString + item.getAddress());
        return item;
    }
}
