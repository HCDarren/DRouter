package com.drouter.api.core;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.drouter.api.action.IRouterAction;
import com.drouter.api.exception.InitException;
import com.drouter.api.extra.Consts;
import com.drouter.api.extra.DefaultLogger;
import com.drouter.api.extra.ErrorRouteAction;
import com.drouter.api.extra.ILogger;
import com.drouter.api.utils.ClassUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description:
 * author: Darren on 2018/1/22 09:59
 * email: 240336124@qq.com
 * version: 1.0
 */
public class DRouter {
    // 是否被初始化
    private volatile static boolean hasInit = false;
    // 是否是 debugable 状态
    private volatile static boolean debuggable = false;
    // 日志打印
    public volatile static ILogger logger = new DefaultLogger();
    // 缓存的 RouterAction
    private volatile static Map<String, IRouterAction> cacheRouterActions = new HashMap();
    // 缓存的 RouterModule
    private volatile static Map<String, IRouterModule> cacheRouterModules = new HashMap();
    // 所有 moudle
    private static List<String> mAllModuleClassName;
    private Context mApplicationContext;

    public static synchronized void openDebug() {
        debuggable = true;
        logger.showLog(true);

        logger.d(Consts.TAG, "DRouter openDebug");
        logger.d(Consts.TAG, "DRouter showLog");
    }

    private volatile static DRouter instance = null;

    public static boolean debuggable() {
        return debuggable;
    }

    private DRouter() {

    }

    /**
     * Get instance of router. A
     * All feature U use, will be starts here.
     */
    public static DRouter getInstance() {
        if (instance == null) {
            synchronized (DRouter.class) {
                if (instance == null) {
                    instance = new DRouter();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化数据
     */
    public void init(Application context) {
        if (hasInit) {
            throw new InitException("ARouter already initialized, It can only be initialized once.");
        }

        hasInit = true;

        this.mApplicationContext = context;
        // 获取 com.drotuer.assist 包名下的所有类名信息
        try {
            mAllModuleClassName = ClassUtils.getFileNameByPackageName(context, Consts.AROUTER_ASSIST_PACK_NAME);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String className : mAllModuleClassName) {
            logger.d(Consts.TAG, "扫描到: " + className);
        }
    }


    public RouterForward action(String actionName) {
        // 1. 动态先查找加载 Module
        // actionName 的格式必须是 xxx/xxx
        if (!actionName.contains("/")) {
            String message = "action name  format error -> " + actionName + ", like: moduleName/actionName";
            debugMessage(message);
            return new RouterForward(new ErrorRouteAction());
        }

        // 2.获取 moduleName，实例化 Module，并缓存
        String moduleName = actionName.split("/")[0];
        String moduleClassName = searchModuleClassName(moduleName);
        if (TextUtils.isEmpty(moduleClassName)) {
            String message = "module find error";
            debugMessage(message);
            return new RouterForward(new ErrorRouteAction());
        }
        IRouterModule routerModule = cacheRouterModules.get(moduleClassName);
        if (routerModule == null) {
            try {
                Class<? extends IRouterModule> moduleClass = (Class<? extends IRouterModule>) Class.forName(moduleClassName);
                routerModule = moduleClass.newInstance();
                cacheRouterModules.put(moduleClassName, routerModule);
            } catch (Exception e) {
                e.printStackTrace();
                String message = "instance module error: " + e.getMessage();
                debugMessage(message);
                return new RouterForward(new ErrorRouteAction());
            }
        }

        // 3. 从 Module 中获取 Action 类名，然后创建缓存 Action
        String actionClassName = routerModule.findActionClassName(actionName);
        if (TextUtils.isEmpty(actionClassName)) {
            if (debuggable) {
                String message = String.format("According to the %s action name cannot find action.", actionName);
                logger.e(Consts.TAG, message);
                showToast(message);
            }
            return new RouterForward(new ErrorRouteAction());
        }
        IRouterAction routerAction = cacheRouterActions.get(actionClassName);
        if (routerAction == null) {
            try {
                Class<? extends IRouterAction> actionClass = (Class<? extends IRouterAction>) Class.forName(actionClassName);

                if (!IRouterAction.class.isAssignableFrom(actionClass)) {
                    String message = actionClassName + " must be implements IRouterAction.";
                    debugMessage(message);
                    return new RouterForward(new ErrorRouteAction());
                }

                routerAction = actionClass.newInstance();
                cacheRouterActions.put(actionClassName, routerAction);
            } catch (Exception e) {
                String message = "instance action error: " + e.getMessage();
                debugMessage(message);
                return new RouterForward(new ErrorRouteAction());
            }
        }

        return new RouterForward(routerAction);
    }

    /**
     * 显示 debug 信息
     *
     * @param message
     */
    private void debugMessage(String message) {
        if (debuggable) {
            logger.e(Consts.TAG, message);
            showToast(message);
        }
    }

    /**
     * 打印显示 Toast
     *
     * @param message
     */
    private void showToast(String message) {
        Toast.makeText(mApplicationContext, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 根据 moduleName 查询 module 的全类名
     *
     * @param moduleName
     * @return
     */
    private String searchModuleClassName(String moduleName) {
        for (String moduleClassName : mAllModuleClassName) {
            if (moduleClassName.contains(moduleName)) {
                return moduleClassName;
            }
        }
        return null;
    }
}
