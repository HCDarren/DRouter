package com.drouter.api.thread;

import android.content.Context;

import com.drouter.api.action.IRouterAction;

import java.util.Map;

/**
 * description: 处理异步
 * author: Darren on 2018/1/23 16:46
 * email: 240336124@qq.com
 * version: 1.0
 */
public class AsyncPoster implements Runnable, Poster {

    private final PendingPostQueue queue;

    AsyncPoster() {
        queue = new PendingPostQueue();
    }

    @Override
    public void run() {
        PendingPost pendingPost = queue.poll();
        if (pendingPost == null) {
            throw new IllegalStateException("No pending post available");
        }
        pendingPost.routerAction.invokeAction(pendingPost.context, pendingPost.params);
        pendingPost.releasePendingPost();
    }

    @Override
    public void enqueue(IRouterAction routerAction, Context context, Map<String, Object> params) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(routerAction, context, params);
        queue.enqueue(pendingPost);
        PosterSupport.getExecutorService().execute(this);
    }
}
