package com.zhoujl.learnshiro.config.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.zhoujl.learnshiro.config.redis.RedisUtil;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2023-11-17 9:26
 * @see: com.zhoujl.learnshiro.config.jwt
 * @Version: 1.0
 */
public class JWTFilter extends BasicHttpAuthenticationFilter {


    //是否允许访问，如果带有 token，则对 token 进行检查，否则直接通过
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        //判断请求的请求头是否带上 "Token"
        System.out.println("isAccessAllowed");
        if (isLoginAttempt(request, response)){
            //如果存在，则进入 executeLogin 方法执行登入，检查 token 是否正确
            try {
                executeLogin(request, response);
                return true;

            }catch (Exception e){
                /* *注意这里捕获的异常其实是在Realm抛出的，但是由于executeLogin（）方法抛出的异常是从login（）来的， * login抛出的异常类型是AuthenticationException，所以要去获取它的子类异常才能获取到我们在Realm抛出的异常类型。 * */
                System.out.println("刷新token");
                String msg=e.getMessage();
                Throwable cause = e.getCause();
                if (cause!=null&&cause instanceof TokenExpiredException){
                    //AccessToken过期，尝试去刷新token
                    String result=refreshToken(request, response);
                    if (result.equals("success")){
                        System.out.println("request.equals(\"success\")");
                        return true;
                    }
                    msg=result;
                }
                responseError(response,msg);
            }
        }
        //如果请求头不存在 Token，则可能是执行登陆操作或者是游客状态访问，无需检查 token，直接返回 true
        return false;  //返回false走 onAccessDenied
    }


    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        System.out.println("没有token");
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        //设置编码，否则中文字符在重定向时会变为空字符串
        String message = URLEncoder.encode("noPermission", "UTF-8");
        httpServletResponse.sendRedirect("/unauthorized/" + message);
        return false;
    }

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req= (HttpServletRequest) request;
        String token=req.getHeader("Authorization");
        return token !=null;
    }
    /* * executeLogin实际上就是先调用createToken来获取token，这里我们重写了这个方法，就不会自动去调用createToken来获取token * 然后调用getSubject方法来获取当前用户再调用login方法来实现登录 * 这也解释了我们为什么要自定义jwtToken，因为我们不再使用Shiro默认的UsernamePasswordToken了。 * */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        System.out.println("executeLogin");
        HttpServletRequest req= (HttpServletRequest) request;
        String token=req.getHeader("Authorization");
        JwtToken jwt=new JwtToken(token);
        //交给自定义的realm对象去登录，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(jwt);
        return true;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        System.out.println("preHandle");
        HttpServletRequest req= (HttpServletRequest) request;
        HttpServletResponse res= (HttpServletResponse) response;
        res.setHeader("Access-control-Allow-Origin",req.getHeader("Origin"));
        res.setHeader("Access-control-Allow-Methods","GET,POST,OPTIONS,PUT,DELETE");
        res.setHeader("Access-control-Allow-Headers",req.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (req.getMethod().equals(RequestMethod.OPTIONS.name())) {
            res.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /** * 将非法请求跳转到 /unauthorized/** */
    private void responseError(ServletResponse response, String message) {
        System.out.println("responseError");

        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            //设置编码，否则中文字符在重定向时会变为空字符串
            message = URLEncoder.encode(message, "UTF-8");
            httpServletResponse.sendRedirect("/unauthorized/" + message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    /* * 这里的getBean是因为使用@Autowired无法把RedisUtil注入进来 * 这样自动去注入当使用的时候是未NULL，是注入不进去了。通俗的来讲是因为拦截器在spring扫描bean之前加载所以注入不进去。 * * 解决的方法： * 可以通过已经初始化之后applicationContext容器中去获取需要的bean. * */
    public <T> T getBean(Class<T> clazz,HttpServletRequest request){
        WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        return applicationContext.getBean(clazz);
    }

    //刷新token
    private String refreshToken(ServletRequest request,ServletResponse response) {
        System.out.println("refreshToken");

        HttpServletRequest req= (HttpServletRequest) request;
        RedisUtil redisUtil=getBean(RedisUtil.class,req);
        //获取传递过来的accessToken
        String accessToken=req.getHeader("Authorization");
        //获取token里面的用户名
        String username= JWTUtil.getUsername(accessToken);
        System.out.println("username"+username);
        //判断refreshToken是否过期了，过期了那么所含的username的键不存在
        System.out.println("redisUtil.hasKey(username)"+redisUtil.hasKey(username));
        if (redisUtil.hasKey(username)){
            //判断refresh的时间节点和传递过来的accessToken的时间节点是否一致，不一致校验失败
            long current= (long) redisUtil.get(username);
            if (current==JWTUtil.getExpire(accessToken)){
                //获取当前时间节点
                long currentTimeMillis = System.currentTimeMillis();
                //生成刷新的token
                String token=JWTUtil.createToken(username,currentTimeMillis);
                //刷新redis里面的refreshToken,过期时间是30min
                redisUtil.set(username,currentTimeMillis,30*60);
                //再次交给shiro进行认证
                JwtToken jwtToken=new JwtToken(token);
                try {
                    getSubject(request, response).login(jwtToken);
                    // 最后将刷新的AccessToken存放在Response的Header中的Authorization字段返回
                    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                    httpServletResponse.setHeader("Authorization", token);
                    httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
                    return "success";
                }catch (Exception e){
                    return e.getMessage();
                }
            }
        }
        return "token认证失效，token过期，重新登陆";
    }



}
