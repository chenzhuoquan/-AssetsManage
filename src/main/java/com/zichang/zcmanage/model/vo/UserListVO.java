package com.zichang.zcmanage.model.vo;

import com.zichang.zcmanage.model.domain.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserListVO  implements Serializable {

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 封装类转对象
     *
     * @param userListVO
     * @return
     */
    public static User voToObj(UserListVO userListVO) {
        if (userListVO == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(userListVO, user);

        return user;
    }

    /**
     * 对象转封装类
     *
     * @param user
     * @return
     */
    public static UserListVO objToVo(User user) {
        if (user == null) {
            return null;
        }
        UserListVO userListVO = new UserListVO();
        BeanUtils.copyProperties(user, userListVO);
        return userListVO;
    }

}
