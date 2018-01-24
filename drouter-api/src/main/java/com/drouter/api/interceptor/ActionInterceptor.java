package com.drouter.api.interceptor;

import com.drouter.api.thread.ActionPost;

/**
 * description: 拦截器
 * author: Darren on 2018/1/22 11:59
 * email: 240336124@qq.com
 * version: 1.0
 */
public interface ActionInterceptor {
    void intercept(ActionChain chain);

    interface ActionChain {
        // 打断拦截
        void onInterrupt();

        // 分发给下一个拦截器
        void proceed(ActionPost actionPost);

        // 获取 ActionPost
        ActionPost action();

        String actionPath();
    }
}
