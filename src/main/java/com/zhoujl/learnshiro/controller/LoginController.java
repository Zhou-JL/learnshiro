package com.zhoujl.learnshiro.controller;

import com.zhoujl.learnshiro.config.jwt.JWTUtil;
import com.zhoujl.learnshiro.config.redis.RedisUtil;
import com.zhoujl.learnshiro.service.UserService;
import com.zhoujl.learnshiro.util.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2023-11-17 9:35
 * @see: com.zhoujl.learnshiro.controller
 * @Version: 1.0
 */
@RestController
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/login")
    public Result login(@RequestParam String account, @RequestParam String password,@RequestParam(defaultValue = "false")boolean rememberMe){
        return userService.login(account,password,rememberMe);
    }

    /**
     * 设置无权限返回接口，当没有传 Authorization的情况下返回，，  在 JWTFilter中的onAccessDenied中设置
     * @param message
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(path = "/unauthorized/{message}")
    public Result unauthorized(@PathVariable String message) throws UnsupportedEncodingException {
        System.out.println("这里");
        return Result.fail(message);
    }


    /**
     * 退出
     * @return
     */
    @DeleteMapping("/logout")
    @RequiresAuthentication
    public Result logout(HttpServletRequest request){
        String token=request.getHeader("Authorization");
        String username=JWTUtil.getUsername(token);
        redisUtil.del(username);
        SecurityUtils.getSubject().logout();
        return Result.success(null);
    }


    /**
     * 获取当前用户
     * @return
     */
    @GetMapping("/getUserInfo")
    public Result getUserInfo() {
        return Result.success("200");
    }


    /**
     * 获取当前用户
     * @return
     */
    @GetMapping("/getCurrentName")
    public Result getCurrentName(){
        String jwtToken = SecurityUtils.getSubject().getPrincipal().toString();
        String username = JWTUtil.getUsername(jwtToken);
        return Result.success(username);
    }
}
