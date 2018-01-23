package com.login.module;

import android.content.Context;
import android.util.Log;

import com.drouter.api.action.IRouterAction;
import com.drouter.api.result.RouterResult;
import com.drouter.base.ThreadMode;
import com.drouter.base.annotation.Action;

import java.util.Map;

/**
 * description:
 * author: Darren on 2018/1/22 10:57
 * email: 240336124@qq.com
 * version: 1.0
 */
@Action(path = "login/action", threadMode = ThreadMode.POSTING)
public class LoginAction1 implements IRouterAction {

    @Override
    public RouterResult invokeAction(Context context, Map<String, Object> requestData) {
        try {
            int i = 0;
            while (i < 100) {
                Thread.sleep(100);

                Log.e(Thread.currentThread().getName(), "i = " + i);
                i++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new RouterResult(RouterResult.SUCCEED_CODE);
    }
}
