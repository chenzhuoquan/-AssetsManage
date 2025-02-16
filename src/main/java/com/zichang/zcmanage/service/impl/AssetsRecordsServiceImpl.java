package com.zichang.zcmanage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zichang.zcmanage.common.ErrorCode;
import com.zichang.zcmanage.constant.CommonConstant;
import com.zichang.zcmanage.exception.BusinessException;
import com.zichang.zcmanage.exception.ThrowUtils;
import com.zichang.zcmanage.mapper.AssetsRecordsMapper;
import com.zichang.zcmanage.model.domain.Assets;
import com.zichang.zcmanage.model.domain.AssetsRecords;
import com.zichang.zcmanage.model.domain.User;
import com.zichang.zcmanage.model.enums.AssetsReviewStatusEnum;
import com.zichang.zcmanage.model.request.*;
import com.zichang.zcmanage.model.vo.AssetsAllRecordsVO;
import com.zichang.zcmanage.model.vo.AssetsRecordsVO;
import com.zichang.zcmanage.model.vo.SafeUserVO;
import com.zichang.zcmanage.service.AssetsRecordsService;
import com.zichang.zcmanage.service.AssetsService;
import com.zichang.zcmanage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author lenvovo
 * @description 针对表【assets(设备表)】的数据库操作Service实现
 * @createDate 2024-12-02 00:02:49
 */
@Service
@Slf4j
public class AssetsRecordsServiceImpl extends ServiceImpl<AssetsRecordsMapper, AssetsRecords>
        implements AssetsRecordsService {

    @Resource
    private UserService userService;

    @Resource
    private AssetsService assetsService;


    @Override
    public boolean addAssets(AddAssetsRequest addAssetsRequest, User loginUser) {
        if (addAssetsRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<String> deviceCode = addAssetsRequest.getDevice_code();
        String locationRemarks = addAssetsRequest.getLocation_remarks();
        String roomNumber = addAssetsRequest.getRoom_number();
        if (CollUtil.isEmpty(deviceCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "设备编码为空");
        }
        ThrowUtils.throwIf(StrUtil.isBlank(roomNumber),ErrorCode.PARAMS_ERROR,"房间号不能为空");
        String buildingCode=null;
        String buildingName=null;
        String floor=null;
        // 定义正则表达式，匹配建筑物编码和房间号
        if(roomNumber!=null){
            Pattern pattern = Pattern.compile("^([A-Za-z]+\\d+)-(\\d{1,2})\\d{2}$");
            Matcher matcher = pattern.matcher(roomNumber);
            if (matcher.find()) {
                // 提取建筑物编码
                buildingCode = matcher.group(1);
                buildingName = matcher.group(1);
                // 提取楼层，确保楼层为两位数
                floor = String.format("%02d", Integer.parseInt(matcher.group(2)));
            } else {
                // 如果匹配失败，可以抛出异常或返回错误信息
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "房间号格式错误");
            }
        }
        //如果是更新,则判断登记记录是否存在
        Long id = addAssetsRequest.getId();
        if (id != null) {
            AssetsRecords oldAssetsRecords = this.getById(id);
            ThrowUtils.throwIf(oldAssetsRecords == null, ErrorCode.NOT_FOUND_ERROR, "登记记录不存在");
            //仅本人可编辑
            if (!oldAssetsRecords.getUserId().equals(loginUser.getId())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
            if (oldAssetsRecords.getEdit_status().equals(AssetsReviewStatusEnum.PASS.getValue())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "该资产条目已审核，无法编辑");
            }

        }
        // 使用String.join方法连接设备编码列表
        String deviceCodeStr = String.join(",", deviceCode);
        AssetsRecords assetsRecords = new AssetsRecords();
        assetsRecords.setDevice_code(deviceCodeStr);
        assetsRecords.setLocation_remarks(locationRemarks);
        assetsRecords.setFloor(floor);
        assetsRecords.setRoom_number(roomNumber);
        assetsRecords.setBuilding_code(buildingCode);
        assetsRecords.setBuilding_name(buildingName);
        assetsRecords.setUserId(loginUser.getId());
        if (id != null) {
            assetsRecords.setId(id);
            assetsRecords.setEditTime(new Date());
        }
       return this.saveOrUpdate(assetsRecords);
    }

/*    @Override
    public Matcher validAssetsRecord(AssetsRecords assetsRecords){
        String roomNumber = assetsRecords.getRoom_number();

        // 定义正则表达式，匹配建筑物编码和房间号
        Pattern pattern = Pattern.compile("^([A-Za-z]+\\d+)-(\\d{1,2})\\d{2}$");
        Matcher matcher = pattern.matcher(roomNumber);
        if (!matcher.find()) {
            // 如果匹配失败，可以抛出异常或返回错误信息
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "房间号格式错误");
        }
        return matcher;
    }*/

    @Transactional(rollbackFor = Exception.class)
    public void addAssetsByBatch(List<AssetsRecords> assetsRecords) {
        try {
            boolean result = this.saveBatch(assetsRecords);
            if (!result) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "登记失败");
            }
        } catch (DataIntegrityViolationException e) {
            log.error("数据库唯一键冲突或违反其他完整性约束, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无法重复添加");
        } catch (DataAccessException e) {
            log.error("数据库连接问题、事务问题等导致操作失败, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败");
        } catch (Exception e) {
            // 捕获其他异常，做通用处理
            log.error("登记时发生未知错误，错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "登记失败");
        }
    }


    @Override
    public Page<AssetsRecordsVO> getQueryAssetsRecords(AssetsRecordQueryRequest assetsQueryRequest, HttpServletRequest request) {
        //获取当前用户id
        User loginUser = userService.getLoginUser(request);
        //构造查询条件
        QueryWrapper<AssetsRecords> queryWrapper = this.getQueryWrapper(assetsQueryRequest);
        queryWrapper.eq("userId", loginUser.getId());

        int pageSize = assetsQueryRequest.getPageSize();
        int current = assetsQueryRequest.getCurrent();

        Page<AssetsRecords> assetsRecordsPage = this.page(new Page<>(current, pageSize), queryWrapper);

        Page<AssetsRecordsVO> assetsRecordsVOPage = this.getAssetsRecordsVO(assetsRecordsPage);
        return assetsRecordsVOPage;
    }


/*    @Override
    public boolean editAssetsRecord(AssetsRecordUpdateRequest assetsRecordUpdateRequest) {
        //参数校验
        this.validateAssetsUpdateRequest(assetsRecordUpdateRequest);

        Long id = assetsRecordUpdateRequest.getId();
        List<String> deviceCode = assetsRecordUpdateRequest.getDevice_code();
        String deviceName = assetsRecordUpdateRequest.getDevice_name();
        String building_code = assetsRecordUpdateRequest.getBuilding_code();
        String floor = assetsRecordUpdateRequest.getFloor();
        String room_number = assetsRecordUpdateRequest.getRoom_number();
        String locationRemarks = assetsRecordUpdateRequest.getLocation_remarks();
        String buildingName = assetsRecordUpdateRequest.getBuilding_name();

        AssetsRecords records = this.getById(id);
        if (records == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < deviceCode.size(); i++) {
            if(i==deviceCode.size()-1){
                sb.append(deviceCode.get(i));
            }else {
                sb.append(deviceCode.get(i)+",");
            }
        }


        //封装实体类
        AssetsRecords assetsRecords = new AssetsRecords();
        assetsRecords.setId(id);
        assetsRecords.setDevice_code(sb.toString());
        assetsRecords.setBuilding_code(building_code);
        assetsRecords.setFloor(floor);
        assetsRecords.setRoom_number(room_number);
        assetsRecords.setLocation_remarks(locationRemarks);
        assetsRecords.setBuilding_name(buildingName);

        //构造更新条件
        return this.updateById(assetsRecords);

    }*/

    /**
     * 获取所有登记记录列表以及登记用户信息
     *
     * @param assetsQueryRequest
     * @return
     */
    @Override
    public Page<AssetsAllRecordsVO> getAllAssesRecords(AssetsRecordQueryRequest assetsQueryRequest) {
        if (assetsQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<AssetsRecords> queryWrapper = this.getQueryWrapper(assetsQueryRequest);
        int pageSize = assetsQueryRequest.getPageSize();
        int current = assetsQueryRequest.getCurrent();

        //查询原始登记列表数据
        Page<AssetsRecords> assetsRecordsPage = this.page(new Page<>(current, pageSize), queryWrapper);

        //获取登记列表数据
        List<AssetsRecords> assetsRecordsPageRecords = assetsRecordsPage.getRecords();
        if (CollUtil.isEmpty(assetsRecordsPageRecords)) {
            return new Page<>(current, pageSize);
        }

        //获取列表中所有用户id
        List<Long> userIdList = assetsRecordsPageRecords.stream()
                .map(AssetsRecords::getUserId)
                .collect(Collectors.toList());

        // 获取用户信息，并构建用户ID到用户信息的映射
        Map<Long, SafeUserVO> userMap = userService.getUserListByIds(userIdList).stream()
                .collect(Collectors.toMap(SafeUserVO::getId, user -> user));

        //构造返回前端的的分页实体类
        Page<AssetsAllRecordsVO> assetsRecordsVOPage = new Page<>(assetsRecordsPage.getCurrent(), assetsRecordsPage.getSize(), assetsRecordsPage.getTotal());

        //添加用户实体类到要返回的登记列表中
        List<AssetsAllRecordsVO> assetsAllRecordsVOS = new ArrayList<>();
        assetsRecordsPageRecords.forEach(assetsRecords -> {
            String deviceCode = assetsRecords.getDevice_code();
            String[] split = deviceCode.split(",");
            List<String> stringList = Arrays.asList(split);
            AssetsAllRecordsVO assetsAllRecordsVO = new AssetsAllRecordsVO();
            BeanUtil.copyProperties(assetsRecords, assetsAllRecordsVO);
            assetsAllRecordsVO.setDevice_code(stringList);
            Long userId = assetsRecords.getUserId();

            // 使用Optional来避免空指针异常
            Optional.ofNullable(userMap.get(userId))
                    .ifPresent(assetsAllRecordsVO::setUser);

            assetsAllRecordsVOS.add(assetsAllRecordsVO);
        });
        assetsRecordsVOPage.setRecords(assetsAllRecordsVOS);

        return assetsRecordsVOPage;
    }

    @Override
    public Page<AssetsRecordsVO> getAssetsRecordsVO(Page<AssetsRecords> assetsRecordsPage) {
        List<AssetsRecords> records = assetsRecordsPage.getRecords();
        Page<AssetsRecordsVO> assetsRecordsVOPage = new Page<>(assetsRecordsPage.getCurrent(), assetsRecordsPage.getSize(), assetsRecordsPage.getTotal());
        List<AssetsRecordsVO> assetsRecordsVOList = new ArrayList<>();
        records.forEach(record -> {
            String deviceCode = record.getDevice_code();
            String[] split = deviceCode.split(",");
            List<String> stringList = Arrays.asList(split);
            AssetsRecordsVO assetsRecordsVO = AssetsRecordsVO.objToVo(record);
            assetsRecordsVO.setDevice_code(stringList);
            assetsRecordsVOList.add(assetsRecordsVO);
        });
        assetsRecordsVOPage.setRecords(assetsRecordsVOList);
        return assetsRecordsVOPage;
    }

    // todo 加锁切面
    @Override
    public void auditAssetsRecord(AssetsAuditRequest assetsAuditRequest) {
        //1.参数校验
        Long id = assetsAuditRequest.getId();
        String auditName = assetsAuditRequest.getAuditName();
        Integer edit_status = assetsAuditRequest.getEdit_status();
        AssetsReviewStatusEnum enumByValue = AssetsReviewStatusEnum.getEnumByValue(edit_status);

        if (id == null || enumByValue == null || AssetsReviewStatusEnum.REVIEWING.equals(enumByValue)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ThrowUtils.throwIf(StrUtil.isBlank(auditName), ErrorCode.PARAMS_ERROR, "审核人不能为空");

        //2.校验资产记录状态
        AssetsRecords oldRecords = this.getById(id);
        ThrowUtils.throwIf(oldRecords == null, ErrorCode.NOT_FOUND_ERROR, "不存在该登记记录");
        Integer editStatus = oldRecords.getEdit_status();
        ThrowUtils.throwIf(editStatus.equals(edit_status), ErrorCode.OPERATION_ERROR, "审核失败,请勿重复审核");
        ThrowUtils.throwIf(editStatus.equals(AssetsReviewStatusEnum.PASS.getValue()), ErrorCode.OPERATION_ERROR, "该资产条目已审核，无法再次审核");


        if (edit_status.equals(AssetsReviewStatusEnum.REJECT.getValue())) {
            AssetsRecords assetsRecords = new AssetsRecords();
            BeanUtil.copyProperties(assetsAuditRequest, assetsRecords);
            assetsRecords.setAuditTime(new Date());
            boolean result2 = this.updateById(assetsRecords);
            ThrowUtils.throwIf(!result2, ErrorCode.OPERATION_ERROR, "审核失败");
        } else {
            //更新审核状态以及同步资产原始信息
            AssetsRecordUpdateRequest assetsRecordUpdateRequest = new AssetsRecordUpdateRequest();
            String deviceCode = oldRecords.getDevice_code();
            String[] split = deviceCode.split(",");
            List<String> device_code = Arrays.asList(split);
            BeanUtils.copyProperties(oldRecords, assetsRecordUpdateRequest, "device_code");
            assetsRecordUpdateRequest.setDevice_code(device_code);
            assetsRecordUpdateRequest.setAuditName(auditName);
            assetsRecordUpdateRequest.setEdit_status(edit_status);

            AssetsRecordsServiceImpl assetsRecordsService = (AssetsRecordsServiceImpl) AopContext.currentProxy();
            assetsRecordsService.updateLocalAssetsAndAssetsRecord(assetsRecordUpdateRequest);
        }
    }

    @Override
    public boolean auditBatchAssetsRecord(AssetsBatchAuditRequest assetsBatchAuditRequest) {
        //1.获取所有审核列表id
        List<Long> ids = assetsBatchAuditRequest.getId();
        String auditName = assetsBatchAuditRequest.getAuditName();
        Integer editStatus = assetsBatchAuditRequest.getEdit_status();
        AssetsReviewStatusEnum enumByValue = AssetsReviewStatusEnum.getEnumByValue(editStatus);

        if (CollUtil.isEmpty(ids) || enumByValue == null || AssetsReviewStatusEnum.REVIEWING.equals(enumByValue)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StrUtil.isBlank(auditName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "审核人不能为空");
        }
        //2.根据每个id，获取相应的登记记录，并更新原始资产信息
        ids.forEach(id -> {
            AssetsRecords oldassetsRecords = this.getById(id);
            if (oldassetsRecords == null) {
                log.error("不存在该登记记录");
                return;
            }
            Integer editStatus1 = oldassetsRecords.getEdit_status();
            if (editStatus1.equals(editStatus)) {
                log.error("审核失败,该条目已经审核过");
                return;
            }
            if (editStatus1.equals(AssetsReviewStatusEnum.PASS.getValue())) {
                log.error("该资产条目已审核，无法再次审核");
                return;
            }

            try {
                //更新审核状态以及同步资产原始信息
                AssetsRecordUpdateRequest assetsRecordUpdateRequest = new AssetsRecordUpdateRequest();
                String deviceCode = oldassetsRecords.getDevice_code();
                String[] split = deviceCode.split(",");
                List<String> device_code = Arrays.asList(split);
                BeanUtils.copyProperties(oldassetsRecords, assetsRecordUpdateRequest, "device_code");
                assetsRecordUpdateRequest.setDevice_code(device_code);
                assetsRecordUpdateRequest.setAuditName(auditName);
                assetsRecordUpdateRequest.setEdit_status(editStatus);
                AssetsRecordsServiceImpl assetsRecordsService = (AssetsRecordsServiceImpl) AopContext.currentProxy();
                assetsRecordsService.updateLocalAssetsAndAssetsRecord(assetsRecordUpdateRequest);
            } catch (Exception e) {
                log.error("审核失败的信息为:{}", e.getMessage());
            }

        });

        return true;
    }

    @Override
    public void validateAssetsUpdateRequest(AssetsRecordUpdateRequest assetsRecordUpdateRequest) {
        if (assetsRecordUpdateRequest == null || assetsRecordUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<String> deviceCode = assetsRecordUpdateRequest.getDevice_code();
        String building_code = assetsRecordUpdateRequest.getBuilding_code();
        String floor = assetsRecordUpdateRequest.getFloor();
        String room_number = assetsRecordUpdateRequest.getRoom_number();
        String buildingName = assetsRecordUpdateRequest.getBuilding_name();

        if (CollUtil.isEmpty(deviceCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编码不能为空");
        }
        if (StringUtils.isBlank(building_code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "建筑物编码不能为空");
        }
        if (StringUtils.isBlank(floor)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "楼层不能为空");
        }
        if (StringUtils.isBlank(room_number)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "房间号不能为空");
        }
        if (StringUtils.isBlank(buildingName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "建筑物名称不能为空");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateLocalAssetsAndAssetsRecord(AssetsRecordUpdateRequest assetsRecordUpdateRequest) {

        try {
            // 直接更新assets表
            Set<String> deviceCode = assetsRecordUpdateRequest.getDevice_code().stream().collect(Collectors.toSet());
            LambdaQueryWrapper<Assets> lambdaQueryWrapper = Wrappers.lambdaQuery(Assets.class)
                    .select(Assets::getId, Assets::getDevice_code)
                    .in(Assets::getDevice_code, deviceCode);
            List<Assets> assetsList = assetsService.list(lambdaQueryWrapper);
            if (CollUtil.isEmpty(assetsList)) {
                return;
            }
        /*    if (deviceCode.size() != assetsList.size()) {
                List<String> collect = assetsList.stream().map(Assets::getDevice_code).collect(Collectors.toList());
                collect.forEach(code -> {
                    if (deviceCode.contains(code)) {
                        deviceCode.remove(code);
                    }
                });
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "设备编码:" + deviceCode + "不存在数据库中");
            }*/

            assetsList.forEach(assets -> {
                Assets assets1 = new Assets();
                BeanUtils.copyProperties(assetsRecordUpdateRequest, assets1, "id", "device_code");
                assets1.setId(assets.getId());
                // assets1.setDevice_code(assets.getDevice_code());
                boolean result = assetsService.updateById(assets1);
                if (!result) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "审核失败");
                }

            });

            AssetsRecords assetsRecords = new AssetsRecords();
            assetsRecords.setId(assetsRecordUpdateRequest.getId());
            assetsRecords.setAuditName(assetsRecordUpdateRequest.getAuditName());
            assetsRecords.setEdit_status(assetsRecordUpdateRequest.getEdit_status());
            assetsRecords.setAuditTime(new Date());
            boolean result2 = this.updateById(assetsRecords);
            if (!result2) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作错误");
            }

        } catch (DataAccessException e) {
            log.error("数据库连接问题、事务问题等导致操作失败, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败");
        } catch (Exception e) {
            // 捕获其他异常，做通用处理
            log.error("资产审核失败，错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }


    public QueryWrapper<AssetsRecords> getQueryWrapper(AssetsRecordQueryRequest assetsQueryRequest) {
        if (assetsQueryRequest == null) {
            return new QueryWrapper<>();
        }

        Long id = assetsQueryRequest.getId();
        String device_code = assetsQueryRequest.getDevice_code();
        String room_number = assetsQueryRequest.getRoom_number();
        String auditName = assetsQueryRequest.getAuditName();
        Integer edit_status = assetsQueryRequest.getEdit_status();
        Long userId = assetsQueryRequest.getUserId();
        String sortField = assetsQueryRequest.getSortField();
        String sortOrder = assetsQueryRequest.getSortOrder();


        QueryWrapper<AssetsRecords> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(edit_status), "edit_status", edit_status);
        queryWrapper.like(StrUtil.isNotBlank(device_code), "device_code", device_code);
        queryWrapper.like(StrUtil.isNotBlank(room_number), "room_number", room_number);
        queryWrapper.like(StrUtil.isNotBlank(auditName), "auditName", auditName);

        // 排序规则
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }
}




