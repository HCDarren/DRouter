package com.drouter.base.annotation;

import com.drouter.base.ThreadMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: 模块注册 apt
 * author: Darren on 2018/1/22 12:32
 * email: 240336124@qq.com
 * version: 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Action {
    /**
     * thread mode
     */
    ThreadMode threadMode() default ThreadMode.POSTING;

    /**
     * Path of route
     */
    String path();

    /**
     * The priority of route.
     */
    int priority() default -1;

    /**
     * 单独额外的进程
     */
    boolean extraProcess() default false;
}
