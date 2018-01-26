package com.drouter.api.core;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.drouter.api.action.IRouterAction;
import com.drouter.api.action.IRouterInterceptor;
import com.drouter.api.action.IRouterModule;
import com.drouter.api.exception.InitException;
import com.drouter.api.extra.ActionWrapper;
import com.drouter.api.extra.Consts;
import com.drouter.api.extra.DefaultLogger;
import com.drouter.api.extra.ErrorActionWrapper;
import com.drouter.api.extra.ILogger;
import com.drouter.api.interceptor.ActionInterceptor;
import com.drouter.api.interceptor.CallActionInterceptor;
import com.drouter.api.interceptor.ErrorActionInterceptor;
import com.drouter.api.thread.PosterSupport;
import com.drouter.api.utils.ClassUtils;

import java.io.IOException;
import java.util.ArrayList;
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
    private volatile static Map<String, ActionWrapper> cacheRouterActions = new HashMap();
    // 缓存的 RouterModule
    private volatile static Map<String, IRouterModule> cacheRouterModules = new HashMap();
    // 所有 moudle
    private static List<String> mAllModuleClassName;
    private Context mApplicationContext;

    private static List<ActionInterceptor> interceptors = new ArrayList<>();

    public static synchronized void openDebug() {
        debuggable = true;
        logger.showLog(true);

        logger.d(Consts.TAG, "DRouter openDebug");
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
            mAllModuleClassName = ClassUtils.getFileNameByPackageName(context, Consts.ROUTER_MODULE_PACK_NAME);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String className : mAllModuleClassName) {
            logger.d(Consts.TAG, "扫描到: " + className);
        }
        // 添加并且实例化所有拦截器
        scanAddInterceptors(context);
    }

    // 扫描并且添加拦截器
    private void scanAddInterceptors(final Context context) {
        PosterSupport.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                // 1. 错误拦截器
                interceptors.add(new ErrorActionInterceptor());
                // 2. module 自定义的拦截器
                try {
                    List<String> interceptorGroups = ClassUtils.getFileNameByPackageName(context, Consts.ROUTER_INTERCEPTOR_PACK_NAME);
                    // 循环所有的 Group 拦截器
                    for (String interceptorGroup : interceptorGroups) {
                        if (interceptorGroup.contains(Consts.ROUTER_INTERCEPTOR_GROUP_PREFIX)) {
                            IRouterInterceptor routerInterceptor = (IRouterInterceptor) Class.forName(interceptorGroup).newInstance();
                            List<ActionInterceptor> interceptorClasses = routerInterceptor.getInterceptors();
                            for (int i = interceptorClasses.size() - 1; i >= 0; i--) {
                                ActionInterceptor interceptor = interceptorClasses.get(i);
                                // 添加到拦截器链表
                                interceptors.add(interceptor);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String message = "Instance interceptor error: " + e.getMessage();
                    logger.e(Consts.TAG, message);
                }

                // 3. 最后添加 Action 执行调用的拦截器
                interceptors.add(new CallActionInterceptor());
            }
        });
    }


    public RouterForward action(String actionName) {
        // 1. 动态先查找加载 Module
        // actionName 的格式必须是 xxx/xxx
        if (!actionName.contains("/")) {
            String message = "action name  format error -> <" + actionName + ">, like: moduleName/actionName";
            debugMessage(message);
            return new RouterForward(new ErrorActionWrapper(), interceptors);
        }

        // 2.获取 moduleName，实例化 Module，并缓存
        String moduleName = actionName.split("/")[0];
        String moduleClassName = searchModuleClassName(moduleName);
        if (TextUtils.isEmpty(moduleClassName)) {
            String message = String.format("Please check to the action name is correct: according to the <%s> cannot find module %s.", actionName, moduleName);
            debugMessage(message);
            return new RouterForward(new ErrorActionWrapper(), interceptors);
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
                return new RouterForward(new ErrorActionWrapper(), interceptors);
            }
        }

        // 3. 从 Module 中获取 ActionWrapper 类名，然后创建缓存 ActionWrapper
        ActionWrapper actionWrapper = cacheRouterActions.get(actionName);
        if (actionWrapper == null) {
            actionWrapper = routerModule.findAction(actionName);
        } else {
            return new RouterForward(actionWrapper, interceptors);
        }

        if (actionWrapper == null) {
            String message = String.format("Please check to the action name is correct: according to the <%s> cannot find action.", actionName);
            debugMessage(message);
            return new RouterForward(new ErrorActionWrapper(), interceptors);
        }

        Class<? extends IRouterAction> actionClass = actionWrapper.getActionClass();
        IRouterAction routerAction = actionWrapper.getRouterAction();
        if (routerAction == null) {
            try {
                if (!IRouterAction.class.isAssignableFrom(actionClass)) {
                    String message = actionClass.getCanonicalName() + " must be implements IRouterAction.";
                    debugMessage(message);
                    return new RouterForward(new ErrorActionWrapper(), interceptors);
                }
                // 创建 RouterAction 实例，并缓存起来
                routerAction = actionClass.newInstance();
                actionWrapper.setRouterAction(routerAction);
                cacheRouterActions.put(actionName, actionWrapper);
            } catch (Exception e) {
                String message = "instance action error: " + e.getMessage();
                debugMessage(message);
                return new RouterForward(new ErrorActionWrapper(), interceptors);
            }
        }

        return new RouterForward(actionWrapper, interceptors);
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
