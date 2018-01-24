package com.drouter.api.result;

/**
 * description: 路由的返回结果
 * author: Darren on 2018/1/22 10:35
 * email: 240336124@qq.com
 * version: 1.0
 */
public class RouterResult {
    static final int SUCCEED_CODE = 0x000011;
    static final int ERROR_CODE = 0x000022;
    private String msg;
    private int code;
    private Object object;

    public int getCode() {
        return code;
    }

    private RouterResult(Builder builder) {
        this.code = builder.code;
        this.msg = builder.msg;
        this.object = builder.object;
    }

    @Override
    public String toString() {
        return super.toString() + "{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", object=" + object +
                '}';
    }

    public String getMsg() {
        return msg;
    }

    public Object getObject() {
        return object;
    }

    /**
     * 返回是否成功
     *
     * @return
     */
    public boolean isSucceed() {
        return code == SUCCEED_CODE;
    }


    public static class Builder {
        int code = SUCCEED_CODE;
        String msg;
        Object object;

        public Builder error() {
            this.code = ERROR_CODE;
            return this;
        }

        public Builder success() {
            this.code = SUCCEED_CODE;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder object(Object object) {
            this.object = object;
            return this;
        }

        public RouterResult build() {
            return new RouterResult(this);
        }
    }
}
