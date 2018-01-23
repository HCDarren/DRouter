package com.drouter.api.core;

import android.content.Context;
import android.os.Looper;

import com.drouter.api.action.IRouterAction;
import com.drouter.api.extra.ActionWrapper;
import com.drouter.api.result.RouterResult;
import com.drouter.api.thread.PosterSupport;
import com.drouter.base.ThreadMode;

import java.util.HashMap;
import java.util.Map;

/**
 * description: 路由转发
 * author: Darren on 2018/1/22 11:20
 * email: 240336124@qq.com
 * version: 1.0
 */
public class RouterForward {
    private ActionWrapper mActionWrapper;
    private Context mContext;
    private Map<String, Object> mParams;
    private ThreadMode mThreadMode = null;

    /**
     * 指定 threadMode 这里指定的优先级高于 Action 注解上的 threadMode
     * @param threadMode
     * @return
     */
    public RouterForward threadMode(ThreadMode threadMode){
        this.mThreadMode = threadMode;
        return this;
    }

    public RouterForward(ActionWrapper actionWrapper) {
        this.mActionWrapper = actionWrapper;
        mParams = new HashMap<>();
    }

    /**
     * 执行 Action
     *
     * @return
     */
    public RouterResult invokeAction() {
        IRouterAction routerAction = mActionWrapper.getRouterAction();
        if (routerAction == null)
            return new RouterResult.Builder().error().build();
        // 处理优先级和线程
        return invokeAction(routerAction, Looper.myLooper() == Looper.getMainLooper());
    }

    /**
     * 路由转发方法传递的 threadMode 优先级高于 Action 注解上的 threadMode
     * @return
     */
    public ThreadMode getThreadMode() {
        return mThreadMode == null?mActionWrapper.getThreadMode():mThreadMode;
    }

    /**
     * 处理线程切换
     *
     * @param routerAction
     * @param isMainThread
     * @return
     */
    private RouterResult invokeAction(IRouterAction routerAction, boolean isMainThread) {
        switch (getThreadMode()) {
            case POSTING:
                return invokeAction(routerAction);
            case MAIN:
                if (isMainThread) {
                    return invokeAction(routerAction);
                } else {
                    PosterSupport.getMainPoster().enqueue(routerAction, mContext, mParams);
                }
                break;
            case BACKGROUND:
                if (isMainThread) {
                    PosterSupport.getBackgroundPoster().enqueue(routerAction, mContext, mParams);
                } else {
                    return invokeAction(routerAction);
                }
                break;
            case ASYNC:
                PosterSupport.getAsyncPoster().enqueue(routerAction, mContext, mParams);
                break;
            default:
                throw new IllegalStateException("Unknown thread mode: " + mActionWrapper.getThreadMode());
        }
        return new RouterResult.Builder().success().build();
    }

    private RouterResult invokeAction(IRouterAction routerAction) {
        return routerAction.invokeAction(mContext, mParams);
    }

    public RouterForward context(Context context) {
        this.mContext = context;
        return this;
    }

    public RouterForward param(String key, Object value) {
        mParams.put(key, value);
        return this;
    }

    public RouterForward param(Map<String, Object> params) {
        mParams.putAll(params);
        return this;
    }

}
