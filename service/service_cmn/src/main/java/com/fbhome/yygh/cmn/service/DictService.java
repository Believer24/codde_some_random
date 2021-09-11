package com.fbhome.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fbhome.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface DictService extends IService<Dict> {
    List<Dict> findChildData(Long id);
    // 导出数据字典接口
    void exportDictData(HttpServletResponse response);

    void importData(MultipartFile file);

    String getDictName(String dictCode, String value);
}
