package com.drouter.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description:
 * author: Darren on 2018/1/24 16:10
 * email: 240336124@qq.com
 * version: 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Interceptor {
    int priority();
}
