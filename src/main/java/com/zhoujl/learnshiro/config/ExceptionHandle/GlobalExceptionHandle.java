package com.zhoujl.learnshiro.config.ExceptionHandle;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.zhoujl.learnshiro.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;


/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2023-11-17 9:33
 * @see: com.zhoujl.learnshiro.config.ExceptionHandle
 * @Version: 1.0
 */
//捕获全局异常的处理
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandle {

    // 捕捉shiro的异常
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public Result handle401(ShiroException e) {
        return Result.fail(401, e.getMessage(), null);
    }

    // 捕捉未登录的异常
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthenticatedException.class)
    public Result handle401(UnauthenticatedException e) {
        System.out.println(e.getMessage());
        return Result.fail(401, "你还没有登录", null);
    }

    // 捕捉没有相应的权限或者角色的异常
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public Result handle401(UnauthorizedException e) {
        System.out.println(e.getMessage());
        return Result.fail(401, "你没有权限访问" + e.getMessage(), null);
    }


    /**
     * @Validated 校验错误异常处理
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e) throws IOException {
// log.error("运行时异常:-------------->",e);
        BindingResult bindingResult = e.getBindingResult();
        //这一步是把异常的信息最简化
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();
        return Result.fail(HttpStatus.BAD_REQUEST.value(), objectError.getDefaultMessage(), null);
    }

    /**
     * 处理Assert的异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e) throws IOException {
        log.error("Assert异常:-------------->{}",e.getMessage());
        return Result.fail(400, e.getMessage(), null);
    }


    //运行时错误处理
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public Result handle(RuntimeException e) {
        return Result.fail(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = TokenExpiredException.class)
    public Result handler(TokenExpiredException e) throws IOException {
        return Result.fail(HttpStatus.BAD_REQUEST.value(), "token已经过期，请重新登录", null);
    }

    /**
     * 处理自定义的业务异常，平台异常捕获
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = RRException.class)
    public Result RRExceptionHandler(RRException e){
        log.error("发生业务异常！原因是：{}，{}", e.getCode(),e.getMessage());
        return Result.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
    }
}
