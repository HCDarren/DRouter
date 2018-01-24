package com.drouter.api.thread;

import com.drouter.api.action.IRouterAction;
import com.drouter.api.core.DRouter;
import com.drouter.api.extra.ActionWrapper;
import com.drouter.api.extra.Consts;
import com.drouter.api.result.RouterResult;

/**
 * description:
 * author: Darren on 2018/1/23 17:01
 * email: 240336124@qq.com
 * version: 1.0
 */
final class BackgroundPoster implements Runnable, Poster {

    private final ActionPostQueue queue;

    private volatile boolean executorRunning;

    BackgroundPoster() {
        queue = new ActionPostQueue();
    }

    @Override
    public void enqueue(ActionPost actionPost) {
        synchronized (this) {
            queue.enqueue(actionPost);
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
                    ActionPost actionPost = queue.poll(1000);
                    if (actionPost == null) {
                        synchronized (this) {
                            // Check again, this time in synchronized
                            actionPost = queue.poll();
                            if (actionPost == null) {
                                executorRunning = false;
                                return;
                            }
                        }
                    }

                    ActionWrapper actionWrapper = actionPost.actionWrapper;
                    IRouterAction routerAction = actionWrapper.getRouterAction();
                    RouterResult routerResult = routerAction.invokeAction(actionPost.context, actionPost.params);
                    actionPost.actionCallback.onResult(routerResult);
                    
                    actionPost.releasePendingPost();
                }
            } catch (InterruptedException e) {
                DRouter.logger.e(Consts.TAG, Thread.currentThread().getName() + " was interruppted");
            }
        } finally {
            executorRunning = false;
        }
    }
}
