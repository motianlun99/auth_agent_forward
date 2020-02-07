package com.duowan.auth_agent_forward.exception;

/**
 * @Author sj
 * @create 2020/2/6 15:07
 */

public class YCTokenInvalidException extends YCTokenException {
    public YCTokenInvalidException() {
        super("token signature invalid");
    }
}
