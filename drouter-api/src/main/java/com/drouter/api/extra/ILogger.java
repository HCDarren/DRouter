package com.drouter.api.extra;

/**
 * description:
 * author: Darren on 2018/1/22 10:02
 * email: 240336124@qq.com
 * version: 1.0
 */
public interface ILogger {
    void showLog(boolean isShowLog);

    void d(String tag, String message);

    void i(String tag, String message);

    void w(String tag, String message);

    void e(String tag, String message);
}
