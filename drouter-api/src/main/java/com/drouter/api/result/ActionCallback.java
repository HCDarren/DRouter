package com.drouter.api.result;

/**
 * description:
 * author: Darren on 2018/1/24 09:05
 * email: 240336124@qq.com
 * version: 1.0
 */
public interface ActionCallback {
    // 被拦截了
    void onInterrupt();

    // 没被拦截返回结果
    void onResult(RouterResult result);

    // 默认的 ActionCallback
    ActionCallback DEFAULT_ACTION_CALLBACK = new ActionCallback() {

        @Override
        public void onInterrupt() {

        }

        @Override
        public void onResult(RouterResult result) {

        }
    };
}
