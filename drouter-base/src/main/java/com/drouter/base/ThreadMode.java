package com.drouter.base;

/**
 * description: thanks EventBus
 * author: Darren on 2018/1/23 08:57
 * email: 240336124@qq.com
 * version: 1.0
 */
public enum ThreadMode {
    /**
     * Action will be called directly in the same thread, which is posting the event. This is the default. Event delivery
     * implies the least overhead because it avoids thread switching completely. Thus this is the recommended mode for
     * simple tasks that are known to complete in a very short time without requiring the main thread. Event handlers
     * using this mode must return quickly to avoid blocking the posting thread, which may be the main thread.
     */
    POSTING,

    /**
     * On Android, action will be called in Android's main thread (UI thread). If the posting thread is
     * the main thread, action methods will be called directly, blocking the posting thread. Otherwise the event
     * is queued for delivery (non-blocking). Action using this mode must return quickly to avoid blocking the main thread.
     * If not on Android, behaves the same as {@link #POSTING}.
     */
    MAIN,

    /**
     * On Android, action will be called in a background thread. If posting thread is not the main thread, action methods
     * will be called directly in the posting thread. If the posting thread is the main thread, EventBus uses a single
     * background thread, that will deliver all its events sequentially. Action using this mode should try to
     * return quickly to avoid blocking the background thread. If not on Android, always uses a background thread.
     */
    BACKGROUND,

    /**
     * Action will be called in a separate thread. This is always independent from the posting thread and the
     * main thread. Posting events never wait for action methods using this mode. Action methods should
     * use this mode if their execution might take some time, e.g. for network access. Avoid triggering a large number
     * of long running asynchronous action methods at the same time to limit the number of concurrent threads. EventBus
     * uses a thread pool to efficiently reuse threads from completed asynchronous action notifications.
     */
    ASYNC
}
