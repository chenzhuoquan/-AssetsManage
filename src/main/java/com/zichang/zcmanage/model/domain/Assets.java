package com.zichang.zcmanage.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName assets
 */
@TableName(value ="assets")
@Data
public class Assets implements Serializable {
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
     * 设备名称
     */
    private String device_name;

    /**
     * 建筑物编码
     */
    private String building_code;

    /**
     * 建筑物名称
     */
    private String building_name;

    /**
     * 楼层
     */
    private String floor;

    /**
     * 房间号
     */
    private String room_number;

    /**
     * 地点盘注
     */
    private String location_remarks;

    /**
     * 盘点备注
     */
    private String inventory_remarks;

    /**
     * 清查状态
     */
    private String clearance_status;

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