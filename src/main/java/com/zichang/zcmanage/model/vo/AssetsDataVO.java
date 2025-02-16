package com.zichang.zcmanage.model.vo;


import lombok.Data;

@Data
public class AssetsDataVO {

    /**
     * id
     */
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




}
