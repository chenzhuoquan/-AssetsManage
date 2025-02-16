package com.zichang.zcmanage.model.request;


import com.zichang.zcmanage.model.domain.Assets;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 登记接收
 */


@Data
public class ApprovalDataRequest implements Serializable {



    /**
     * 登记用户
     */
    private String userName;

    /**
     * 备注
     */
    private String  remark;

    /**
     * 登记时间
     */
    private Date dataTime;

    /**
     * 资产登记总列表
     */
    private List<Assets> assetsList;

    /**
     * 登记时间
     */
    private Date dateTime;
}
