package com.fbhome.yygh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.fbhome.yygh.oss.service.FileService;

import com.fbhome.yygh.oss.utils.ConstantOssPropertiesUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileServiceImpl implements FileService {
    /**
     * 文件上传
     * @param file
     * @return
     */
    @Override
    public String upload(MultipartFile file) {
            // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
            String endpoint = ConstantOssPropertiesUtils.END_POINT;
            // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
            String accessKeyId =ConstantOssPropertiesUtils.ACCESS_KEY_ID ;
            String accessKeySecret =ConstantOssPropertiesUtils.ACCESS_KEY_ID;
            // 填写Bucket名称，例如examplebucket。
            String bucketName = ConstantOssPropertiesUtils.BUCKET;
            //上传文件流
            try {
                // 创建OSSClient实例。
                OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
                InputStream inputStream=file.getInputStream();
                String fileName=file.getOriginalFilename();
                //调用方法实现上传
                ossClient.putObject(bucketName,fileName,inputStream);
                //关闭OssClient
                ossClient.shutdown();
                //上传之后的文件路径
                String url="https://"+bucketName+"."+endpoint+"/"+fileName;
                return url;
            } catch ( IOException e ) {
                e.printStackTrace();
                return null;
            }
    }
}
