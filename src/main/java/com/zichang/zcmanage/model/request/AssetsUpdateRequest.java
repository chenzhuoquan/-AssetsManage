package com.zichang.zcmanage.model.request;

import lombok.Data;

@Data
public class AssetsUpdateRequest {

    /**
     * id
     */
    private Long id;


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
     * 盘点状态
     */
    private String clearance_status;

}
