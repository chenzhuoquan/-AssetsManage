/*
package com.yupi.springbootinit.listen;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.yupi.springbootinit.model.domain.AdditionalInfo;
import com.yupi.springbootinit.model.dto.data.AdditionalInfoData;
import com.yupi.springbootinit.service.AdditionalInfoService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AdditionImportListener extends AnalysisEventListener<AdditionalInfoData> {


    private final AdditionalInfoService additionalInfoService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdditionImportListener.class);

    private static final int savaBatchSize=100;

    //临时存储数据
    private List<AdditionalInfo> cacheAssetsList=new ArrayList<>(savaBatchSize);

    public AdditionImportListener(AdditionalInfoService additionalInfoService) {
        this.additionalInfoService = additionalInfoService;
    }

    @Override
    public void invoke(AdditionalInfoData additionalInfoData, AnalysisContext analysisContext) {
        AdditionalInfo additionalInfo=new AdditionalInfo();
        BeanUtils.copyProperties(additionalInfoData,additionalInfo);
        cacheAssetsList.add(additionalInfo);
      */
/*  if(cacheAssetsList.size()>=savaBatchSize){
            try {
                usageInfoService.saveBatch(cacheAssetsList);
                cacheAssetsList= ListUtils.newArrayListWithExpectedSize(savaBatchSize);
            } catch (DataIntegrityViolationException e) {
                log.error("数据库唯一键冲突或违反其他完整性约束, 错误信息: {}", e.getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "资产已存在于该库，无法重复添加");
            } catch (DataAccessException e) {
                log.error("数据库连接问题、事务问题等导致操作失败, 错误信息: {}", e.getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败");
            } catch (Exception e) {
                // 捕获其他异常，做通用处理
                log.error("添加题目到题库时发生未知错误，错误信息: {}", e.getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加题目失败");
            }

        }*//*

    }



    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        try {
            additionalInfoService.saveBatch(cacheAssetsList);
            log.info("所有数据解析完成！");
        }  catch (Throwable e) {
            // 捕获其他异常，做通用处理
            log.error("222添加题目到题库时发生未知错误，错误信息: {}", e.getMessage());
            throw new RuntimeException("批量导入失败", e);
        }

    }
}
*/
