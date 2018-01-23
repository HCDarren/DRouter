package com.drouter.api.thread;

import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description:
 * author: Darren on 2018/1/23 16:08
 * email: 240336124@qq.com
 * version: 1.0
 */
public class PosterSupport {
    private static volatile Poster mainPoster, backgroundPoster, asyncPoster;
    private final static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();


    public static Poster getMainPoster() {
        if (mainPoster == null) {
            synchronized (PosterSupport.class) {
                if (mainPoster == null) {
                    mainPoster = new HandlerPoster(Looper.getMainLooper(), 10);
                }
            }
        }
        return mainPoster;
    }

    public static ExecutorService getExecutorService() {
        return DEFAULT_EXECUTOR_SERVICE;
    }

    public static Poster getBackgroundPoster() {
        if (backgroundPoster == null) {
            synchronized (PosterSupport.class) {
                if (backgroundPoster == null) {
                    backgroundPoster = new BackgroundPoster();
                }
            }
        }
        return backgroundPoster;
    }

    public static Poster getAsyncPoster() {
        if (asyncPoster == null) {
            synchronized (PosterSupport.class) {
                if (asyncPoster == null) {
                    asyncPoster = new AsyncPoster();
                }
            }
        }
        return asyncPoster;
    }
}
