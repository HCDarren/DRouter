package com.example.circle_module;

import com.drouter.api.core.DRouter;
import com.drouter.api.interceptor.ActionInterceptor;
import com.drouter.api.thread.ActionPost;
import com.drouter.base.annotation.Interceptor;

/**
 * description:
 * author: Darren on 2018/1/24 16:14
 * email: 240336124@qq.com
 * version: 1.0
 */
@Interceptor(priority = 8)
public class CircleInterceptor implements ActionInterceptor {

    @Override
    public void intercept(ActionChain chain) {
        ActionPost actionPost = chain.action();
        if (chain.actionPath().equals("circlemodule/test")) {
            // 拦截
            chain.onInterrupt();
            // 跳转到登录页面
            DRouter.getInstance()
                    .action("login/action")
                    .context(actionPost.context)
                    .invokeAction();
        }
        // 继续向下转发
        chain.proceed(actionPost);
    }
}
