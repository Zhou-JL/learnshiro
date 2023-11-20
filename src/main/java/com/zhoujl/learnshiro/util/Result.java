package com.zhoujl.learnshiro.util;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2023-11-17 9:31
 * @see: com.zhoujl.learnshiro.util
 * @Version: 1.0
 */
@Data
public class Result implements Serializable {

    private int code; // 200是正常，非200表示异常
    private String msg;
    private Object data;

    public static Result success(Object data) {
        return success(200, "操作成功", data);
    }

    public static Result success(int code, String msg, Object data) {
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static Result fail(String msg) {
        return fail(400, msg, null);
    }

    public static Result fail(String msg, Object data) {
        return fail(400, msg, data);
    }

    public static Result fail(int code, String msg, Object data) {
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

}
