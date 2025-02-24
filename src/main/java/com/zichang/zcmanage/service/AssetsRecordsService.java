package com.zichang.zcmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zichang.zcmanage.model.domain.AssetsRecords;
import com.zichang.zcmanage.model.domain.User;
import com.zichang.zcmanage.model.request.*;
import com.zichang.zcmanage.model.vo.AssetsAllRecordsVO;
import com.zichang.zcmanage.model.vo.AssetsRecordsVO;
import com.zichang.zcmanage.model.vo.LocationInfo;

import javax.servlet.http.HttpServletRequest;

/**
* @author lenvovo
* @description 针对表【assets(设备表)】的数据库操作Service
* @createDate 2024-12-02 00:02:49
*/

public interface AssetsRecordsService extends IService<AssetsRecords> {


    /**
     * 登记/编辑资产信息
     * @param addAssetsRequest
     * @param loginUser
     * @return
     */
    boolean addAssets(AddAssetsRequest addAssetsRequest, User loginUser);

   // Matcher validAssetsRecord(AssetsRecords assetsRecords);

    Page<AssetsRecordsVO> getQueryAssetsRecords(AssetsRecordQueryRequest assetsQueryRequest, HttpServletRequest request);

    LocationInfo fillLocation(String roomNumber);

    QueryWrapper<AssetsRecords> getQueryWrapper(AssetsRecordQueryRequest assetsQueryRequest);

   // boolean editAssetsRecord(AssetsRecordUpdateRequest assetsRecordUpdateRequest);

    void validateAssetsUpdateRequest(AssetsRecordUpdateRequest assetsRecordUpdateRequest);

    /**
     * 分页获取所有登记记录(仅管理员可用)
     * @param assetsQueryRequest
     * @return
     */
    Page<AssetsAllRecordsVO> getAllAssesRecords(AssetsRecordQueryRequest assetsQueryRequest);

    Page<AssetsRecordsVO> getAssetsRecordsVO(Page<AssetsRecords> assetsRecordsPage);

    /**
     * 审核登记信息(管理员)
     * 审核通过的信息不能再审核（通过/拒绝）
     * @param assetsAuditRequest
     */
    void auditAssetsRecord(AssetsAuditRequest assetsAuditRequest);

    /**
     * 批量审核登记信息(管理员)
     * @param assetsBatchAuditRequest
     */
    boolean auditBatchAssetsRecord(AssetsBatchAuditRequest assetsBatchAuditRequest);
}

