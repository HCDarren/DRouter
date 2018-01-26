package com.drouter.api.thread;

import android.content.Context;

import com.drouter.api.extra.ActionWrapper;
import com.drouter.api.result.ActionCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * description:
 * author: Darren on 2018/1/23 16:11
 * email: 240336124@qq.com
 * version: 1.0
 */
public final class ActionPost {
    private final static List<ActionPost> pendingPostPool = new ArrayList<ActionPost>();

    public Context context;
    public ActionWrapper actionWrapper;
    public Map<String, Object> params;
    public ActionCallback actionCallback;
    ActionPost next;

    private ActionPost(ActionWrapper actionWrapper, Context context, Map<String, Object> params, ActionCallback actionCallback) {
        this.context = context;
        this.actionWrapper = actionWrapper;
        this.params = params;
        this.actionCallback = actionCallback;
    }

    public static ActionPost obtainActionPost(ActionWrapper actionWrapper, Context context, Map<String, Object> params, ActionCallback actionCallback) {
        synchronized (pendingPostPool) {
            int size = pendingPostPool.size();
            if (size > 0) {
                ActionPost actionPost = pendingPostPool.remove(size - 1);
                actionPost.context = context;
                actionPost.actionWrapper = actionWrapper;
                actionPost.params = params;
                actionPost.next = null;
                actionPost.actionCallback = actionCallback;
                return actionPost;
            }
        }
        return new ActionPost(actionWrapper, context, params, actionCallback);
    }

    public void releasePendingPost() {
        this.context = null;
        this.actionWrapper = null;
        this.next = null;
        this.actionCallback = null;
        synchronized (pendingPostPool) {
            // Don't let the pool grow indefinitely
            if (pendingPostPool.size() < 10000) {
                pendingPostPool.add(this);
            }
        }
    }
}
