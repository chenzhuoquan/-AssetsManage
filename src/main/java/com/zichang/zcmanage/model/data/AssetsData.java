package com.zichang.zcmanage.model.data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class AssetsData {

    @ExcelProperty("设备编码")
    private String device_code;

    @ExcelProperty("设备名称")
    private String device_name;

    @ExcelProperty("建筑物编码")
    private String building_code;

    @ExcelProperty("建筑物名称")
    private String building_name;

    @ExcelProperty("楼层")
    private String floor;

    @ExcelProperty("房间号")
    private String room_number;

    @ExcelProperty("地点备注")
    private String location_remarks;

    @ExcelProperty("盘点备注")
    private String inventory_remarks;

    @ExcelProperty("清查状态")
    private String clearance_status;
}