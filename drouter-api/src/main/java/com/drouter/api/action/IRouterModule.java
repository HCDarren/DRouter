package com.drouter.api.action;

import com.drouter.api.extra.ActionWrapper;

/**
 * description:
 * author: Darren on 2018/1/22 11:08
 * email: 240336124@qq.com
 * version: 1.0
 */
public interface IRouterModule {
    ActionWrapper findAction(String actionName);
}
