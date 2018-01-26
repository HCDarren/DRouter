package com.drouter.api.action;

import com.drouter.api.interceptor.ActionInterceptor;

import java.util.List;

/**
 * description:
 * author: Darren on 2018/1/22 11:08
 * email: 240336124@qq.com
 * version: 1.0
 */
public interface IRouterInterceptor {
    // 通过 Action 的名称找到 Action
    List<ActionInterceptor> getInterceptors();
}
