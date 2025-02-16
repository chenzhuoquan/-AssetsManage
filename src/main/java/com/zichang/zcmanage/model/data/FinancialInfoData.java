package com.zichang.zcmanage.model.data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinancialInfoData {

    @ExcelProperty("设备编码")
    private String device_code;  // 外键，关联到 assets 表

    @ExcelProperty("计量单位")
    private String device_unit;

    @ExcelProperty("单价")
    private BigDecimal unit_price;

    @ExcelProperty("数量")
    private Integer device_num;

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

    @ExcelProperty("供应商码")
    private String supplier_code;

    @ExcelProperty("购置日期")
    private String purchase_date;

    @ExcelProperty("验收日期")
    private String acceptance_date;

    @ExcelProperty("领用人工号")
    private String user_id;

    @ExcelProperty("领用人名称")
    private String user_name;

    @ExcelProperty("单位名称")
    private String unit_name;

    @ExcelProperty("科室名称")
    private String department_name;
}