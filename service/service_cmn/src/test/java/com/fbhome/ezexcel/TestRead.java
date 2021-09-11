package com.fbhome.ezexcel;

import com.alibaba.excel.EasyExcel;

public class TestRead {
    public static void main(String[] args) {
        // 要读取的文件路径
        String fileName="H:\\excel\\01.xlsx";
        // 调用方法实现读取操作
        EasyExcel.read(fileName,UserData.class,new ExcelListener()).sheet().doRead();
    }
}
