package com.zichang.zcmanage.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zichang.zcmanage.annotation.AuthCheck;
import com.zichang.zcmanage.annotation.MyLog;
import com.zichang.zcmanage.common.BaseResponse;
import com.zichang.zcmanage.common.ErrorCode;
import com.zichang.zcmanage.common.ResultUtils;
import com.zichang.zcmanage.constant.InventoryConstant;
import com.zichang.zcmanage.constant.UserConstant;
import com.zichang.zcmanage.exception.BusinessException;
import com.zichang.zcmanage.model.domain.Assets;
import com.zichang.zcmanage.model.domain.AssetsRecords;
import com.zichang.zcmanage.model.domain.User;
import com.zichang.zcmanage.model.request.*;
import com.zichang.zcmanage.model.vo.AssetsAllRecordsVO;
import com.zichang.zcmanage.model.vo.AssetsDataVO;
import com.zichang.zcmanage.model.vo.AssetsRecordsVO;
import com.zichang.zcmanage.service.AssetsRecordsService;
import com.zichang.zcmanage.service.AssetsService;
import com.zichang.zcmanage.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/assets")
public class AssetsController {


    @Resource
    private AssetsRecordsService assetsRecordsService;

    @Resource
    private UserService userService;

    @Resource
    private AssetsService assetsService;

    //构建本地缓存
    private final Cache<String, Boolean> LOCAL_CACHE =
            Caffeine.newBuilder().initialCapacity(1024)
                    .maximumSize(10000L)
                    // 缓存 5 秒钟移除
                    .expireAfterWrite(5, TimeUnit.SECONDS)
                    .build();

    /**
     * 登记资产信息、编辑修改资产信息
     * @param addAssetsRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addAssets(@RequestBody AddAssetsRequest addAssetsRequest, HttpServletRequest request) {
        if (addAssetsRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        String jsonStr = JSONUtil.toJsonStr(addAssetsRequest);
        String hasKey = DigestUtils.md5DigestAsHex(jsonStr.getBytes());
        String cacheKey = String.format("ziChang:addAssets:%s:%s", loginUser.getId(),hasKey);
        Boolean isLocked = LOCAL_CACHE.getIfPresent(cacheKey);
        if (isLocked != null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请勿重复提交");
        }
        boolean result = assetsRecordsService.addAssets(addAssetsRequest, loginUser);
        LOCAL_CACHE.put(cacheKey, Boolean.TRUE);
        return ResultUtils.success(result);
    }


    /**
     * 根据设备编码获取相关设备信息
     * @param deviceCodeId
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<AssetsDataVO> getAssetsVOById(long deviceCodeId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        AssetsDataVO assetsDataVO = assetsService.getAssetsDataByCodeId(deviceCodeId);
        if (assetsDataVO == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "资产不存在");
        }
        return ResultUtils.success(assetsDataVO);
    }

    /**
     * 根据id获取用户登记记录
     * @param assetsRecordId
     * @return
     */
    @GetMapping("/getRecord/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<AssetsRecordsVO> getAssetsRecordsVOById(long assetsRecordId) {
        if (assetsRecordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AssetsRecords records = assetsRecordsService.getById(assetsRecordId);
        String deviceCode = records.getDevice_code();
        String[] split = deviceCode.split(",");
        List<String> codeList = Arrays.asList(split);
        AssetsRecordsVO assetsRecordsVO = new AssetsRecordsVO();
        BeanUtil.copyProperties(records, assetsRecordsVO);
        assetsRecordsVO.setDevice_code(codeList);
        return ResultUtils.success(assetsRecordsVO);
    }


   /* @PostMapping("/edit")
    public BaseResponse<Boolean> editAssetsRecord(@RequestBody AssetsRecordUpdateRequest assetsRecordUpdateRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (assetsRecordUpdateRequest == null || assetsRecordUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = assetsRecordUpdateRequest.getId();
        LambdaQueryWrapper<AssetsRecords> queryWrapper = Wrappers.lambdaQuery(AssetsRecords.class)
                .select(AssetsRecords::getEdit_status)
                .eq(AssetsRecords::getId, id);
        AssetsRecords one = assetsRecordsService.getOne(queryWrapper);
        ThrowUtils.throwIf(one == null, ErrorCode.NOT_FOUND_ERROR, "编辑数据不存在");
        if (one.getEdit_status() == 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该资产条目已审核，无法编辑");
        }
        //仅本人可编辑
        if(!one.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

        AssetsRecords assetsRecords = new AssetsRecords();
        List<String> deviceCode = assetsRecordUpdateRequest.getDevice_code();
        // 使用String.join方法连接设备编码列表
        String deviceCodeStr = String.join(",", deviceCode);
        BeanUtil.copyProperties(assetsRecordUpdateRequest, assetsRecords, "id");
        assetsRecords.setId(id);
        assetsRecords.setDevice_code(deviceCodeStr);
        assetsRecords.setEditTime(new Date());
        boolean result = assetsRecordsService.updateById(assetsRecords);
        return ResultUtils.success(result);
    }*/


    /**
     * 分页获取所有登记记录(仅管理员可用)
     *
     * @param assetsQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AssetsAllRecordsVO>> assetsRecordsList(@RequestBody AssetsRecordQueryRequest assetsQueryRequest) {
        if (assetsQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<AssetsAllRecordsVO> assetsRecordsVOPage = assetsRecordsService.getAllAssesRecords(assetsQueryRequest);
        return ResultUtils.success(assetsRecordsVOPage);
    }

    /**
     * 审核登记信息(管理员)
     * 审核通过的信息不能再审核（通过/拒绝）
     *
     * @param assetsAuditRequest
     * @return
     */
    @PostMapping("/audit")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> auditAssetsRecord(@RequestBody AssetsAuditRequest assetsAuditRequest) {
        if (assetsAuditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        assetsRecordsService.auditAssetsRecord(assetsAuditRequest);
        return ResultUtils.success(true);
    }

    /**
     * 批量审核登记信息(管理员)
     *
     * @param assetsBatchAuditRequest
     * @return
     */
    @PostMapping("/auditBatch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> auditBatchAssetsRecord(@RequestBody AssetsBatchAuditRequest assetsBatchAuditRequest) {
        if (assetsBatchAuditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = assetsRecordsService.auditBatchAssetsRecord(assetsBatchAuditRequest);
        return ResultUtils.success(result);
    }

    /**
     * 分页获取资产信息(管理员)
     *
     * @param assetsQueryRequest
     * @return
     */
    @PostMapping("/getAssets/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AssetsDataVO>> getAssetsDataVO(@RequestBody AssetsQueryRequest assetsQueryRequest) {
        if (assetsQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<AssetsDataVO> assetsDataVOPage = assetsService.getAssetsDataVO(assetsQueryRequest);
        return ResultUtils.success(assetsDataVOPage);
    }

    /**
     * 更改资产信息(管理员)
     *
     * @param assetsUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAssetsById(@RequestBody AssetsUpdateRequest assetsUpdateRequest) {
        if (assetsUpdateRequest == null || assetsUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = assetsUpdateRequest.getId();
        Assets one = assetsService.getById(id);
        if (one == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该资产条目不存在");
        }
        Assets assets = new Assets();
        BeanUtil.copyProperties(assetsUpdateRequest, assets);
        boolean result = assetsService.updateById(assets);
        return ResultUtils.success(result);
    }


    /**
     * 机器人盘点
     *
     * @param deviceCode
     * @return
     */
    @GetMapping("/inventory")
    @MyLog(title = "盘点模块", content = "机器人盘点")
    //@AuthCheck(mustRole = UserConstant)
    public BaseResponse<Boolean> inventory(@RequestParam String deviceCode) {
        if (!deviceCode.matches("\\d+")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "清点失败,传递参数格式有误");
        }
        LambdaQueryWrapper<Assets> assetsLambdaQueryWrapper = Wrappers.lambdaQuery(Assets.class)
                .eq(Assets::getDevice_code, deviceCode);
        Assets one = assetsService.getOne(assetsLambdaQueryWrapper);
        //检查资产是否存在
        if (one == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "清点失败,未找到此资产条目");
        }
        //如果已经盘点，直接返回
        if (InventoryConstant.INVENTORY.equals(one.getInventory_remarks())) {
            return ResultUtils.success(true);
        }
        //更新盘点状态
        one.setClearance_status(InventoryConstant.INVENTORY);
        boolean result = assetsService.updateById(one);
        if (!result) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "更新盘点状态失败");
        }
        return ResultUtils.success(result);
    }


}
