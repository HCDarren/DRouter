package com.example.circle_module;

import android.content.Context;
import android.util.Log;

import com.drouter.base.annotation.Action;
import com.drouter.base.ThreadMode;
import com.drouter.api.action.IRouterAction;
import com.drouter.api.result.RouterResult;

import java.util.Map;

/**
 * description:
 * author: Darren on 2018/1/22 10:57
 * email: 240336124@qq.com
 * version: 1.0
 */
@Action(path = "circlemodule/test", threadMode = ThreadMode.MAIN)
public class LoginAction implements IRouterAction {
    @Override
    public RouterResult connect(Context context, Map<String, Object> requestData) {
        Log.e("TAG", "CIRCLE LoginAction 方法执行了");
        return null;
    }
}
