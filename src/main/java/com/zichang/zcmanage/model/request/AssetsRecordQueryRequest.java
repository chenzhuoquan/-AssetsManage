package com.zichang.zcmanage.model.request;


import lombok.Data;

import java.io.Serializable;

@Data
public class AssetsRecordQueryRequest extends PageRequest implements Serializable {


    /**
     * id
     */
    private Long id;

    /**
     * 设备编码
     */
    private String device_code;


    /**
     * 房间号
     */
    private String room_number;

    /**
     * 审核人
     */
    private String auditName;


    /**
     * 审核状态
     */
    private Integer edit_status;


    /**
     * 用户id
     */
    private Long userId;

}
