package com.drouter.api.utils;

import com.drouter.api.interceptor.ActionInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * description:
 * author: Darren on 2018/1/24 16:57
 * email: 240336124@qq.com
 * version: 1.0
 */
public class MapUtils {

    public static List<ActionInterceptor> getInterceptorClasses(Map<Integer, ActionInterceptor> map) {
        List<ActionInterceptor> list = new ArrayList();

        for (Object key : map.keySet()) {
            list.add(map.get(key));
        }

        return list;
    }
}
