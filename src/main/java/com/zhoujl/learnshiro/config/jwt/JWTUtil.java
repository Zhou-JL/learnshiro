package com.zhoujl.learnshiro.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2023-11-17 9:22
 * @see: com.zhoujl.learnshiro.util
 * @Version: 1.0
 */
public class JWTUtil {
    //token有效时长  半个小时
    private static final long EXPIRE=30*60*1000L;
    //token的密钥
    private static final String SECRET="jwt+shiro";


    public static String createToken(String username,Long current) {
        //token过期时间
        Date date=new Date(current+EXPIRE);

        //jwt的header部分
        Map<String ,Object> map=new HashMap<>();
        map.put("alg","HS256");
        map.put("typ","JWT");

        //使用jwt的api生成token
        String token= null;//签名
        token = JWT.create()
                .withHeader(map)
                .withClaim("username", username)//私有声明
                .withClaim("current",current)//当前时间截点
                .withExpiresAt(date)//过期时间
                .withIssuedAt(new Date())//签发时间
                .sign(Algorithm.HMAC256(SECRET));
        return token;
    }

    //校验token的有效性，1、token的header和payload是否没改过；2、没有过期
    public static boolean verify(String token){
        try {
            //解密
            JWTVerifier verifier=JWT.require(Algorithm.HMAC256(SECRET)).build();
            verifier.verify(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    //无需解密也可以获取token的信息
    public static String getUsername(String token){
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    //获取过期时间
    public static Long getExpire(String token){
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("current").asLong();
        }catch (Exception e){
            return null;
        }
    }
}
