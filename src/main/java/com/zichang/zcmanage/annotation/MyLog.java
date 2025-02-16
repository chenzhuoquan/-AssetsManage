package com.zichang.zcmanage.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


//决定MyLog注解可以加在哪些成分上，如加载类身上，或者属性身上，或者方法身上等成分
@Target({ElementType.METHOD,ElementType.PARAMETER})
//注解意思是让MyLog这个注解的生命周期在程序运行时都存在
@Retention(RetentionPolicy.RUNTIME)
public @interface MyLog {

    /**
     * 模块标题
     * @return
     */
    String title() default "";


    /**
     * 日志内容
     */
    String content() default "";
}
