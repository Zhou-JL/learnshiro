package com.zhoujl.learnshiro.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhoujl.learnshiro.config.ExceptionHandle.RRException;
import com.zhoujl.learnshiro.config.jwt.JWTUtil;
import com.zhoujl.learnshiro.config.redis.RedisUtil;
import com.zhoujl.learnshiro.eneity.User;
import com.zhoujl.learnshiro.mapper.UserMapper;
import com.zhoujl.learnshiro.service.UserService;
import com.zhoujl.learnshiro.util.Result;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2023-11-16 10:17
 * @see: com.zhoujl.learnshiro.service.serviceImpl
 * @Version: 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public User getUserInfoByName(String name) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, name));
        return user;
    }

    @Override
    public User getUserInfoByPhone(String phone) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        return user;
    }

    @Override
    public List<String> getUserRoleInfo(String principal) {
        return userMapper.getUserRoleInfoMapper(principal);
    }

    @Override
    public Result login(String account, String password, Boolean rememberMe) {
        System.out.println("进入");
        //调用login方法进行登陆
        User userInfoByName = this.getUserInfoByName(account);
        User userInfoByPhone = this.getUserInfoByPhone(account);
        if (userInfoByName == null && userInfoByPhone == null) {
            throw new RRException("用户不存在");
        }
        User user = userInfoByName != null ? userInfoByName:userInfoByPhone;
        if (!user.getPassword().equals(new Md5Hash(password, "salt").toString())) {
            System.out.println("jjj");
            throw new RRException("密码错误");
        }
        long currentTimeMillis = System.currentTimeMillis();
        String token= JWTUtil.createToken(user.getUsername(),currentTimeMillis);
        redisUtil.set(user.getUsername(),currentTimeMillis,60*30);       //半个小时
        return Result.success(200,"登陆成功",token);
    }
}
