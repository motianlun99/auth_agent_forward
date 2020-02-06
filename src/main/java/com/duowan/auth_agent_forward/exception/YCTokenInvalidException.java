package com.duowan.auth_agent_forward.exception;

/**
 * @Author sj
 * @create 2020/2/6 15:07
 */

public class YCTokenInvalidException extends YCTokenException {
    private static final long serialVersionUID = -3749384174130728957L;
    private final int code = 10002;

    public YCTokenInvalidException() {
        super("token signature invalid");
    }
}
