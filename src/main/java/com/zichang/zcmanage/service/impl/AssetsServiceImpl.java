package com.zichang.zcmanage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zichang.zcmanage.common.ErrorCode;
import com.zichang.zcmanage.constant.CommonConstant;
import com.zichang.zcmanage.exception.BusinessException;
import com.zichang.zcmanage.exception.ThrowUtils;
import com.zichang.zcmanage.mapper.AssetsMapper;
import com.zichang.zcmanage.model.data.ExcelData;
import com.zichang.zcmanage.model.domain.Assets;
import com.zichang.zcmanage.model.request.AssetsQueryRequest;
import com.zichang.zcmanage.model.vo.AssetsDataVO;
import com.zichang.zcmanage.service.AssetsService;
import com.zichang.zcmanage.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
* @author lenvovo
* @description 针对表【assets(设备表)】的数据库操作Service实现
* @createDate 2024-12-02 00:02:49
*/
@Service
@Slf4j
public class AssetsServiceImpl extends ServiceImpl<AssetsMapper, Assets>
    implements AssetsService{

   /* @Override
    public void saveBatchToAssets(List<Assets> cacheAssetsList) {
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
            List<Assets> assets = cacheAssetsList.subList(i, Math.min(i + batchSize, assetListSize));

            //AOP获取代理对象
            AssetsServiceImpl assetsService = (AssetsServiceImpl) AopContext.currentProxy();
            // 异步执行插入操作
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // 调用服务层方法批量插入用户
                    assetsService.saveBatch(assets);
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
        System.out.println("asset总耗时:"+stopWatch.getTotalTimeMillis());
        //关闭线程池
        customExecutor.shutdown();
    }*/

    @Override
    public AssetsDataVO getAssetsDataByCodeId(long deviceCodeId) {

        LambdaQueryWrapper<Assets> queryWrapper = Wrappers.lambdaQuery(Assets.class)
                .eq(Assets::getDevice_code, deviceCodeId);
        //从Assets表查询
        Assets assets = this.getOne(queryWrapper);
        if(assets==null||StringUtils.isBlank(assets.getDevice_name())){
            return null;
        }
        //封装VO类
        AssetsDataVO assetsDataVO = new AssetsDataVO();
        BeanUtil.copyProperties(assets,assetsDataVO);
        return assetsDataVO;
    }

    @Override
    public Page<AssetsDataVO> getAssetsDataVO(AssetsQueryRequest assetsQueryRequest) {

        int current = assetsQueryRequest.getCurrent();
        int pageSize = assetsQueryRequest.getPageSize();
        QueryWrapper<Assets> queryWrapper = this.getQueryWrapper(assetsQueryRequest);
        Page<Assets> assetsPage = this.page(new Page<>(current, pageSize), queryWrapper);
        List<Assets> records = assetsPage.getRecords();
        if(CollUtil.isEmpty(records)){
            return new Page<>(current,pageSize);
        }

        Page<AssetsDataVO> pageVO = new Page<>(assetsPage.getCurrent(),assetsPage.getSize(),assetsPage.getTotal());
        List<AssetsDataVO> assetsDataVOS = new ArrayList<>();
        records.forEach(assets -> {
            AssetsDataVO assetsDataVO = new AssetsDataVO();
            BeanUtil.copyProperties(assets,assetsDataVO);
            assetsDataVOS.add(assetsDataVO);
        });
        pageVO.setRecords(assetsDataVOS);
        return pageVO;
    }

    @Override
    public QueryWrapper<Assets> getQueryWrapper(AssetsQueryRequest assetsQueryRequest) {
        if(assetsQueryRequest==null){
            return new QueryWrapper<>();
        }
        String deviceCode = assetsQueryRequest.getDevice_code();
        String deviceName = assetsQueryRequest.getDevice_name();
        String buildingCode = assetsQueryRequest.getBuilding_code();
        String buildingName = assetsQueryRequest.getBuilding_name();
        String floor = assetsQueryRequest.getFloor();
        String roomNumber = assetsQueryRequest.getRoom_number();
        String clearanceStatus = assetsQueryRequest.getClearance_status();
        String sortField = assetsQueryRequest.getSortField();
        String sortOrder = assetsQueryRequest.getSortOrder();

        QueryWrapper<Assets> queryWrapper=new QueryWrapper<>();
        if(StringUtils.isNotBlank(deviceCode)){
            queryWrapper.like("device_code",deviceCode);
        }

        if(StringUtils.isNotBlank(deviceName)){
            queryWrapper.like("device_name",deviceName);
        }

        if(StringUtils.isNotBlank(buildingCode)){
            queryWrapper.like("building_code",buildingCode);
        }

        if(StringUtils.isNotBlank(buildingName)){
            queryWrapper.like("building_name",buildingName);
        }

        if(StringUtils.isNotBlank(floor)){
            queryWrapper.like("floor",floor);
        }

        if(StringUtils.isNotBlank(roomNumber)){
            queryWrapper.like("room_number",roomNumber);
        }

        if(StringUtils.isNotBlank(clearanceStatus)){
            queryWrapper.like("clearance_status",clearanceStatus);
        }

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;

    }

    @Override
    public List<ExcelData> selectAllData(int pageNum,int pageSize) {
        int offSet = (pageNum - 1) * pageSize;

        return this.getBaseMapper().selectExcelData(offSet, pageSize);
    }


    /**
     * 批量添加到库中（事务、仅供内部调用）
     *
     * @param questionBankQuestions
     */

    //@Transactional(rollbackFor = Exception.class)
    public void saveBatchAssets(List<Assets> questionBankQuestions) {

        try {
            boolean result = this.saveBatch(questionBankQuestions);
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




