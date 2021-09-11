package com.fbhome.yygh.cmn.controller;

import com.fbhome.yygh.cmn.service.DictService;
import com.fbhome.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fbhome.yygh.common.result.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "数据字典的接口")
@RestController
@RequestMapping(value = "/admin/cmn/dict")
@CrossOrigin
public class DictController {

    @Autowired
    private DictService dictService;

    // 导出数据字典接口
    @GetMapping("exportData")
    public void exportDict(HttpServletResponse response) {
        dictService.exportDictData(response);
    }

    // 导入数据字典
    @PostMapping("importData")
    public Result importDict(MultipartFile file){
        dictService.importData(file);
        return Result.ok();
    }

    // 根据数据id查询子数据列表
    @ApiOperation( value="根据数据id查询子数据列表" )
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id){
        List<Dict> list=dictService.findChildData(id);
        return  Result.ok(list);
    }

    //根据dictCode和value值查询
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,@PathVariable String value){
        String dictName=dictService.getDictName(dictCode,value);
        return dictName;
    }


    //根据value值查询
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value){
        String dictName=dictService.getDictName("",value);
        return dictName;
    }
}
