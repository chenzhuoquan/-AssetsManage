package com.zichang.zcmanage.aop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zichang.zcmanage.annotation.MyLog;
import com.zichang.zcmanage.model.domain.Assets;
import com.zichang.zcmanage.model.domain.Inventoryrecord;
import com.zichang.zcmanage.service.AssetsService;
import com.zichang.zcmanage.service.InventoryrecordService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;


/**
 * 切面处理类，记录操作日志到数据库
 */
@Component
@Aspect
public class OperLogAspect {

    @Resource
    private AssetsService assetsService;

    @Resource
    private InventoryrecordService inventoryrecordService;
    /**
     * 基于注解切入(打了自定义注解的方法才会切入)
     */
    @Pointcut("@annotation(com.zichang.zcmanage.annotation.MyLog)")
    public void operLogPointCut(){

    }

    /**
     * 异常返回通知，用于拦截异常日志信息 连接点抛出异常后执行
     */
    @AfterThrowing(pointcut = "operLogPointCut()", throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint,Throwable e){
        try {
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            //获取操作
            MyLog myLog = method.getAnnotation(MyLog.class);

            Inventoryrecord inventoryrecord=new Inventoryrecord();
            if(myLog!=null){
                inventoryrecord.setTitle(myLog.title());//设置模块名称
                inventoryrecord.setContent(myLog.content());//设置日志内容
            }
            Object[] args = joinPoint.getArgs();
            String paramAssetCode = (String) args[0];
            inventoryrecord.setAssetCode(paramAssetCode);
            LambdaQueryWrapper<Assets> lambdaQueryWrapper = Wrappers.lambdaQuery(Assets.class)
                    .select(Assets::getRoom_number)
                    .eq(Assets::getDevice_code, paramAssetCode);
            Assets one = assetsService.getOne(lambdaQueryWrapper);
            if(one!=null){
                inventoryrecord.setLocation(one.getRoom_number());
            }
            inventoryrecord.setStatus(0);
            inventoryrecord.setErrorMessage(e.getMessage());
            inventoryrecordService.save(inventoryrecord);
        }catch (Exception e1){
            e1.printStackTrace();
        }

    }

   /* private String argsArrayToString(Object[] paramsArray){
        StringBuffer params=new StringBuffer();
        if(paramsArray!=null&&paramsArray.length>0){
            for(Object obj:paramsArray){

                if(obj!=null){

                    try {
                        Gson gson=new Gson();
                        Object json = gson.toJson(obj);
                        params.append(json.toString()).append(" ");
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
        //返回去掉首尾空格的字符串
        return params.toString().trim();
    }*/





}
