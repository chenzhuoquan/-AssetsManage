package com.zichang.zcmanage.model.request;


import lombok.Data;

import java.util.List;

@Data
public class AssetsRecordUpdateRequest {

    /**
     * id
     */
    private Long id;


    /**
     * 设备编码
     */
    private List<String> device_code;


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
     * 状态：0-待审核, 1-通过, 2-拒绝
     */
    private Integer edit_status;

    /**
     * 审核人
     */
    private String auditName;


}
