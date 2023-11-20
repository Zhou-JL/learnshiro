package com.zhoujl.learnshiro.service;

import com.zhoujl.learnshiro.eneity.User;
import com.zhoujl.learnshiro.util.Result;

import java.util.List;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2023-11-16 10:17
 * @see: com.zhoujl.learnshiro.service
 * @Version: 1.0
 */
public interface UserService {
    //获取用户信息
    User getUserInfoByName(String name);
    User getUserInfoByPhone(String name);
    List<String> getUserRoleInfo(String principal);

    Result login(String username, String password, Boolean rememberMe );
}
