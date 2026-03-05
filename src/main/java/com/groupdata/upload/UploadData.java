package com.groupdata.upload;

import java.util.Date;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode
public class UploadData {
    @ExcelProperty(index = 0)
    @DateTimeFormat("yyyy-MM-dd")
    private String DATE;
    @ExcelProperty(index = 1)
    private String ZHU;
    @ExcelProperty(index = 2)
    private String PLAYER_1;
    @ExcelProperty(index = 3)
    private String ZHONG_1;
    @ExcelProperty(index = 4)
    private String PLAYER_2;
    @ExcelProperty(index = 5)
    private String ZHONG_2;
    @ExcelProperty(index = 6)
    private String PLAYER_3;
    @ExcelProperty(index = 7)
    private String FAN_1;
    @ExcelProperty(index = 8)
    private String PLAYER_4;
    @ExcelProperty(index = 9)
    private String FAN_2;
    @ExcelProperty(index = 10)
    private String PLAYER_5;
    @ExcelProperty(index = 11)
    private String FAN_3;
    @ExcelProperty(index = 12)
    private String PLAYER_6;
    @ExcelProperty(index = 13)
    private String FAN_4;
    @ExcelProperty(index = 14)
    private String PLAYER_7;
    @ExcelProperty(index = 15)
    private String NEI;
    @ExcelProperty(index = 16)
    private String PLAYER_8;
    @ExcelProperty(index = 18)
    private Integer WINNER;
}
