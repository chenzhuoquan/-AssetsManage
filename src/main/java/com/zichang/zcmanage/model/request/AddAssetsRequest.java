package com.zichang.zcmanage.model.request;


import lombok.Data;

import java.util.List;

@Data
public class AddAssetsRequest {

    /**
     * id
     */
    private Long id;


    /**
     * 设备编码
     */
    private List<String> device_code;


    /**
     * 房间号
     */
    private String room_number;

    /**
     * 地点备注
     */
    private String location_remarks;
}
