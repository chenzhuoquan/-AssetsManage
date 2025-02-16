/*
package com.yupi.springbootinit.service.impl;

import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.data.UsageInfoData;
import com.yupi.springbootinit.model.domain.UsageInfo;
import com.yupi.springbootinit.service.UsageInfoService;
import com.yupi.springbootinit.mapper.UsageInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

*/
/**
* @author lenvovo
* @description 针对表【usage_info(使用信息表)】的数据库操作Service实现
* @createDate 2024-12-02 00:02:49
*//*

@Service
@Slf4j
public class UsageInfoServiceImpl extends ServiceImpl<UsageInfoMapper, UsageInfo>
    implements UsageInfoService{



    @Override

    public void saveBatchToUsage(List<UsageInfo> cacheAssetsList) {


        // 创建并启动秒表，用于计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 自定义线程池
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
                12,             // 核心线程数
                50,            // 最大线程数
                60L,           // 线程空闲存活时间
                TimeUnit.SECONDS,  // 存活时间单位
                new LinkedBlockingQueue<>(10000),   // 阻塞队列容量
                new ThreadPoolExecutor.CallerRunsPolicy()   // 拒绝策略，由调用线程处理任务
        );

        int batchSize = 800;
        int assetListSize = cacheAssetsList.size();

        // 存储所有异步任务的列表
        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        // 循环创建用户并分批插入
        for (int i = 0; i < assetListSize; i += batchSize) {
            List<UsageInfo> assets = cacheAssetsList.subList(i, Math.min(i + batchSize, assetListSize));


            //AOP获取代理对象
            UsageInfoServiceImpl usageInfoService = (UsageInfoServiceImpl) AopContext.currentProxy();
            // 异步执行插入操作
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {

                    // 调用服务层方法批量插入用户
                    usageInfoService.saveBatch(assets);
                } catch (Exception e) {
                    log.error("333批量导入失败", e);
                    throw new RuntimeException("333批量导入失败", e);
                }
            }, customExecutor);

            // 将异步任务添加到列表中
            futureList.add(future);
        }

        // 等待所有批量处理操作执行完毕才会继续向下执行
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();

        // 停止秒表并打印总耗时
        stopWatch.stop();
        System.out.println("usage总耗时:" + stopWatch.getTotalTimeMillis());

        // 关闭线程池
        customExecutor.shutdown();
    }

    */
/**
     * 批量添加到库中（事务、仅供内部调用）
     *
     * @param questionBankQuestions
     *//*


    //@Transactional(rollbackFor = Exception.class)
    public void saveBatchUsage(List<UsageInfo> questionBankQuestions,int size) {

        try {
            boolean result = this.saveBatch(questionBankQuestions,size);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "添加失败");
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
    }
}




*/
