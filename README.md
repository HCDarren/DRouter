# Android 平台多模块多组件开发的路由库


#### **一.基本介绍**

**1.** 该库所涉及到的类大概在 30 个左右，源码并不多相信我们都能读懂里面的内容，这里罗列一下源码中所涉及到的一些知识点：

(1) 编译时注解自动生成 Module、Action 和 Intercepter

(2) 线程、线程池、线程同步异步和 Handler

(3) 责任链模式、享元模式、策略模式、模板模式 ...

**2.** 作为一个多模块的路由通信库，相信它已支持了所有跨模块通信的使用场景，功能介绍如下：

(1) 支持依赖注入，可单独作为依赖注入框架使用

(2) 支持线程切换和调度（原始线程，主线程，同步，异步）

(3) 支持多模块工程下的所有跨模块通信使用场景

(4) 支持添加多个拦截器，可根据优先级自定义拦截顺序

(5) 支持权限和网络检测、登录拦截跳转和数据埋点等功能

**3.** 阅读了大量的开源库源码，本库的所有代码思想都来自其中，很感激这些大牛的开源和分享精神：

[(1) ARouter](https://github.com/alibaba/ARouter)

[(2) butterknife](https://github.com/JakeWharton/butterknife)

[(3) okhttp](https://github.com/square/okhttp)

[(4) EventBus](https://github.com/greenrobot/EventBus)

[(5) RxJava](https://github.com/ReactiveX/RxJava)

[(6) retrofit](https://github.com/square/retrofit)

#### **2. DRouter 基本使用**
1. 在需要跨模块通信的Module中添加依赖和配置
```
    defaultConfig {
        ......
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [moduleName: project.getName()]
            }
        }
    }

    dependencies {
        .......
        annotationProcessor project(':drouter-compiler')
    }
```
2. 在 Module 中创建需要执行的 Action
```
// path 必须是以在 gradle 中配置的 moduleName + "/" 开头，否则编译通不过。
// threadMode 支持 POSTING 、MAIN、BACKGROUND、ASYNC 默认情况下是 POSTING（原始线程）
@Action(path = "login/action", threadMode = ThreadMode.MAIN)
public class LoginAction implements IRouterAction {

    @Override
    public RouterResult invokeAction(Context context, Map<String, Object> requestData) {
        // 通信执行方法支持所有场景，启动 Activity，Service，Provider，弹框，缓存数据，获取 Fragment 等等等等
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("key", (String) requestData.get("key"));
        context.startActivity(intent);
        return new RouterResult.Builder().success().object(100).build();
    }
}
```
3. 初始化 SDK
```
public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        // 开启 debug
        DRouter.openDebug();
        // 初始化且只能初始化一次，参数必须是 Application
        DRouter.getInstance().init(this);
    }
}
```
4. 可在任意 Module 中执行跳转
```
// 根据 action 查询只执行对应方法，不处理返回回调，参数携带随意
DRouter.getInstance()
                .action("login/action")
                .context(this)
                .param("key", "value")
                .invokeAction();

// 根据 action 查询执行对应方法，并处理返回回调
DRouter.getInstance()
                .action("circlemodule/test")
                .context(this)
                .invokeAction(new ActionCallback() {
                    @Override
                    public void onInterrupt() {
                        Log.e("TAG", "被拦截了");
                    }

                    @Override
                    public void onResult(RouterResult result) {
                        // 注意该方法任何时候都会执行在主线程中
                        Log.e("TAG", "result = " + result.toString());
                    }
                });
```
5. 在任意模块下都可添加拦截
```
// priority 优先级越高，拦截器执行越优先
@Interceptor(priority = 18)
public class CircleInterceptor implements ActionInterceptor {

    @Override
    public void intercept(ActionChain chain) {
        ActionPost actionPost = chain.action();
        // 圈子详情页必须是要登录，如果没有登录即可拦截跳转到登录页面，否则继续往下执行。
        if (chain.actionPath().equals("circlemodule/test")) {
            Toast.makeText(actionPost.context, "拦截圈子，跳转到登录", Toast.LENGTH_LONG).show();
            // 跳转到登录页面
            DRouter.getInstance()
                    .action("login/action")
                    .context(actionPost.context)
                    .invokeAction();

            // 这个方法调用后便会拦截整条链
            chain.onInterrupt();
        }
        // 继续向下转发
        chain.proceed(actionPost);
    }
}
```

#### **3. 其他**
1. 简书详细介绍地址：xxxx
2. 视频详细讲解地址：周日晚八点


