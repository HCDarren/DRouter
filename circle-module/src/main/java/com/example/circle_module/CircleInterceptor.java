package com.example.circle_module;

import android.widget.Toast;

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
@Interceptor(priority = 18)
public class CircleInterceptor implements ActionInterceptor {

    @Override
    public void intercept(ActionChain chain) {
        ActionPost actionPost = chain.action();
        if (chain.actionPath().equals("circlemodule/test")) {
            Toast.makeText(actionPost.context, "拦截圈子，跳转到登录", Toast.LENGTH_LONG).show();
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
