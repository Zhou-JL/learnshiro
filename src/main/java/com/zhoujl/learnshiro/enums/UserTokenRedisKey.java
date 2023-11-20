package com.zhoujl.learnshiro.enums;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2023-11-16 16:43
 * @see: com.zhoujl.learnshiro.enums
 * @Version: 1.0
 *
 * 未用到
 */
@Getter
public enum UserTokenRedisKey {
    USER_TOKEN("userToken:", TimeUnit.MINUTES,60);
    UserTokenRedisKey(String prefix, TimeUnit unit, int expireTime){
        this.prefix = prefix;
        this.unit = unit;
        this.expireTime = expireTime;
    }
    public String getRealKey(String key){
        return this.prefix+key;
    }
    private String prefix;
    private TimeUnit unit;
    private int expireTime;
}
