package com.login.module;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.drouter.base.ThreadMode;
import com.drouter.base.annotation.Action;
import com.drouter.api.action.IRouterAction;
import com.drouter.api.result.RouterResult;

import java.util.Map;

/**
 * description:
 * author: Darren on 2018/1/22 10:57
 * email: 240336124@qq.com
 * version: 1.0
 */
@Action(path = "login/action", threadMode = ThreadMode.MAIN)
public class LoginAction implements IRouterAction {

    @Override
    public RouterResult connect(Context context, Map<String, Object> requestData) {
        Log.e("TAG", "LoginAction 方法执行了");
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("key", (String) requestData.get("key"));
        context.startActivity(intent);
        return new RouterResult(RouterResult.SUCCEED_CODE);
    }
}
