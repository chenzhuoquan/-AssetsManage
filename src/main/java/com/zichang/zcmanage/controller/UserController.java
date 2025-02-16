package com.zichang.zcmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zichang.zcmanage.annotation.AuthCheck;
import com.zichang.zcmanage.common.BaseResponse;
import com.zichang.zcmanage.common.DeleteRequest;
import com.zichang.zcmanage.common.ErrorCode;
import com.zichang.zcmanage.common.ResultUtils;
import com.zichang.zcmanage.constant.UserConstant;
import com.zichang.zcmanage.exception.BusinessException;
import com.zichang.zcmanage.exception.ThrowUtils;
import com.zichang.zcmanage.model.domain.User;
import com.zichang.zcmanage.model.request.*;
import com.zichang.zcmanage.model.vo.AssetsRecordsVO;
import com.zichang.zcmanage.model.vo.LoginUserVO;
import com.zichang.zcmanage.model.vo.UserListVO;
import com.zichang.zcmanage.service.AssetsRecordsService;
import com.zichang.zcmanage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private AssetsRecordsService assetsRecordsService;

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("login")
    public BaseResponse<LoginUserVO> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyEmpty(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码不能为空");
        }
        LoginUserVO loginUserVO = userService.login(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }


    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @param request
     * @return
     */
    @PostMapping("register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String userName = userRegisterRequest.getUserName();
        if (StringUtils.isAnyEmpty(userAccount, userPassword, checkPassword, userName)) {
            return null;
        }
        long result = userService.register(userAccount, userPassword, checkPassword, userName);
        return ResultUtils.success(result);
    }

    /**
     * 退出登录
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        boolean result = userService.logout(request);
        return ResultUtils.success(result);
    }


    /**
     * 用户查询个人登记记录
     *
     * @param assetsQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/query")
    public BaseResponse<Page<AssetsRecordsVO>> queryAssetsRecords(@RequestBody AssetsRecordQueryRequest assetsQueryRequest, HttpServletRequest request) {
        Page<AssetsRecordsVO> queryAssetsRecords = assetsRecordsService.getQueryAssetsRecords(assetsQueryRequest, request);
        return ResultUtils.success(queryAssetsRecords);
    }


    /**
     * 分页获取用户列表(管理员)
     * @return
     */
    @PostMapping("getUser/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserListVO>> getUserList(@RequestBody UserQueryRequest userQueryRequest) {
        Page<UserListVO> userVOList = userService.getSafeUserList(userQueryRequest);
        return ResultUtils.success(userVOList);
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }



}
