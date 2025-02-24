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
     * 房间号
     */
    private String room_number;

    /**
     * 盘点状态
     */
    private String clearance_status;

}
