package com.drouter.api.extra;

import android.content.Context;

import com.drouter.api.action.IRouterAction;
import com.drouter.api.result.RouterResult;

import java.util.Map;

/**
 * description: 默认的 Action
 * author: Darren on 2018/1/23 10:00
 * email: 240336124@qq.com
 * version: 1.0
 */
public class ErrorRouteAction implements IRouterAction {

    @Override
    public RouterResult invoke(Context context, Map<String, Object> requestData) {
        return new RouterResult(RouterResult.ERROR_CODE);
    }
}
