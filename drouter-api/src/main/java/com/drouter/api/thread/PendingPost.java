package com.drouter.api.thread;

import android.content.Context;

import com.drouter.api.action.IRouterAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * description:
 * author: Darren on 2018/1/23 16:11
 * email: 240336124@qq.com
 * version: 1.0
 */
final class PendingPost {
    private final static List<PendingPost> pendingPostPool = new ArrayList<PendingPost>();

    Context context;
    IRouterAction routerAction;
    Map<String, Object> params;
    PendingPost next;

    private PendingPost(IRouterAction routerAction, Context context, Map<String, Object> params) {
        this.context = context;
        this.routerAction = routerAction;
        this.params = params;
    }

    static PendingPost obtainPendingPost(IRouterAction routerAction, Context context, Map<String, Object> params) {
        synchronized (pendingPostPool) {
            int size = pendingPostPool.size();
            if (size > 0) {
                PendingPost pendingPost = pendingPostPool.remove(size - 1);
                pendingPost.context = context;
                pendingPost.routerAction = routerAction;
                pendingPost.params = params;
                pendingPost.next = null;
                return pendingPost;
            }
        }
        return new PendingPost(routerAction, context, params);
    }

    void releasePendingPost() {
        this.context = null;
        this.routerAction = null;
        this.next = null;
        synchronized (pendingPostPool) {
            // Don't let the pool grow indefinitely
            if (pendingPostPool.size() < 10000) {
                pendingPostPool.add(this);
            }
        }
    }
}
