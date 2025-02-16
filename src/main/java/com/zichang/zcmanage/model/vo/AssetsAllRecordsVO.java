package com.zichang.zcmanage.model.vo;


import com.zichang.zcmanage.model.domain.AssetsRecords;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
public class AssetsAllRecordsVO {

    /**
     * id
     */
    private Long id;


    /**
     * 设备编码
     */
    private List<String> device_code;


    /**
     * 登记用户
     */
    private SafeUserVO user;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 封装类转对象
     *
     * @param assetsRecordsVO
     * @return
     */
    public static AssetsRecords voToObj(AssetsAllRecordsVO assetsRecordsVO) {
        if (assetsRecordsVO == null) {
            return null;
        }
        AssetsRecords assetsRecords = new AssetsRecords();
        BeanUtils.copyProperties(assetsRecordsVO, assetsRecords);

        return assetsRecords;
    }

    /**
     * 对象转封装类
     *
     * @param assetsRecords
     * @return
     */
    public static AssetsAllRecordsVO objToVo(AssetsRecords assetsRecords) {
        if (assetsRecords == null) {
            return null;
        }
        AssetsAllRecordsVO assetsRecordsVO = new AssetsAllRecordsVO();
        BeanUtils.copyProperties(assetsRecords, assetsRecordsVO);
        return assetsRecordsVO;
    }

}
