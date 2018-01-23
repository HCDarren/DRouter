package com.drouter.api.thread;

import android.content.Context;

import com.drouter.api.action.IRouterAction;
import com.drouter.api.core.DRouter;
import com.drouter.api.extra.Consts;

import java.util.Map;

/**
 * description:
 * author: Darren on 2018/1/23 17:01
 * email: 240336124@qq.com
 * version: 1.0
 */
final class BackgroundPoster implements Runnable, Poster {

    private final PendingPostQueue queue;

    private volatile boolean executorRunning;

    BackgroundPoster() {
        queue = new PendingPostQueue();
    }

    @Override
    public void enqueue(IRouterAction routerAction, Context context, Map<String, Object> params) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(routerAction, context, params);
        synchronized (this) {
            queue.enqueue(pendingPost);
            if (!executorRunning) {
                executorRunning = true;
                PosterSupport.getExecutorService().execute(this);
            }
        }
    }

    @Override
    public void run() {
        try {
            try {
                while (true) {
                    PendingPost pendingPost = queue.poll(1000);
                    if (pendingPost == null) {
                        synchronized (this) {
                            // Check again, this time in synchronized
                            pendingPost = queue.poll();
                            if (pendingPost == null) {
                                executorRunning = false;
                                return;
                            }
                        }
                    }

                    pendingPost.routerAction.invokeAction(pendingPost.context, pendingPost.params);
                    pendingPost.releasePendingPost();
                }
            } catch (InterruptedException e) {
                DRouter.logger.e(Consts.TAG, Thread.currentThread().getName() + " was interruppted");
            }
        } finally {
            executorRunning = false;
        }
    }
}
