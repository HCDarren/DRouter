package com.drouter.api.extra;

import com.drouter.api.action.IRouterAction;
import com.drouter.base.ThreadMode;

/**
 * description:
 * author: Darren on 2018/1/23 14:22
 * email: 240336124@qq.com
 * version: 1.0
 */
public class ActionWrapper {
    private Class<? extends IRouterAction> actionClass;
    private String path;
    private ThreadMode threadMode;
    private boolean extraProcess;
    private IRouterAction routerAction;

    ActionWrapper() {

    }

    public void setRouterAction(IRouterAction routerAction) {
        this.routerAction = routerAction;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public IRouterAction getRouterAction() {
        return routerAction;
    }

    private ActionWrapper(Class<? extends IRouterAction> actionClass, String path, boolean extraProcess, ThreadMode threadMode) {
        this.actionClass = actionClass;
        this.path = path;
        this.extraProcess = extraProcess;
        this.threadMode = threadMode;
    }

    public Class<? extends IRouterAction> getActionClass() {
        return actionClass;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public String getPath() {
        return path;
    }

    public boolean isExtraProcess() {
        return extraProcess;
    }

    public static ActionWrapper build(Class<? extends IRouterAction> actionClass, String path, boolean extraProcess, ThreadMode threadMode) {
        return new ActionWrapper(actionClass, path, extraProcess, threadMode);
    }
}
