package com.zhoujl.learnshiro.config.shiro;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.zhoujl.learnshiro.config.jwt.JwtToken;
import com.zhoujl.learnshiro.config.redis.RedisUtil;
import com.zhoujl.learnshiro.eneity.User;
import com.zhoujl.learnshiro.service.UserService;
import com.zhoujl.learnshiro.config.jwt.JWTUtil;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2023-11-17 9:27
 * @see: com.zhoujl.learnshiro.config.shiro
 * @Version: 1.0
 */
@Component
public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;

    //根据token判断此Authenticator是否使用该realm
    //必须重写不然shiro会报错
    @Override
    public boolean supports(AuthenticationToken token) {
        System.out.println(token instanceof JwtToken);
        return token instanceof JwtToken;
    }





    /** * 只有当需要检测用户权限的时候才会调用此方法，例如@RequiresRoles,@RequiresPermissions之类的 */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("授权~~~~~");
        String token=principals.toString();
        String username= JWTUtil.getUsername(token);
        //User user=userService.getUser(username);
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        //查询数据库来获取用户的角色
        //info.addRole(user.getRoles());
        //查询数据库来获取用户的权限
        //info.addStringPermission(user.getPermission());
        return info;
    }


    /** * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可，在需要用户认证和鉴权的时候才会调用 */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("认证~~~~~~~");
        String jwt= (String) token.getCredentials();
        String username= null;
        try {
            username= JWTUtil.getUsername(jwt);
        }catch (Exception e){
            throw new AuthenticationException("token非法，不是规范的token，可能被篡改了，或者过期了");
        }
        if (username==null){
            throw new AuthenticationException("token中无用户名");
        }
        User user=userService.getUserInfoByName(username);
        if (user==null){
            throw new AuthenticationException("该用户不存在");
        }
        //开始认证，只要AccessToken没有过期，或者refreshToken的时间节点和AccessToken一致即可
        if (redisUtil.hasKey(username)){
            //判断AccessToken有无过期
            System.out.println("判断AccessToken有无过期:"+!JWTUtil.verify(jwt));
            if (!JWTUtil.verify(jwt)){
                throw new TokenExpiredException("token认证失效，token过期，重新登陆");
            }else {
                //判断AccessToken和refreshToken的时间节点是否一致
                long current= (long) redisUtil.get(username);
                if (current==JWTUtil.getExpire(jwt)){
                    return new SimpleAuthenticationInfo(jwt,jwt, ByteSource.Util.bytes("salt"),getName());
                }else{
                    throw new AuthenticationException("token已经失效，请重新登录！");
                }
            }
        }else{
            throw new AuthenticationException("token过期或者Token错误！！");
        }
    }
}
