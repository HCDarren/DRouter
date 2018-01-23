package com.drouter.api.result;

/**
 * description: 路由的返回结果
 * author: Darren on 2018/1/22 10:35
 * email: 240336124@qq.com
 * version: 1.0
 */
public class RouterResult {
    public static final int SUCCEED_CODE = 0x000011;
    public static final int ERROR_CODE = 0x000022;
    private String msg;
    private int code = SUCCEED_CODE;
    private Object object;

    public RouterResult() {

    }

    public RouterResult(int code) {
        this.code = code;
    }

    public RouterResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RouterResult(int code, String msg, Object object) {
        this.code = code;
        this.msg = msg;
        this.object = object;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * 返回是否成功
     *
     * @return
     */
    public boolean isSucceed() {
        return code == SUCCEED_CODE;
    }
}
