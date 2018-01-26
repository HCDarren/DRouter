package com.drouter.api.interceptor;

import com.drouter.api.thread.ActionPost;

import java.util.List;

/**
 * description:
 * author: Darren on 2018/1/24 09:38
 * email: 240336124@qq.com
 * version: 1.0
 */
public class ActionInterceptorChain implements ActionInterceptor.ActionChain {
    // 是否被拦截了
    private boolean isInterrupt = false;
    private List<ActionInterceptor> interceptors;
    private ActionPost actionPost;
    private int index;

    public ActionInterceptorChain(List<ActionInterceptor> interceptors, ActionPost actionPost, int index) {
        this.interceptors = interceptors;
        this.actionPost = actionPost;
        this.index = index;
    }

    @Override
    public void onInterrupt() {
        isInterrupt = true;
        actionPost.actionCallback.onInterrupt();
    }

    @Override
    public void proceed(ActionPost actionPost) { // 0
        if (!isInterrupt && index < interceptors.size()) {
            // 继续往下分发
            ActionInterceptor.ActionChain next = new ActionInterceptorChain(interceptors, actionPost, index + 1);
            // 0 拦截器
            ActionInterceptor interceptor = interceptors.get(index);
            // 执行第一个
            interceptor.intercept(next);
        }
    }

    @Override
    public ActionPost action() {
        return actionPost;
    }

    @Override
    public String actionPath() {
        return actionPost.actionWrapper.getPath();
    }
}
