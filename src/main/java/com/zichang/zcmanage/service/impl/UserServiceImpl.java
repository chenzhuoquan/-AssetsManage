package com.zichang.zcmanage.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zichang.zcmanage.common.ErrorCode;
import com.zichang.zcmanage.constant.UserConstant;
import com.zichang.zcmanage.exception.BusinessException;
import com.zichang.zcmanage.mapper.UserMapper;
import com.zichang.zcmanage.model.domain.User;
import com.zichang.zcmanage.model.request.UserQueryRequest;
import com.zichang.zcmanage.model.vo.LoginUserVO;
import com.zichang.zcmanage.model.vo.SafeUserVO;
import com.zichang.zcmanage.model.vo.UserListVO;
import com.zichang.zcmanage.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lenvovo
 * @description 针对表【additional_info(额外信息表)】的数据库操作Service实现
 * @createDate 2024-12-02 00:02:48
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "zichang";


    /**
     * 登录
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    @Override
    public LoginUserVO login(String userAccount, String userPassword, HttpServletRequest request) {
        //1.参数校验
        if (StringUtils.isAnyEmpty(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码为空");
        }
        //2.对账号密码格式进行约束
        if (userAccount.length() < 7) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        //对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //构造查询条件
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getUserAccount, userAccount)
                .eq(User::getUserPassword, encryptPassword);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    @Override
    public long register(String userAccount, String userPassword, String checkPassword, String userName) {

        //1.参数校验
        if (StringUtils.isAnyEmpty(userAccount, userPassword, checkPassword, userName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入不能为空");
        }
        if (userAccount.length() < 7) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号位数过短");
        }
        if (userPassword.length()< 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!checkPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码输入不一致");
        }
        if (userName.length() > 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名不合法");
        }

        //2.构造查询条件
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getUserAccount, userAccount);
        long result = this.count(queryWrapper);
        if (result > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已注册");
        }
        //密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(userName);
        boolean resultUser = this.save(user);
        if (!resultUser) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
    }

    @Override
    public boolean logout(HttpServletRequest request) {
        if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null || user.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        User result = this.getById(user.getId());
        return result;
    }

    @Override
    public List<SafeUserVO> getUserListByIds(List<Long> userIdList) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
                .in(User::getId, userIdList);
        List<User> userList = this.list(queryWrapper);
        List<SafeUserVO> safeUserVOList = new ArrayList<>();
        userList.forEach(user -> {
            SafeUserVO safeUserVO = new SafeUserVO();
            BeanUtils.copyProperties(user, safeUserVO);
            safeUserVOList.add(safeUserVO);
        });
        return safeUserVOList;
    }

    @Override
    public Page<UserListVO> getSafeUserList(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = this.getQueryWrapper(userQueryRequest);
        int current = userQueryRequest.getCurrent();
        int pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = this.page(new Page<>(current, pageSize), queryWrapper);
        Page<UserListVO> userListVO = this.getUserListVO(userPage);
        return userListVO;
    }

    @Override
    public Page<UserListVO> getUserListVO(Page<User> userPage) {
        List<User> records = userPage.getRecords();
        Page<UserListVO> userListVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserListVO> userListVOArrayList = new ArrayList<>();
        records.forEach(record -> {
            UserListVO userListVO = UserListVO.objToVo(record);
            userListVOArrayList.add(userListVO);
        });
        userListVOPage.setRecords(userListVOArrayList);
        return userListVOPage;
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }
}




