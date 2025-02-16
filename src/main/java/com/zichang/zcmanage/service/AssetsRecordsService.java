package com.zichang.zcmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zichang.zcmanage.model.domain.AssetsRecords;
import com.zichang.zcmanage.model.domain.User;
import com.zichang.zcmanage.model.request.*;
import com.zichang.zcmanage.model.vo.AssetsAllRecordsVO;
import com.zichang.zcmanage.model.vo.AssetsRecordsVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author lenvovo
* @description 针对表【assets(设备表)】的数据库操作Service
* @createDate 2024-12-02 00:02:49
*/

public interface AssetsRecordsService extends IService<AssetsRecords> {


    boolean addAssets(AddAssetsRequest addAssetsRequest, User loginUser);

   // Matcher validAssetsRecord(AssetsRecords assetsRecords);

    Page<AssetsRecordsVO> getQueryAssetsRecords(AssetsRecordQueryRequest assetsQueryRequest, HttpServletRequest request);

    QueryWrapper<AssetsRecords> getQueryWrapper(AssetsRecordQueryRequest assetsQueryRequest);

   // boolean editAssetsRecord(AssetsRecordUpdateRequest assetsRecordUpdateRequest);

    void validateAssetsUpdateRequest(AssetsRecordUpdateRequest assetsRecordUpdateRequest);

    Page<AssetsAllRecordsVO> getAllAssesRecords(AssetsRecordQueryRequest assetsQueryRequest);

    Page<AssetsRecordsVO> getAssetsRecordsVO(Page<AssetsRecords> assetsRecordsPage);

    void auditAssetsRecord(AssetsAuditRequest assetsAuditRequest);

    boolean auditBatchAssetsRecord(AssetsBatchAuditRequest assetsBatchAuditRequest);
}

