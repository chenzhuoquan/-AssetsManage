package com.zichang.zcmanage.listen;

import cn.hutool.core.date.StopWatch;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExcelAllDataImport {

    @Resource
    private AssetsService assetsService;
    @Resource
    private AdditionalInfoService additionalInfoService;
    @Resource
    private FinancialInfoService financialInfoService;

    @Resource
    private PlatformTransactionManager transactionManager;

    // 错误数据收集队列（线程安全）
    private final Queue<ExcelData> errorQueue = new ConcurrentLinkedQueue<>();

    // 自定义线程池（IO密集型任务）
   /* private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            8,                              // 核心线程数
            16,                             // 最大线程数
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),// 队列容量
            new ThreadPoolExecutor.CallerRunsPolicy()
    );*/
    @Autowired
    @Qualifier("globalThreadPool")
    private Executor executor;

    /**
     * 主入口：处理所有数据并返回错误列表
     */
    public List<ExcelData> savaBatch(List<ExcelData> allData) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 分批次处理（2000条/批）
        int batchSize = 2000;
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < allData.size(); i += batchSize) {
            List<ExcelData> batch = allData.subList(i, Math.min(i + batchSize, allData.size()));
            futures.add(processBatchAsync(batch).exceptionally(e -> {
                log.error("处理批次失败", e);
                return null;
            }));
        }
        // 等待所有批次完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        stopWatch.stop();
        System.out.println("总耗时: " + stopWatch.getTotalTimeMillis() + "ms");
        return new ArrayList<>(errorQueue);
    }

    /**
     * 异步处理单个批次
     */
    private CompletableFuture<Void> processBatchAsync(List<ExcelData> batch) {
        return CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " 开始处理批次");
            // 1. 数据校验和错误隔离
            Map<Boolean, List<ExcelData>> partitioned = batch.stream()
                    .collect(Collectors.partitioningBy(this::validateData));
            // 记录错误数据
            errorQueue.addAll(partitioned.get(false));
            // 处理有效数据
            List<ExcelData> validData = partitioned.get(true);
            if (!validData.isEmpty()) {
                processValidData(validData);
            }
        }, executor);
    }

    /**
     * 处理有效数据（多线程并发）
     */
    private void processValidData(List<ExcelData> validData) {
        // 进一步拆分子批次（500条/子批）
        int subBatchSize = 500;
        List<CompletableFuture<Void>> subFutures = new ArrayList<>();
        for (int i = 0; i < validData.size(); i += subBatchSize) {
            List<ExcelData> subBatch = validData.subList(i, Math.min(i + subBatchSize, validData.size()));
            subFutures.add(CompletableFuture.runAsync(() -> {
                System.out.println(Thread.currentThread().getName() + " 开始处理批次");
                this.batchInsertOrUpdate(subBatch);
            }, executor));
        }
        CompletableFuture.allOf(subFutures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * 批量插入/更新（事务管理）
     */
    public void batchInsertOrUpdate(List<ExcelData> subBatch) {

        Set<String> deviceCodeList = subBatch.stream().map(ExcelData::getDevice_code).collect(Collectors.toSet());


        LambdaQueryWrapper<Assets> assetsLambdaQueryWrapper = Wrappers.lambdaQuery(Assets.class).
                select(Assets::getDevice_code, Assets::getId).
                in(Assets::getDevice_code, deviceCodeList);
        LambdaQueryWrapper<FinancialInfo> financialInfoLambdaQueryWrapper = Wrappers.lambdaQuery(FinancialInfo.class).
                select(FinancialInfo::getDevice_code, FinancialInfo::getId).
                in(FinancialInfo::getDevice_code, deviceCodeList);
        LambdaQueryWrapper<AdditionalInfo> additionalInfoLambdaQueryWrapper = Wrappers.lambdaQuery(AdditionalInfo.class).
                select(AdditionalInfo::getDevice_code, AdditionalInfo::getId).
                in(AdditionalInfo::getDevice_code, deviceCodeList);

        Map<String, Long> assetsMap = assetsService.list(assetsLambdaQueryWrapper).stream().collect(Collectors.toMap(Assets::getDevice_code, Assets::getId));

        Map<String, Long> financialMap = financialInfoService.list(financialInfoLambdaQueryWrapper).stream().collect(Collectors.toMap(FinancialInfo::getDevice_code, FinancialInfo::getId));

        Map<String, Long> additionalMap = additionalInfoService.list(additionalInfoLambdaQueryWrapper).stream().collect(Collectors.toMap(AdditionalInfo::getDevice_code, AdditionalInfo::getId));

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            try {
                // 1. 批量转换实体
                List<Assets> assetsList = new ArrayList<>();
                List<FinancialInfo> financialList = new ArrayList<>();
                List<AdditionalInfo> additionalList = new ArrayList<>();

                for (ExcelData data : subBatch) {
                    Assets asset = new Assets();
                    FinancialInfo financial = new FinancialInfo();
                    AdditionalInfo additional = new AdditionalInfo();
                    BeanUtils.copyProperties(data, asset);
                    BeanUtils.copyProperties(data, financial);
                    BeanUtils.copyProperties(data, additional);

                    // 1.1 处理主键
                    Long assetsId = assetsMap.get(data.getDevice_code());
                    if (assetsId != null) {
                        asset.setId(assetsId);
                    }
                    Long financialId = financialMap.get(data.getDevice_code());
                    if (financialId != null) {
                        financial.setId(financialId);
                    }
                    Long additionalId = additionalMap.get(data.getDevice_code());
                    if (additionalId != null) {
                        additional.setId(additionalId);
                    }

                    assetsList.add(asset);
                    financialList.add(financial);
                    additionalList.add(additional);
                }
                // 2. 批量保存或更新
                boolean result1 = assetsService.saveOrUpdateBatch(assetsList);
                boolean result2 = financialInfoService.saveOrUpdateBatch(financialList);
                boolean result3 = additionalInfoService.saveOrUpdateBatch(additionalList);
                ThrowUtils.throwIf(!result1 || !result2 || !result3, ErrorCode.OPERATION_ERROR, "数据库插入异常");
                return Boolean.TRUE;
            } catch (Exception e) {
                errorQueue.addAll(subBatch); // 子批次失败时记录全部
                status.setRollbackOnly();
                throw new RuntimeException("子批次处理失败:" + e.getMessage());
            }
        });

    }

    /**
     * 数据校验逻辑
     */
    private boolean validateData(ExcelData data) {
        try {
            // 示例校验规则
            return data.getDevice_code() != null
                    && !data.getDevice_code().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }


}