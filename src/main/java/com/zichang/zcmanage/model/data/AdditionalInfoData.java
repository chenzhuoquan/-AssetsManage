package com.zichang.zcmanage.model.data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class AdditionalInfoData {

    @ExcelProperty("设备编码")
    private String device_code;  // 外键，关联到 assets 表

    @ExcelProperty("设备用途")
    private String device_purpose;

    @ExcelProperty("采购类型")
    private String purchase_type;

    @ExcelProperty("资产来源")
    private String asset_source;

    @ExcelProperty("国别码")
    private String country_code;

    @ExcelProperty("国别")
    private String country_name;

    @ExcelProperty("设备照片")
    private String photo;

    @ExcelProperty("原设备编码")
    private String predevice_code;

    @ExcelProperty("品牌")
    private String brand;

    @ExcelProperty("设备规格")
    private String specification;

    @ExcelProperty("设备型号")
    private String model;

    @ExcelProperty("分类编码")
    private String category_code;

    @ExcelProperty("分类说明")
    private String category_description;

    @ExcelProperty("分库类型")
    private String warehouse_type;

    @ExcelProperty("序列号")
    private String serial_number;

    @ExcelProperty("设备状态")
    private String status;

    @ExcelProperty("报送")
    private String submit;

    @ExcelProperty("有无标签")
    private String isTag;
}