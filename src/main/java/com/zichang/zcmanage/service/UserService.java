package com.zichang.zcmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zichang.zcmanage.model.domain.User;
import com.zichang.zcmanage.model.request.UserQueryRequest;
import com.zichang.zcmanage.model.vo.LoginUserVO;
import com.zichang.zcmanage.model.vo.SafeUserVO;
import com.zichang.zcmanage.model.vo.UserListVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author lenvovo
* @description 针对表【usage_info(使用信息表)】的数据库操作Service
* @createDate 2024-12-02 00:02:49
*/
public interface UserService extends IService<User> {

    LoginUserVO login(String userAccount, String userPassword, HttpServletRequest request);

    long register(String userAccount, String userPassword, String checkPassword,String userName);

    boolean logout(HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    /**
     * 获取用户ID到用户信息的映射
     * @param userIdList
     * @return
     */
    List<SafeUserVO> getUserListByIds(List<Long> userIdList);

    Page<UserListVO> getSafeUserList(UserQueryRequest userQueryRequest);

    Page<UserListVO> getUserListVO( Page<User> userPage);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
