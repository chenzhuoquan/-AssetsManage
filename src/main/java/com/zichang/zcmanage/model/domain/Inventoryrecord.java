package com.zichang.zcmanage.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 盘点记录
 * @TableName inventoryrecord
 */
@TableName(value ="inventoryrecord")
@Data
public class Inventoryrecord implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 条形码id
     */
    private String assetCode;

    /**
     * 操作状态(1.成功 2.失败)
     */
    private Integer status;

    /**
     * 记录错误信息(如果状态为失败)
     */
    private String errorMessage;

    /**
     * 操作人员 
     */
    private String operName;

    /**
     * 资产所在位置
     */
    private String location;

    /**
     * 盘点时间
     */
    private Date checkTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 模块标题
     */
    private String title;

    /**
     * 日志内容
     */
    private String content;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}