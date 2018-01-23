package com.darren.drouter;

import android.app.Application;
import com.drouter.api.core.DRouter;

/**
 * description:
 * author: Darren on 2018/1/22 11:27
 * email: 240336124@qq.com
 * version: 1.0
 */
public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        DRouter.openDebug();
        DRouter.getInstance().init(this);
    }
}
