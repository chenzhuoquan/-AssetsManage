package com.zichang.zcmanage.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long id;


    /**
     * 账号
     */
    private String userAccount;


    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;



}
