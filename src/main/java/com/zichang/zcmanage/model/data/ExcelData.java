package com.zichang.zcmanage.model.data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExcelData {

    @ExcelProperty("设备编码")
    private String device_code;

    @ExcelProperty("设备名称")
    private String device_name;

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

    @ExcelProperty("数量")
    private Integer device_num;

    @ExcelProperty("计量单位")
    private String device_unit;

    @ExcelProperty("单价")
    private BigDecimal unit_price;

    @ExcelProperty("总价")
    private BigDecimal total_price;

    @ExcelProperty("附件数量")
    private Integer attachment_quantity;

    @ExcelProperty("附件总价")
    private BigDecimal attachment_total_price;

    @ExcelProperty("维修次数")
    private Integer maintenance_times;

    @ExcelProperty("维修费用")
    private BigDecimal maintenance_cost;

    @ExcelProperty("验收日期")
    private String acceptance_date;

    @ExcelProperty("购置日期")
    private String purchase_date;

    @ExcelProperty("领用人工号")
    private String user_id;

    @ExcelProperty("领用人名称")
    private String user_name;

    @ExcelProperty("建筑物编码")
    private String building_code;

    @ExcelProperty("建筑物名称")
    private String building_name;

    @ExcelProperty("楼层")
    private String floor;

    @ExcelProperty("房间号")
    private String room_number;

    @ExcelProperty("单位名称")
    private String unit_name;

    @ExcelProperty("科室名称")
    private String department_name;

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

    @ExcelProperty("序列号")
    private String serial_number;

    @ExcelProperty("供应商码")
    private String supplier_code;

    @ExcelProperty("报送")
    private String submit;

    @ExcelProperty("有无标签")
    private String isTag;

    @ExcelProperty("设备状态")
    private String status;

    @ExcelProperty("地点备注")
    private String location_remarks;

    @ExcelProperty("盘点备注")
    private String inventory_remarks;

    @ExcelProperty("设备照片")
    private String photo;

    @ExcelProperty("清查状态")
    private String clearance_status;

}
