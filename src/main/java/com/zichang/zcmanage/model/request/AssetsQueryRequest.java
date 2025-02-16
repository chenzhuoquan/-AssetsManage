package com.zichang.zcmanage.model.request;


import lombok.Data;

import java.io.Serializable;

@Data
public class AssetsQueryRequest extends PageRequest implements Serializable {



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
     * 清查状态
     */
    private String clearance_status;




}
