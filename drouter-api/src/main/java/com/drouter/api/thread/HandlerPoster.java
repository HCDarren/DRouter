package com.drouter.api.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.drouter.api.action.IRouterAction;

import java.util.Map;

/**
 * description: 处理主线程切换
 * author: Darren on 2018/1/23 16:10
 * email: 240336124@qq.com
 * version: 1.0
 */
public class HandlerPoster extends Handler implements Poster {
    private final PendingPostQueue queue;
    private final int maxMillisInsideHandleMessage;
    private boolean handlerActive;

    protected HandlerPoster(Looper looper, int maxMillisInsideHandleMessage) {
        super(looper);
        this.maxMillisInsideHandleMessage = maxMillisInsideHandleMessage;
        queue = new PendingPostQueue();
    }

    @Override
    public void handleMessage(Message msg) {
        boolean rescheduled = false;
        try {
            long started = SystemClock.uptimeMillis();
            while (true) {
                PendingPost pendingPost = queue.poll();
                if (pendingPost == null) {
                    synchronized (this) {
                        // Check again, this time in synchronized
                        pendingPost = queue.poll();
                        if (pendingPost == null) {
                            handlerActive = false;
                            return;
                        }
                    }
                }

                pendingPost.routerAction.invokeAction(pendingPost.context, pendingPost.params);
                pendingPost.releasePendingPost();

                long timeInMethod = SystemClock.uptimeMillis() - started;
                if (timeInMethod >= maxMillisInsideHandleMessage) {
                    if (!sendMessage(obtainMessage())) {
                        throw new RuntimeException("Could not send handler message");
                    }
                    rescheduled = true;
                    return;
                }
            }
        } finally {
            handlerActive = rescheduled;
        }
    }

    @Override
    public void enqueue(IRouterAction routerAction, Context context, Map<String, Object> params) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(routerAction, context, params);
        synchronized (this) {
            queue.enqueue(pendingPost);
            if (!handlerActive) {
                handlerActive = true;
                if (!sendMessage(obtainMessage())) {
                    throw new RuntimeException("Could not send handler message");
                }
            }
        }
    }
}
