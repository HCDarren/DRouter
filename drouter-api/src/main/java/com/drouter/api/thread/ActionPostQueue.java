package com.drouter.api.thread;

/**
 * description:
 * author: Darren on 2018/1/23 16:13
 * email: 240336124@qq.com
 * version: 1.0
 */
public class ActionPostQueue {
    private ActionPost head;
    private ActionPost tail;

    synchronized void enqueue(ActionPost pendingPost) {
        if (pendingPost == null) {
            throw new NullPointerException("null cannot be enqueued");
        }
        if (tail != null) {
            tail.next = pendingPost;
            tail = pendingPost;
        } else if (head == null) {
            head = tail = pendingPost;
        } else {
            throw new IllegalStateException("Head present, but no tail");
        }
        notifyAll();
    }

    synchronized ActionPost poll() {
        ActionPost pendingPost = head;
        if (head != null) {
            head = head.next;
            if (head == null) {
                tail = null;
            }
        }
        return pendingPost;
    }

    synchronized ActionPost poll(int maxMillisToWait) throws InterruptedException {
        if (head == null) {
            wait(maxMillisToWait);
        }
        return poll();
    }
}
