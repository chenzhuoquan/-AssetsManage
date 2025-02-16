package com.zichang.zcmanage.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量审核请求
 */
@Data
public class AssetsBatchAuditRequest implements Serializable {

    /**
     * id列表
     */
    private List<Long> id;

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