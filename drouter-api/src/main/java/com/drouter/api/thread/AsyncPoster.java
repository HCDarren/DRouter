package com.drouter.api.thread;

import com.drouter.api.action.IRouterAction;
import com.drouter.api.extra.ActionWrapper;
import com.drouter.api.result.RouterResult;

/**
 * description: 处理异步
 * author: Darren on 2018/1/23 16:46
 * email: 240336124@qq.com
 * version: 1.0
 */
public class AsyncPoster implements Runnable, Poster {

    private final ActionPostQueue queue;

    AsyncPoster() {
        queue = new ActionPostQueue();
    }

    @Override
    public void run() {
        ActionPost actionPost = queue.poll();
        if (actionPost == null) {
            throw new IllegalStateException("No pending post available");
        }

        ActionWrapper actionWrapper = actionPost.actionWrapper;
        IRouterAction routerAction = actionWrapper.getRouterAction();
        RouterResult routerResult = routerAction.invokeAction(actionPost.context, actionPost.params);
        actionPost.actionCallback.onResult(routerResult);

        actionPost.releasePendingPost();
    }

    @Override
    public void enqueue(ActionPost actionPost) {
        queue.enqueue(actionPost);
        PosterSupport.getExecutorService().execute(this);
    }
}
