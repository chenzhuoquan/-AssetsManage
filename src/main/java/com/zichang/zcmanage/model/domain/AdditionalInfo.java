package com.zichang.zcmanage.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName additional_info
 */
@TableName(value ="additional_info")
@Data
public class AdditionalInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String device_code;

    /**
     * 
     */
    private String device_purpose;

    /**
     * 
     */
    private String asset_source;

    /**
     * 
     */
    private String country_code;

    /**
     * 
     */
    private String country_name;

    /**
     * 
     */
    private String purchase_type;

    /**
     * 
     */
    private String photo;

    /**
     * 
     */
    private String predevice_code;

    /**
     * 
     */
    private String brand;

    /**
     * 
     */
    private String specification;

    /**
     * 
     */
    private String model;

    /**
     * 
     */
    private String category_code;

    /**
     * 
     */
    private String category_description;

    /**
     * 
     */
    private String warehouse_type;

    /**
     * 
     */
    private String serial_number;

    /**
     * 
     */
    private String status;

    /**
     * 
     */
    private String submit;

    /**
     * 
     */
    private String isTag;

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