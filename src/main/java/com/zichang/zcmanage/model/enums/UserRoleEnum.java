package com.zichang.zcmanage.model.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRoleEnum {

    USER("用户","user"),
    ADMIN("管理员","admin");

    private final String text;

    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if(Objects.isNull(value)) {
            return null;
        }
        for(UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if(userRoleEnum.getValue().equals(value)) {
                return userRoleEnum;
            }
        }
        return null;
    }

}
