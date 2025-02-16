package com.zichang.zcmanage.listen;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zichang.zcmanage.common.ErrorCode;
import com.zichang.zcmanage.exception.ThrowUtils;
import com.zichang.zcmanage.model.data.ExcelData;
import com.zichang.zcmanage.model.domain.AdditionalInfo;
import com.zichang.zcmanage.model.domain.Assets;
import com.zichang.zcmanage.model.domain.FinancialInfo;
import com.zichang.zcmanage.service.AdditionalInfoService;
import com.zichang.zcmanage.service.AssetsService;
import com.zichang.zcmanage.service.FinancialInfoService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class ExcelAllDataImport {


    @Resource
    private AssetsService assetsService;

    @Resource
    private AdditionalInfoService additionalInfoService;

    @Resource
    private FinancialInfoService financialInfoService;


    public List<ExcelData> savaBatch(List<ExcelData> excelDataList) {

        //记录由于错误而需要重新导入的数据
        List<ExcelData> needManageList = new Vector<>();

        //自定义线程池
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
                16,             //核心线程数
                30,                         //最大线程数
                60L,                        //线程空闲存活时间
                TimeUnit.SECONDS,           //存活时间单位
                new LinkedBlockingQueue<>(10000),   //阻塞队列容量
                new ThreadPoolExecutor.CallerRunsPolicy()   //拒绝策略，由调用线程处理任务
        );
        //定义批量处理的任务列表
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        int excelSize = excelDataList.size();
        int batchSize = 1000;
        ExcelAllDataImport excelAllDataImport = (ExcelAllDataImport) AopContext.currentProxy();
        for (int i = 0; i < excelSize; i += batchSize) {
            List<ExcelData> batchDataList = excelDataList.subList(i, Math.min(i + batchSize, excelSize));
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        for (ExcelData excelData : batchDataList) {
                            try {
                                excelAllDataImport.insertData(excelData);
                            } catch (Exception e) {
                                needManageList.add(excelData); // 记录失败的数据
                            }
                        }
                    }
                    , customExecutor);
            futures.add(future);
        }

        //等待所有批量处理操作执行完毕才会继续向下执行
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        customExecutor.shutdown();
        return needManageList;
    }


    @Transactional(rollbackFor = Exception.class)
    public void insertData(ExcelData excelData) {
        Assets assets = new Assets();
        FinancialInfo financialInfo = new FinancialInfo();
        AdditionalInfo additionalInfo = new AdditionalInfo();
        BeanUtils.copyProperties(excelData, assets);
        BeanUtils.copyProperties(excelData, financialInfo);
        BeanUtils.copyProperties(excelData, additionalInfo);

        //数据库插入/更新操作
        //如果是新数据,则直接插入,如果是旧数据,则更新
        try {
            LambdaQueryWrapper<Assets> assetsQueryWrapper = Wrappers.lambdaQuery(Assets.class)
                    .select(Assets::getId)
                    .eq(Assets::getDevice_code, assets.getDevice_code());
            Assets oldAssets = assetsService.getOne(assetsQueryWrapper);
            if (oldAssets != null) {
                assets.setId(oldAssets.getId());
            }
            LambdaQueryWrapper<FinancialInfo> financialInfoQueryWrapper = Wrappers.lambdaQuery(FinancialInfo.class)
                    .select(FinancialInfo::getId)
                    .eq(FinancialInfo::getDevice_code, financialInfo.getDevice_code());
            FinancialInfo oldFinancialInfo = financialInfoService.getOne(financialInfoQueryWrapper);
            if (oldFinancialInfo != null) {
                financialInfo.setId(oldFinancialInfo.getId());
            }
            LambdaQueryWrapper<AdditionalInfo> additionalInfoWrapper = Wrappers.lambdaQuery(AdditionalInfo.class)
                    .select(AdditionalInfo::getId)
                    .eq(AdditionalInfo::getDevice_code, additionalInfo.getDevice_code());
            AdditionalInfo oldAdditionalInfo = additionalInfoService.getOne(additionalInfoWrapper);
            if (oldAdditionalInfo != null) {
                additionalInfo.setId(oldAdditionalInfo.getId());
            }
            boolean result1 = assetsService.saveOrUpdate(assets);
            boolean result2 = financialInfoService.saveOrUpdate(financialInfo);
            boolean result3 = additionalInfoService.saveOrUpdate(additionalInfo);
            ThrowUtils.throwIf(!result1 || !result2 || !result3, ErrorCode.OPERATION_ERROR, "数据库操作失败");
        } catch (Exception e) {
            throw new RuntimeException(e); // 抛出异常以触发当前任务的事务回滚
        }
    }
}
