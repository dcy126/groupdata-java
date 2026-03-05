package com.groupdata.upload;
import java.io.IOException;
import java.sql.SQLException;


import com.alibaba.excel.EasyExcel;


import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@Slf4j
public class UploadCtrl {

    @Autowired
    UploadService service;

    @RequestMapping("/upload")
    public String index(){
        return "upload";
    }

    @PostMapping("/uploadExcel")
    public String upload(MultipartFile file) throws IOException, SQLException {
        int num =service.getReadNum();
        log.info("开始解析excel！");
        ExcelReaderBuilder excel=EasyExcel.read(file.getInputStream(), UploadData.class, new UploadDataListener(service));
        ExcelReaderSheetBuilder sheet =excel.sheet("牌局记录");
        if (sheet==null)
            sheet=excel.sheet(1);
        sheet.headRowNumber(num+1).doRead();
        return "success";
    }
    
}
