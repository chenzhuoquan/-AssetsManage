package com.zichang.zcmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zichang.zcmanage.model.data.ExcelData;
import com.zichang.zcmanage.model.domain.Assets;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zichang.zcmanage.model.request.AssetsQueryRequest;
import com.zichang.zcmanage.model.vo.AssetsDataVO;

import java.util.List;

/**
* @author lenvovo
* @description 针对表【assets(设备表)】的数据库操作Service
* @createDate 2024-12-02 00:02:49
*/

public interface AssetsService extends IService<Assets> {


   // void saveBatchToAssets(List<Assets> cacheAssetsList);


    /**
     * 根据设备编号获取设备信息
     * @param deviceCodeId
     * @return
     */
    AssetsDataVO getAssetsDataByCodeId(long deviceCodeId);


    /**
     * 分页获取资产信息(管理员)
     * @param assetsQueryRequest
     * @return
     */
    Page<AssetsDataVO> getAssetsDataVO(AssetsQueryRequest assetsQueryRequest);

    QueryWrapper<Assets> getQueryWrapper(AssetsQueryRequest assetsQueryRequest);

    List<ExcelData> selectAllData(int pageNum,int pageSize);
}

