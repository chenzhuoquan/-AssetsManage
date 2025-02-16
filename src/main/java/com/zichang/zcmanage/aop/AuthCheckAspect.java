package com.zichang.zcmanage.aop;

import com.zichang.zcmanage.annotation.AuthCheck;
import com.zichang.zcmanage.common.ErrorCode;
import com.zichang.zcmanage.exception.BusinessException;
import com.zichang.zcmanage.model.domain.User;
import com.zichang.zcmanage.model.enums.UserRoleEnum;
import com.zichang.zcmanage.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * 权限校验切面
 */
@Aspect
@Component
public class AuthCheckAspect {


    @Resource
    private UserService userService;



    @Around("@annotation(authCheck)")
    public Object justAdmin(ProceedingJoinPoint joinPoint,AuthCheck authCheck) throws Throwable {

        String mustRole = authCheck.mustRole();
        //获取请求上下文
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        User loginUser = userService.getLoginUser(request);

        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        //如果不需要权限，放行
        if(mustRoleEnum==null){
            return joinPoint.proceed();
        }

        //执行以下逻辑，需要有权限才能通过
        String userRole = loginUser.getUserRole();
        UserRoleEnum enumByValue = UserRoleEnum.getEnumByValue(userRole);
        if(enumByValue==null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        //要求必须有管理员权限，但用户没有管理员权限，拒绝
        if(UserRoleEnum.ADMIN.equals(mustRoleEnum)&&!UserRoleEnum.ADMIN.equals(UserRoleEnum.getEnumByValue(userRole))){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        return joinPoint.proceed();
    }

}
