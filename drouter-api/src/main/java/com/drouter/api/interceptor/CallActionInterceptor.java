package com.drouter.api.interceptor;


import android.os.Looper;

import com.drouter.api.action.IRouterAction;
import com.drouter.api.extra.ActionWrapper;
import com.drouter.api.result.RouterResult;
import com.drouter.api.thread.ActionPost;
import com.drouter.api.thread.PosterSupport;

/**
 * description: 执行 Action 方法的最后一个拦截器
 * author: Darren on 2018/1/24 09:03
 * email: 240336124@qq.com
 * version: 1.0
 */
public class CallActionInterceptor implements ActionInterceptor {
    @Override
    public void intercept(ActionChain chain) {
        // 执行 Action 方法
        ActionPost actionPost = chain.action();
        invokeAction(actionPost, Looper.myLooper() == Looper.getMainLooper());
    }

    /**
     * 处理线程切换
     *
     * @param isMainThread
     * @return
     */
    private void invokeAction(ActionPost actionPost, boolean isMainThread) {
        switch (actionPost.actionWrapper.getThreadMode()) {
            case POSTING:
                invokeAction(actionPost);
            case MAIN:
                if (isMainThread) {
                    invokeAction(actionPost);
                } else {
                    PosterSupport.getMainPoster().enqueue(actionPost);
                }
                break;
            case BACKGROUND:
                if (isMainThread) {
                    PosterSupport.getBackgroundPoster().enqueue(actionPost);
                } else {
                    invokeAction(actionPost);
                }
                break;
            case ASYNC:
                PosterSupport.getAsyncPoster().enqueue(actionPost);
                break;
            default:
                throw new IllegalStateException("Unknown thread mode: " + actionPost.actionWrapper.getThreadMode());
        }
    }

    /**
     * 执行 Action
     *
     * @param actionPost
     */
    private void invokeAction(ActionPost actionPost) {
        ActionWrapper actionWrapper = actionPost.actionWrapper;
        IRouterAction routerAction = actionWrapper.getRouterAction();
        RouterResult routerResult = routerAction.invokeAction(actionPost.context, actionPost.params);
        actionPost.actionCallback.onResult(routerResult);
    }
}
