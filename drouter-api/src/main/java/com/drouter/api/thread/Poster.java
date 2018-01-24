package com.drouter.api.thread;

/**
 * description:
 * author: Darren on 2018/1/23 16:05
 * email: 240336124@qq.com
 * version: 1.0
 */
public interface Poster {
    void enqueue(ActionPost actionPost);
}
