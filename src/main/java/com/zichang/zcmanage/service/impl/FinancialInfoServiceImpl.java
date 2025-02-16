package com.zichang.zcmanage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zichang.zcmanage.model.domain.FinancialInfo;
import com.zichang.zcmanage.service.FinancialInfoService;
import com.zichang.zcmanage.mapper.FinancialInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author lenvovo
* @description 针对表【financial_info(资产表)】的数据库操作Service实现
* @createDate 2024-12-02 00:02:49
*/
@Service
public class FinancialInfoServiceImpl extends ServiceImpl<FinancialInfoMapper, FinancialInfo>
    implements FinancialInfoService{


    /*public void saveBatchToAssets(List<FinancialInfo> cacheAssetsList) {



        // 创建并启动秒表，用于计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


        //自定义线程池
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
                12,             //核心线程数
                50,                         //最大线程数
                60L,                        //线程空闲存活时间
                TimeUnit.SECONDS,           //存活时间单位
                new LinkedBlockingQueue<>(10000),   //阻塞队列容量
                new ThreadPoolExecutor.CallerRunsPolicy()   //拒绝策略，由调用线程处理任务
        );

        int batchSize = 800;

        int assetListSize=cacheAssetsList.size();



        // 存储所有异步任务的列表
        List<CompletableFuture<Void>> futureList = new ArrayList<>();



        // 循环创建用户并分批插入
        for (int i = 0; i < assetListSize; i+=batchSize) {
            List<FinancialInfo> assets = cacheAssetsList.subList(i, Math.min(i + batchSize, assetListSize));

            //AOP获取代理对象
            FinancialInfoServiceImpl financialInfoService = (FinancialInfoServiceImpl) AopContext.currentProxy();
            // 异步执行插入操作
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                try {

                    // 调用服务层方法批量插入用户
                    financialInfoService.saveBatch(assets);
                } catch (Exception e) {
                    log.error("333批量导入失败", e);

                    throw new RuntimeException("333批量导入失败", e);
                }
            },customExecutor);

            // 将异步任务添加到列表中
            futureList.add(future);
        }
        // 等待所有批量处理操作执行完毕才会继续向下执行
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();


        // 停止秒表并打印总耗时
        stopWatch.stop();
        System.out.println("fin总耗时:"+stopWatch.getTotalTimeMillis());

        //关闭线程池
        customExecutor.shutdown();
    }*/
}




