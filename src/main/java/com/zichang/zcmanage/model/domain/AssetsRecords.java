package com.zichang.zcmanage.model.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class AssetsRecords {

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
     * 用户id
     */
    private Long userId;

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
     * 地点备注
     */
    private String location_remarks;

    /**
     * 审核人
     */
    private String auditName;

    /**
     * 审核状态
     */
    private Integer edit_status;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

}
