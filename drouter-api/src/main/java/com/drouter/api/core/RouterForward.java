package com.drouter.api.core;

import android.content.Context;

import com.drouter.api.action.IRouterAction;
import com.drouter.api.result.RouterResult;

import java.util.HashMap;
import java.util.Map;

/**
 * description: 路由转发
 * author: Darren on 2018/1/22 11:20
 * email: 240336124@qq.com
 * version: 1.0
 */
public class RouterForward {
    private IRouterAction mRouterAction;
    private Context mContext;
    private Map<String, Object> mParams;

    public RouterForward(IRouterAction routerAction) {
        this.mRouterAction = routerAction;
        mParams = new HashMap<>();
    }

    public RouterResult connect() {
        return mRouterAction.connect(mContext, mParams);
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
