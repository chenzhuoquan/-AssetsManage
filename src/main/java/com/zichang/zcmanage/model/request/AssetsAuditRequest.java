package com.zichang.zcmanage.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 审核请求
 */
@Data
public class AssetsAuditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 状态：0-待审核, 1-通过, 2-拒绝
     */
    private Integer edit_status;

    /**
     * 审核人
     */
    private String auditName;


    private static final long serialVersionUID = 1L;
}