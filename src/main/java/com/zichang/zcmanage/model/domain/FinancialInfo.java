package com.zichang.zcmanage.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName financial_info
 */
@TableName(value ="financial_info")
@Data
public class FinancialInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备编码
     */
    private String device_code;

    /**
     * 计量单位
     */
    private String device_unit;

    /**
     * 单价
     */
    private BigDecimal unit_price;

    /**
     * 数量
     */
    private Integer device_num;

    /**
     * 总价
     */
    private BigDecimal total_price;

    /**
     * 附件数量
     */
    private Integer attachment_quantity;

    /**
     * 附件总价
     */
    private BigDecimal attachment_total_price;

    /**
     * 维修次数
     */
    private Integer maintenance_times;

    /**
     * 维修费用
     */
    private BigDecimal maintenance_cost;

    /**
     * 供应商码
     */
    private String supplier_code;

    /**
     * 购置日期
     */
    private String purchase_date;

    /**
     * 验收日期
     */
    private String acceptance_date;

    /**
     * 领用人工号
     */
    private String user_id;

    /**
     * 领用人名称
     */
    private String user_name;

    /**
     * 单位名称
     */
    private String unit_name;

    /**
     * 科室名称
     */
    private String department_name;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}