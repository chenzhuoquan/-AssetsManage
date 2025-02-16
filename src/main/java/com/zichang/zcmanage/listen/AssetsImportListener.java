/*
package com.yupi.springbootinit.listen;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.yupi.springbootinit.model.data.AssetsData;
import com.yupi.springbootinit.model.domain.Assets;
import com.yupi.springbootinit.service.AssetsService;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AssetsImportListener extends AnalysisEventListener<AssetsData> {


    private final AssetsService assetsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetsImportListener.class);

    private static final int savaBatchSize=1000;

    //临时存储数据
    private List<Assets> cacheAssetsList=new ArrayList<>(savaBatchSize);

    public AssetsImportListener(AssetsService assetsService) {
        this.assetsService = assetsService;
    }

    @Override
    public void invoke(AssetsData assetsData, AnalysisContext analysisContext) {
        Assets assets=new Assets();
        BeanUtils.copyProperties(assetsData,assets);
        cacheAssetsList.add(assets);
      */
/*  if(cacheAssetsList.size()>=savaBatchSize){
            try {
                assetsService.saveBatch(cacheAssetsList);
                cacheAssetsList= ListUtils.newArrayListWithExpectedSize(savaBatchSize);
            }  catch (Exception e) {
                // 捕获其他异常，做通用处理
                log.error("添加题目到题库时发生未知错误，错误信息: {}", e.getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加题目失败");
            }

        }*//*

    }



    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        try {
            cacheAssetsList.forEach(assetsService::save);
           // assetsService.saveBatch(cacheAssetsList);
            log.info("所有数据解析完成！");
        } catch (Throwable e) {
            // 捕获其他异常，做通用处理
            log.error("添加题目到题库时发生未知错误，错误信息: {}", e.getMessage());
            throw new RuntimeException("批量导入失败", e);
        }

    }
}
*/
