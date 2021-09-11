package com.fbhome.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fbhome.yygh.cmn.listener.DictListener;
import com.fbhome.yygh.cmn.mapper.DictMapper;
import com.fbhome.yygh.cmn.service.DictService;
import com.fbhome.yygh.common.result.Result;
import com.fbhome.yygh.model.cmn.Dict;
import com.fbhome.yygh.vo.cmn.DictEeVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {


    // 根据数据id查询子数据列表
    @Override
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Dict> dictList=baseMapper.selectList(wrapper);
        // 向list集合每个dict对象中设置hasChildren
        for(Dict dict:dictList){
           Long dictId= dict.getId();
           boolean isChild=this.isChildren( dictId );
           dict.setHasChildren( isChild );
        }
        return dictList;
    }
    


    // 导出数据字典接口
    @Override
    public void exportDictData(HttpServletResponse response) {
        // 设置下载信息
        response.setContentType( "application/vnd.ms-excel" );
        response.setCharacterEncoding( "utf-8" );
        String fileName="dict";
        response.setHeader( "Content-disposition","attachment;filename="+fileName+".xlsx" );
        // 查询数据库
        List<Dict> dictList=baseMapper.selectList( null );
        // Dict -->DictEeVo对象
        List<DictEeVo> dictEeVos=new ArrayList<>();
        for(Dict dict:dictList){
            DictEeVo dictEeVo=new DictEeVo();
            // 对对象的每个属性进行赋值
            BeanUtils.copyProperties(dict,dictEeVo);
            dictEeVos.add( dictEeVo );
        }
        // 调用方法向Excel进行写操作
        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class ).sheet("dict")
                    .doWrite( dictEeVos );
        } catch ( IOException e ) {
            e.printStackTrace();
        }


    }

    // 导入数据字典
    @Override
    @CacheEvict(value = "dict", allEntries=true)
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener( baseMapper )).sheet().doRead();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDictName(String dictCode, String value) {
        //如果dictCode本身为空，则直接根据value去查询
        if(StringUtils.isEmpty(dictCode)){
            //直接根据value查询
            QueryWrapper<Dict> wrapper=new QueryWrapper<>();
            wrapper.eq( "value",value );
            Dict dict = baseMapper.selectOne( wrapper );
            return dict.getName();
        }else {
            //如果dictCode不为空，则根据两个条件进行查询,根据dictCode查询dict对象，得到dict的id值
            Dict finalDict=this.getDictByDictCode( dictCode );
            Long parent_id = finalDict.getId();
            //根据parent_id和value值
            baseMapper.selectOne( new QueryWrapper<Dict>().eq( "parent_id",parent_id).eq( "value",value ) );
            return finalDict.getName();
        }
    }

    private Dict getDictByDictCode(String dictCode){
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq( "dict_code",dictCode );
        Dict codeDict=baseMapper.selectOne( wrapper );
        return codeDict;
    }
    // 判断id下面是否有子节点
    private boolean isChildren(Long id){
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq( "parent_id",id );
        Integer count=baseMapper.selectCount(wrapper);
        return count>0;
    }
}
