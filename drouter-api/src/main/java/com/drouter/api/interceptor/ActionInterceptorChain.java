package com.drouter.api.interceptor;

import com.drouter.api.thread.ActionPost;

import java.util.List;

/**
 * description:
 * author: Darren on 2018/1/24 09:38
 * email: 240336124@qq.com
 * version: 1.0
 */
public class ActionInterceptorChain implements Interceptor.ActionChain {
    // 是否被拦截了
    private boolean isInterrupt = false;
    private List<Interceptor> interceptors;
    private ActionPost actionPost;
    private int index;

    public ActionInterceptorChain(List<Interceptor> interceptors, ActionPost actionPost, int index) {
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
    public void proceed(ActionPost actionPost) {
        if (!isInterrupt && index < interceptors.size()) {
            // 继续往下分发
            Interceptor.ActionChain next = new ActionInterceptorChain(interceptors, actionPost, index + 1);
            Interceptor interceptor = interceptors.get(index);
            interceptor.intercept(next);
        }
    }

    @Override
    public ActionPost action() {
        return actionPost;
    }
}
